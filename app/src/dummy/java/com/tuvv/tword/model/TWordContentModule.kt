package com.tuvv.tword.model.source

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tuvv.tword.model.DummyTWordSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class TWordContentModule {

    @Singleton
    @Binds
    abstract fun provideTWordAPISource(source: DummyTWordSource): TWordDataSource

    @Module
    companion object {
        @Provides
        @JvmStatic
        fun provideGson(): Gson {
            val gsonBuilder = GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
            return gsonBuilder.create()
        }
    }

}