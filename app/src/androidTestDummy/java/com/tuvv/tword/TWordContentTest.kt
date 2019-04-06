package com.tuvv.tword

import android.media.MediaPlayer
import android.net.Uri
import android.os.Looper
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tuvv.tword.model.TWord

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import com.tuvv.tword.model.DummyContent
import com.tuvv.tword.model.Meaning
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.logging.Handler


@RunWith(AndroidJUnit4::class)
class TWordContentTest {



    @Test
    fun loadListFromJSONToModel() {

        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        val data =  DummyContent.loadStringFromAssetFile(appContext, "list_tab.json")

        var gson: Gson
        //gsonBuilder.setDateFormat("M/d/yy hh:mm a")
        gson = GsonBuilder().create()

        val items: List<TWord> = gson.fromJson(data, Array<TWord>::class.java).toList()

        assertEquals(15, items.count())
    }

    @Test
    fun loadItemFromJSONToModel() {

        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        val data =  DummyContent.loadStringFromAssetFile(appContext, "details_tab_1.json")

        var gson: Gson
        //gsonBuilder.setDateFormat("M/d/yy hh:mm a")
        gson = GsonBuilder().create()

        val items: List<Meaning> = gson.fromJson(data, Array<Meaning>::class.java).toList()

        assertEquals("https://dmsbj0x9fxpml.cloudfront.net/c3e70c36a16ab1127254698501267d21.mp3", items.get(0).definition?.soundUrl)
        assertEquals(495, items.get(0).examplesText.count())
    }

}
