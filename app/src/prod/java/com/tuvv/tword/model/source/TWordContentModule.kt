package com.tuvv.tword.model.source

import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class TWordContentModule {

    @Singleton
    @Binds
    abstract fun provideTWordAPISource(source: TWordAPISource): TWordDataSource


}