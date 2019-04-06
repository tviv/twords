package com.tuvv.tword.utils

import android.content.Context
import android.support.test.InstrumentationRegistry
import com.tuvv.tword.utils.settings.Settings
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mockito
import java.util.*

class SettingsTest {

    lateinit var context: Context
    lateinit var pref: Settings

    @Before
    fun setVars() {
        context = InstrumentationRegistry.getTargetContext()

        //prepare storage
        val sharedPrefKey = "shared_set_test"
        context.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE)
                .edit().clear().commit()

        pref = Settings(context, sharedPrefKey)

    }

    @Test
    fun getWasTutorial() {
        assertEquals(false, pref.wasTutorial)
    }

    @Test
    fun setWasTutorial() {
        pref.wasTutorial = true
        assertEquals(true, pref.wasTutorial)
    }

    @Test
    fun isRussianTranslation() {
        setLocale("ru", "RU")
        assertEquals(true, pref.isRussianTranslation)

        setLocale("es", "ES")
        assertEquals(false, pref.isRussianTranslation)
    }



    private fun setLocale(language: String, country: String) {
        val locale = Locale(language, country)
        // here we update locale for date formatters
        Locale.setDefault(locale)
        // here we update locale for app resources
        val res = context.resources
        val config = res.getConfiguration()
        config.setLocale(locale)
        context.createConfigurationContext(config)
    }
}