package com.tuvv.tword.di

import com.tuvv.tword.utils.settings.SettingsActivity
import com.tuvv.tword.features.meaning.MeaningDetailsActivity
import com.tuvv.tword.features.meaning.MeaningDetailsModule
import com.tuvv.tword.features.twords.TWordListActivity
import com.tuvv.tword.features.twords.TWordListModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = [TWordListModule::class])
    abstract fun twordListActivity(): TWordListActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [MeaningDetailsModule::class])
    abstract fun twordDetailActivity(): MeaningDetailsActivity

    @ActivityScoped
    @ContributesAndroidInjector()
    abstract fun twordListActivity1(): SettingsActivity

}
