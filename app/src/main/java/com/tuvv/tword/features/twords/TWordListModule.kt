package com.tuvv.tword.features.twords

import com.tuvv.tword.di.ActivityScoped
import com.tuvv.tword.features.meaning.MeaningDetailsModule
import dagger.Binds
import dagger.Module

@Module(includes = [MeaningDetailsModule::class])
abstract class TWordListModule {

//    @FragmentScoped
//    @ContributesAndroidInjector
//    internal abstract fun twordDetailFragment(): MeaningDetailsFragment
//
//    @ActivityScoped
//    @Binds
//    internal abstract fun detailPresenter(presenter: MeaningDetailsPresenter): MeaningDetailsContract.Presenter
//
    @ActivityScoped
    @Binds
    internal abstract fun provdePresenter(presenter: TWordListPresenter): TWordListContract.Presenter

}