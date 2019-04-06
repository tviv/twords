package com.tuvv.tword.features.meaning

import com.tuvv.tword.di.*
import dagger.Binds
import dagger.android.ContributesAndroidInjector
import dagger.Module

@Module
abstract class MeaningDetailsModule {

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun twordDetailFragment(): MeaningDetailsFragment

    @ActivityScoped
    @Binds
    internal abstract fun detailPresenter(presenter: MeaningDetailsPresenter): MeaningDetailsContract.Presenter

//    @Module
//    companion object {
//        @Provides
//        @ActivityScoped
//        @JvmStatic internal fun provideTaskId(fragment: MeaningDetailsFragment): Int {
//            return fragment.arguments?.getInt(KEY_TASK_ID, 0) ?: 0
//        }
//    }
}