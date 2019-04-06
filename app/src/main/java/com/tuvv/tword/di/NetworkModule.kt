package com.tuvv.tword.di

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
class NetworkModule {
    @Singleton
    @Provides
    @Inject
    fun providePicasso(context: Context): Picasso {
        return Picasso.Builder(context).build()
    }

    @Singleton
    @Provides
    fun provideRetrofitApi(): Retrofit {
        var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://dictionary.skyeng.ru")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit
    }

}