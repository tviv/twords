package com.tuvv.tword.model

import android.content.Context
import com.tuvv.tword.model.source.TWordDataSource
import io.reactivex.*
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DummyTWordSource @Inject constructor(val context: Context) : TWordDataSource {

    @Inject
    internal lateinit var dummy: DummyContent

//    override fun getList(searchText: String): Flowable<List<TWord>> {
//
//        val data = dummy.getData(searchText)
//        if (data == null) return Flowable.error(Exception("dummy errorddd"))
//        return Flowable
//                .fromIterable(data)
//                .delay(DummyContent.RESPONSE_DELAY, TimeUnit.MILLISECONDS)
//                .toList()
//                .toFlowable()
//    }

    override fun getList(searchText: String): Flowable<List<TWord>> {
        return Single.create<List<TWord>> { e ->
            dummy.getData(searchText, object : TWordDataSource.GetListCallback {
                override fun onComplited(items: List<TWord>, error: String?) {
                    error?.let {e.onError(Exception(error))}
                            ?: run {
                                e.onSuccess(items)
                            }
                }
            } )
        }.toFlowable()
    }

    override fun getItem(id: Int): Maybe<Meaning> {
        return Maybe.create { e ->
           dummy.getMoreDetailsForItem(object : TWordDataSource.GetItemCallback {
               override fun onComplited(item: Meaning?, error: String?) {
                   error?.let {e.onError(Exception(error))}
                           ?: run {
                               item?.let {e.onSuccess(item)}
                               e.onComplete()
                           }
               }
           })
        }
    }

    override fun getItems(ids: String): Observable<Map<Int, Meaning>> {
        return Maybe.create<Map<Int, Meaning>> { e ->
            dummy.getMoreDetailsForItems(ids, object : TWordDataSource.GetItemsCallback {
                override fun onComplited(items: Map<Int, Meaning>?, error: String?) {
                    error?.let {e.onError(Exception(error))}
                            ?: run {
                                items?.let {e.onSuccess(it)}
                                e.onComplete()
                            }
                }
            })
        }.toObservable()
    }


//    fun getList(searchText: String, callback: TWordDataSource.GetListCallback) {
//        dummy.getData(searchText, callback)
//    }
//
//    fun getItem(id: Int, callback: TWordDataSource.GetItemCallback) {
//        //dummy.getItem(id, callbackBriefInfo)
//        dummy.getMoreDetailsForItem(callback)
//    }
//
//    fun getItems(ids: String, callback: TWordDataSource.GetItemsCallback) {
//        dummy.getMoreDetailsForItems(ids, callback)
//    }



}