package com.tuvv.tword.utils.sound

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
class SoundPlayerModule {

    @Provides
    @Inject
    fun provideSoundPlayer(context: Context): SoundPlayer {
        return MediaPlayerManager(context)
    }
}