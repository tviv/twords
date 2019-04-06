package com.tuvv.tword.utils.sound

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.tuvv.tword.R
import com.tuvv.tword.utils.sound.MediaPlayerManager

class DummySoundPlayer(context: Context, releaseAfterPlay: Boolean = false) : MediaPlayerManager(context, releaseAfterPlay) {

    override fun playSound(url: String?) {
        with(Handler(Looper.getMainLooper())) {//to see download spinner
            postDelayed({
                playSound(R.raw.test) //for offline working
            }, 3000)
        }
    }
}