package com.tuvv.tword.utils

import android.content.Context
import com.squareup.picasso.Picasso
import com.tuvv.tword.utils.settings.Settings
import com.tuvv.tword.utils.schedulers.SchedulerProvider
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Module
class UtilsModule {

    @Singleton
    @Provides
    @Inject
    fun provideSettings(context: Context): Settings {
        return Settings(context)
    }

    @Singleton
    @Provides
    @Inject
    fun provideSchedule(): SchedulerProvider {
        return SchedulerProvider.instance
    }

}