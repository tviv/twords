package com.tuvv.tword.model.source

import com.tuvv.tword.model.Meaning
import com.tuvv.tword.model.TWord
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TWordAPI {
    @GET("api/public/v1/words/search")
    abstract fun getData(@Query("search") search: String): Call<List<TWord>>

    @GET("api/public/v1/meanings")
    abstract fun getItem(@Query("ids") ids: Int): Call<List<Meaning>>

    @GET("api/public/v1/meanings")
    abstract fun getItems(@Query("ids") ids: String): Call<List<Meaning>>

}