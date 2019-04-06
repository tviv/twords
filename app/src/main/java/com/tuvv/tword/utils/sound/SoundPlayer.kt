package com.tuvv.tword.utils.sound

interface SoundPlayer {
    interface OnPlayListners {
        fun onCompletionListener()
        fun onPreparedListener()
        fun onErrorListener(error: String?)
    }

    var listners: OnPlayListners?

    fun playSound(url: String?)

    fun stop()

    fun release()
}