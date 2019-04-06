package com.tuvv.tword.model

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tuvv.tword.model.source.TWordDataSource
import java.lang.Exception
import java.nio.charset.Charset
import java.util.HashMap
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.random.Random


class DummyContent @Inject constructor (val context: Context, val gson: Gson) {

    enum class ContentSource {
        JSON, ADDING
    }


    val CONTENT_SOURCE = ContentSource.JSON

    val ITEM_MAP: MutableMap<Int, Meaning> = HashMap()

    fun getData(searchText: String, callback: TWordDataSource.GetListCallback ) {
        with(Handler(Looper.getMainLooper())) {
            postDelayed({
                getData(searchText)?.let { callback.onComplited(it) } ?: callback.onComplited(ArrayList(), "Errorrrr dummyyy")
            }, RESPONSE_DELAY)
        }

    }

    fun getData(searchText: String): List<TWord>? {
        if ("err".equals(searchText)) {
            return null
        }

        val result: List<TWord>
        when (CONTENT_SOURCE) {
            ContentSource.JSON ->
                when (searchText) {
                    "", "tabs" ->   result = ArrayList()
                    "t" -> {    result = createListFromJSON("list_t.json")
                        result[0].meanings[0].apply{ previewUrl = null; imageUrl = null } }
                    "ta" ->     result = createListFromJSON("list_ta.json")
                    "tab" ->    result = createListFromJSON("list_tab.json")
                    else ->     result = createListFromJSON("list_tab.json")

                }
            ContentSource.ADDING ->
                result = createByAdding()
        }
        return (result)
    }

//    fun getItem(id: Int, callback: TWordDataSource.GetItemCallback) {
//            callback.onComplited(ITEM_MAP[id]!!)
//    }
//

    fun getMoreDetailsForItem(callback: TWordDataSource.GetItemCallback) {
        with(Handler(Looper.getMainLooper())) {
            postDelayed({ callback.onComplited(createDetailsFromJSON("details_tab_1.json")!!) }, RESPONSE_DELAY * 2)
        }
    }

    fun getMoreDetailsForItems(ids:String, callback: TWordDataSource.GetItemsCallback) {
        with(Handler(Looper.getMainLooper())) {
            postDelayed({
                val result: MutableMap<Int, Meaning> = HashMap()
                ids.split(",").forEach {result.put(it.toInt(), createDetailsFromJSON("details_tab_1.json")!!)}
                callback.onComplited(result)
            }, RESPONSE_DELAY * 2)
        }
    }


    private fun createListFromJSON(fileName: String): List<TWord> {
        val data =  loadStringFromAssetFile(context, fileName)
        return gson.fromJson<List<TWord>>(data, object: TypeToken<List<TWord>>() { }.type).apply {
            //for some random images (independed from main api
            forEach { it.meanings.forEach {
                m -> m.previewUrl = "https://placeimg.com/9${Random.nextInt(0, 10).toString()}/7${Random.nextInt(0, 10).toString()}"
                m.imageUrl = "https://placeimg.com/64${Random.nextInt(0, 10).toString()}/48${Random.nextInt(0, 10).toString()}"
                ITEM_MAP.put(m.id, m)
            }
            }
        }
    }

    private fun createDetailsFromJSON(fileName: String): Meaning? {
        val data = loadStringFromAssetFile(context, fileName)
        return gson.fromJson<Array<Meaning>>(data, Array<Meaning>::class.java)?.let {
            if (it.count() > 0) it[0] else null
        }
    }



    private fun createByAdding(): List<TWord> {
        return arrayListOf(
                TWord(1, "tab", arrayListOf(
                        createMeaningItem(1,1,"прихватка", "кухонная", "tæb"),
                        createMeaningItem(2,1,"клавиша табуляции", "", "tæb"),
                        createMeaningItem(3,1,"вкладка", "", "tæb"),
                        createMeaningItem(4,1,"пилюля", "", "tæb"),
                        createMeaningItem(5,1,"вкладка", "", "tæb"),
                        createMeaningItem(6,1,"жёлтая журналистика", "", "tæb")
                )
                ),
                TWord(2, "tab key", arrayListOf<Meaning>(
                        createMeaningItem(7,2,"клавиша tab", "", "tæb kiː")
                )
                ),
                TWord(3, "control tab", arrayListOf<Meaning>(
                        createMeaningItem(8,3,"триммер", "", "kənˈtrəʊl tæb")
                )
                )
        )

    }

    private fun createMeaningItem(id: Int,
                                wordId: Int,
                                translation: String,
                                note: String,
                                transcription: String): Meaning {
        val result = Meaning(id,wordId, null, null, Meaning.TextAndNote(translation, note), transcription, "https://placeimg.com/96/7" + id.toString(), "http://lorempixel.com/640/480")
        ITEM_MAP.put(result.id, result)

        return result

    }


    companion object {

        val RESPONSE_DELAY = 2000L

        fun loadStringFromAssetFile(context: Context, fileName: String): String {
            val json: String
            try {
                val f = context.getAssets().open(fileName)

                val size = f.available()

                val buffer = ByteArray(size)

                f.read(buffer)

                f.close()

                json = String(buffer, Charset.forName("UTF-8"))


            } catch (ex: Exception) {
                ex.printStackTrace()
                return ""
            }

            return json

        }
    }

}
