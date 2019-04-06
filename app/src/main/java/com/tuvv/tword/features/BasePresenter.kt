package com.tuvv.tword.features

interface BasePresenter<T> {

    fun subscribe(view: T)

    fun unsubscribe()

}