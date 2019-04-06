package com.tuvv.tword.model.source

import com.tuvv.tword.model.Meaning
import com.tuvv.tword.model.TWord
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TWordContent @Inject constructor(
        val twordAPISource: TWordDataSource
)  {

    private var lastFiltering: String = ""
    private var lastResult: List<TWord> = ArrayList()
    private var meaningMap: MutableMap<Int, Meaning> = HashMap()

    fun getList(searchText: String, force:Boolean = false): Flowable<List<TWord>> {
        //don't repeat the same request (our service is not dynamical)
        if (lastFiltering.equals(searchText) && !force) {

            return Flowable
                    .fromIterable(lastResult)
                    .toList()
                    .toFlowable()
        } else {
            return twordAPISource.getList(searchText)
                    .doOnNext {items ->
                        synchronized(lastFiltering) { lastFiltering = searchText }
                        synchronized(lastResult) { lastResult = items }
                    }
                    .doOnError {
                        synchronized(lastFiltering) { lastFiltering = "" }
                        synchronized(lastResult) { lastResult = ArrayList() }
                    }
                    .doFinally {
                        refreshMeaningMap()
                    }
//                twordAPISource.getList(searchText, object : TWordDataSource.GetListCallback {
//                    override fun onComplited(items: List<TWord>, error: String?) {
//                        error?.let {
//                            synchronized(lastFiltering) { lastFiltering = "" }
//                            synchronized(lastResult) { lastResult = ArrayList() }
//                            throw Exception(error)
//                        } ?: let {
//                            synchronized(lastFiltering) { lastFiltering = searchText }
//                            synchronized(lastResult) { lastResult = items }
//
//                        }
//                        refreshMeaningMap()
//                        if (error != null) throw Exception(error)
//                        else Flowable
//                                .fromIterable(items)
//                                .toList()
//                                .toFlowable()
//                    }
//                })

            }

    }

    fun getItem(id: Int): Observable<Meaning> {
        return Observable.create { e ->
            val meaning = meaningMap.get(id)
            meaning?.let {
                e.onNext(meaning)
            }

            meaning?.definition?.let{e.onComplete()} ?: run {
                twordAPISource.getItem(id)
                        .subscribe(
                                {e.onNext(it); e.onComplete()},
                                {e.onError(it)}
                        )
            }
        }
    }

//    fun getItem(id: Int, callbackBriefInfo: TWordDataSource.GetItemCallback, callbackMoreInfo: TWordDataSource.GetItemCallback) {
//        meaningMap.get(id)?.let {
//            callbackBriefInfo.onComplited(it)
//            //check some field from item api
//            it.definition?.let { _ -> callbackMoreInfo.onComplited(it) } //we have all info
//                    ?: run {twordAPISource.getItem(id, callbackMoreInfo)} //no was item api
//        }
//                ?: run {twordAPISource.getItem(id, callbackMoreInfo) //no was anything
//                }
//    }

    fun getItems(ids: Array<Int>): Observable<Map<Int, Meaning>>{
        return Single.create<Map<Int, Meaning>> { e ->
            if (ids.isNullOrEmpty()) {
                e.onSuccess(HashMap())
            } else {
                if (meaningMap.get(ids[0])?.definition != null) {
                    e.onSuccess(meaningMap.toMap())
                } else {
                    twordAPISource.getItems(ids.joinToString(","))
                            .subscribe({e.onSuccess(it)}, {e.onError(it)})
                }
            }

        }.toObservable()
    }

//    fun getItems(ids: Array<Int>, callback: TWordDataSource.GetItemsCallback) {
//        if (ids.isNullOrEmpty()) {
//            callback.onComplited(HashMap())
//            return
//        }
//
//        if (meaningMap.get(ids[0])?.definition != null) {
//            callback.onComplited(meaningMap.toMap())
//        } else {
//            twordAPISource.getItems(ids.joinToString(","), callback)
//        }
//    }

    //aux funcs
    private fun refreshMeaningMap() {
        synchronized(meaningMap) {
            meaningMap.clear()
            callbackForMeanings(lastResult, {_, _, meaning -> meaningMap.put(meaning.id, meaning)})
        }
    }

    companion object {
        fun callbackForMeanings (items:List<TWord>, forEachMeaning: (index: Int, tword: TWord, meaning: Meaning)->Unit ) {
            var i = 0
            items.forEach { t -> t.meanings.forEach { m -> forEachMeaning(i++, t, m) } }
        }
    }


}