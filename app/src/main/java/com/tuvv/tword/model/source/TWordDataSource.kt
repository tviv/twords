package com.tuvv.tword.model.source

import com.tuvv.tword.model.Meaning
import com.tuvv.tword.model.TWord
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable

interface TWordDataSource {
    interface GetListCallback {

        fun onComplited(items: List<TWord>, error: String? = null)

    }

    interface GetItemCallback {

        fun onComplited(item: Meaning?, error: String? = null)
    }

    interface GetItemsCallback {

        fun onComplited(items: Map<Int, Meaning>?, error: String? = null)
    }

    fun getList(searchText: String): Flowable<List<TWord>>

    fun getItem(id: Int): Maybe<Meaning>

    fun getItems(ids: String): Observable<Map<Int, Meaning>>

}