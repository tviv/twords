package com.tuvv.tword.utils.sound

import android.content.ContentResolver
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import java.lang.Exception

open class MediaPlayerManager(private val context: Context, private val releaseAfterPlay: Boolean = false)
    : SoundPlayer {

    private var mediaPlayer: MediaPlayer? = null

    override var listners: SoundPlayer.OnPlayListners? = null

    override fun playSound(url: String?) {
        playSound(Uri.parse(url))
    }

    fun playSound(resId: Int) {
        val uri = with(context) {
            Uri.Builder()
                    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                    .authority(resources.getResourcePackageName(resId))
                    .appendPath(resources.getResourceTypeName(resId))
                    .appendPath(resources.getResourceEntryName(resId))
                    .build()
        }
        playSound(uri)
    }

    private var lastUri: Uri? = null

    fun playSound(uri: Uri?) {
        if (uri == null) return
        if (uri != lastUri || mediaPlayer == null) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer()
        }
        if (mediaPlayer == null) {listners?.onErrorListener("init error")}
        mediaPlayer?.reset()
        mediaPlayer?.apply {
            try {
                setDataSource(context, uri)
                lastUri = uri
                setOnCompletionListener {
                    if (releaseAfterPlay) this@MediaPlayerManager.release()
                    listners?.onCompletionListener()
                }
                setOnPreparedListener {listners?.onPreparedListener(); start()}
                setOnErrorListener { _, what, _ ->
                    listners?.onErrorListener(what.toString())
                    lastUri = null
                    true
                }
                prepareAsync()

            } catch (e: Exception) {
                lastUri = null
                release()
                e.printStackTrace()
                listners?.onErrorListener(e.toString())
            }
        }
    }

    override fun stop() {
        mediaPlayer?.let {if(it.isPlaying) it.stop() else  it.reset()}
        listners?.onCompletionListener()
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}