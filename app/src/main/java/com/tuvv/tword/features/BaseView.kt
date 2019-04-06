package com.tuvv.tword.features

enum class SoundState {
    N,D,P

//    fun isIdle(state: Int):Boolean {
//        return state == N.ordinal
//    }
}

interface BaseView {

    fun setLoadingIndicator(active: Boolean)

    fun showError(text: String? = null)

}