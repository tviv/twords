package com.tuvv.tword.di

import android.app.Application
import com.tuvv.tword.TWordApplication
import com.tuvv.tword.utils.sound.SoundPlayerModule
import com.tuvv.tword.model.source.TWordContentModule
import com.tuvv.tword.utils.UtilsModule

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component (modules = [
    TWordContentModule::class,
    NetworkModule::class,
    SoundPlayerModule::class,
    UtilsModule::class,
    ApplicationModule::class,
    ActivityBindingModule::class,
    AndroidSupportInjectionModule::class
])
public interface AppComponent : AndroidInjector<TWordApplication> {
    @Component.Builder
    interface Builder {

            @BindsInstance
            fun application(application: Application): AppComponent.Builder

        fun build(): AppComponent
    }
}