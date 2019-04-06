package com.tuvv.tword.features

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.tuvv.tword.R
import com.tuvv.tword.utils.schedulers.SchedulerProvider
import com.tuvv.tword.utils.sound.SoundPlayer
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.*
import java.util.concurrent.TimeUnit

open class GeneralMainPresenterWork (
        protected val context: Context,
        private val soundPlayer: SoundPlayer,
        val picasso: Picasso
) {


    //sound work
    private var soundDisposable: Disposable? = null

    protected fun playRemoteSound(url: String?, stateBlock: (state: SoundState)->Unit, errorBlock: (error:String?)->Unit) {
        if (url == null) return

        soundPlayer.apply {
                stateBlock(SoundState.D)
                stop()
                listners = object : SoundPlayer.OnPlayListners {
                    override fun onCompletionListener() {
                        stateBlock(SoundState.N)
                    }

                    override fun onPreparedListener() {
                        stateBlock(SoundState.P)
                    }

                    override fun onErrorListener(error: String?) {
                        stateBlock(SoundState.N)
                        errorBlock(context.getString(R.string.play_error_happend))
                    }
                }
                playSound(url)
        }
    }

//    protected fun playRemoteSound(url: String?, stateBlock: (state: SoundState)->Unit, errorBlock: (error:String?)->Unit) {
//        if (url == null) return
//
//        soundPlayer.apply {
//            Observable.create<SoundState> { e ->
//                e.onNext(SoundState.D)
//                stop()
//                listners = object : SoundPlayer.OnPlayListners {
//                    override fun onCompletionListener() {
//                        //stateBlock(SoundState.N)
//                        e.onNext(SoundState.N)
//                        e.onComplete()
//                    }
//
//                    override fun onPreparedListener() {
//                        //stateBlock(SoundState.P)
//                        e.onNext(SoundState.P)
//
//                    }
//
//                    override fun onErrorListener(error: String?) {
//                        //stateBlock(SoundState.N)
//                        e.onNext(SoundState.N)
//                        e.onError(java.lang.Exception(context.getString(R.string.play_error_happend)))
//                        //errorBlock(context.getString(R.string.play_error_happend))
//                    }
//                }
//                playSound(url)
//            }
//                    .subscribeOn(scheduler.computation())
//                    .delay(700, TimeUnit.MILLISECONDS)
//                    .observeOn(scheduler.ui())
//                    .subscribe({stateBlock(it)}
//                            ,{errorBlock(it.message)})
//        }
//    }

    protected fun stopSound() {
        soundPlayer.stop()
    }

    protected fun release() {
        soundPlayer.release()
    }

    //end sound work

    //image work

    fun getImageByUrl(imgUrl: String?, successBlock: (image: Drawable?)->Unit, errorBlock: (error:String?)->Unit) {
        val url = imgUrl ?: run {successBlock(null); return}

        ImageView(context).also {
            picasso.load(url).into(it, object : Callback {

                override fun onSuccess() {
                    successBlock(it.drawable)
                }

                override fun onError(e: Exception?) {
                    errorBlock(e?.message)
                }
            })
        }
    }

    //end image work
}