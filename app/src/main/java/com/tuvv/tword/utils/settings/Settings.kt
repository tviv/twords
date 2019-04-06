package com.tuvv.tword.utils.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.tuvv.tword.R
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Settings @Inject constructor(val context: Context, sharedPrefKey: String? = null) {
    private val prefs: SharedPreferences

    init {
        if (sharedPrefKey == null) {
            PreferenceManager.setDefaultValues(context, R.xml.pref_general, false)
            prefs = PreferenceManager.getDefaultSharedPreferences(context)
        }
        else {
            prefs = context.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE)
        }
    }

    fun edit() {
        val intent = Intent(context, SettingsActivity::class.java)
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    var wasTutorial: Boolean
        get() = !prefs.getBoolean(context.getString(R.string.show_settings_tutorialKey), true)
        @SuppressLint("ApplySharedPref")
        set(value) {
            prefs.edit().putBoolean(context.getString(R.string.show_settings_tutorialKey), !value).commit()
        }

    val isRussianTranslation: Boolean
        get() =
            prefs.getString(context.getString(R.string.settings_isRussianTranslationKey),"-1") == "1"
                    || (Locale.getDefault().language == "ru"
                    && prefs.getString(context.getString(R.string.settings_isRussianTranslationKey),"-1") == "-1")
}
