package com.tuvv.tword.model.source

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import com.tuvv.tword.R
import com.tuvv.tword.model.Meaning
import com.tuvv.tword.model.TWord
import io.reactivex.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.Exception
import java.net.InetAddress
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TWordAPISource @Inject constructor(retrofit: Retrofit, private val context: Context) : TWordDataSource {

    private val api = retrofit.create(TWordAPI::class.java)

    private var listCall: Call<List<TWord>>? = null
    private var itemCall: Call<List<Meaning>>? = null


    override fun getList(searchText: String): Flowable<List<TWord>> {
        return Observable.create<List<TWord>> {e ->

            getList(searchText, object : TWordDataSource.GetListCallback {
                override fun onComplited(items: List<TWord>, error: String?) {
                    error?.let {e.onError(Exception(error))}
                            ?: run {
                                e.onNext(items)
                                e.onComplete()
                            }
                }
            }).also { e.setCancellable { it.cancel(); e.onComplete() } }

        }.toFlowable(BackpressureStrategy.LATEST)

    }

    fun getList(searchText: String, callback: TWordDataSource.GetListCallback): Call<List<TWord>> {
        if (!checkInternet()) { callback.onComplited(ArrayList(),context.getString(R.string.no_internet)) }

//        listCall?.apply {
//            if (isExecuted) cancel()
//        }

        val call = api.getData(searchText)
        call.enqueue(object : Callback<List<TWord>> {
            override fun onResponse(call: Call<List<TWord>>, response: Response<List<TWord>>) {
                callback.onComplited(response.body()!!, null)
            }

            override fun onFailure(call: Call<List<TWord>>, t: Throwable) {
                if (call.isCanceled) { return }

                //println(t.message)
                callback.onComplited(ArrayList(),t.message)
            }
        })
        listCall = call
        return call
    }

    override fun getItem(id: Int): Maybe<Meaning> {
        return Maybe.create { e->
            api.getItem(id)
                    .also { e.setCancellable { it.cancel(); e.onComplete() } }
                    .enqueue(object : Callback<List<Meaning>> {
                        override fun onResponse(call: Call<List<Meaning>>, response: Response<List<Meaning>>) {
                            response.body()?.get(0)?.let {e.onSuccess(it)}
                            e.onComplete()
                        }

                        override fun onFailure(call: Call<List<Meaning>>, t: Throwable) {
                            if (!call.isCanceled) {
                                e.onError(t)
                            }
                        }
                    })
        }
    }

//    fun getItem(id: Int, callback: TWordDataSource.GetItemCallback) {
//        itemCall?.apply {
//            if (isExecuted) cancel()
//        }
//
//        itemCall = api.getItem(id)
//        itemCall?.enqueue(object : Callback<List<Meaning>> {
//            override fun onResponse(call: Call<List<Meaning>>, response: Response<List<Meaning>>) {
//                var error: String? = null
//                val item: Meaning? = (response.body()?.get(0)).also { if (it == null) error = "empty item is gotten"  }
//                callback.onComplited(item, error)
//            }
//
//            override fun onFailure(call: Call<List<Meaning>>, t: Throwable) {
//                if (call.isCanceled) { return }
//
//                println(t.message)
//                callback.onComplited(null,t.message)
//            }
//        })
//    }

    override fun getItems(ids: String): Observable<Map<Int, Meaning>> {
        return Observable.create { e->
            api.getItems(ids)
                    .also { e.setCancellable { it.cancel(); e.onComplete() } }
                    .enqueue(object : Callback<List<Meaning>> {
                        override fun onResponse(call: Call<List<Meaning>>, response: Response<List<Meaning>>) {
                            val result: MutableMap<Int, Meaning> = HashMap()
                            response.body()?.forEach { result.put(it.id, it) }
                            e.onNext(result)
                            e.onComplete()
                        }

                        override fun onFailure(call: Call<List<Meaning>>, t: Throwable) {
                            if (!call.isCanceled) {
                                e.onError(t)
                            }
                        }
                    })
        }
    }

//    fun getItems(ids: String, callback: TWordDataSource.GetItemsCallback) {
//        itemCall?.apply {
//            if (isExecuted) cancel()
//        }
//
//        itemCall = api.getItems(ids)
//        itemCall?.enqueue(object : Callback<List<Meaning>> {
//            override fun onResponse(call: Call<List<Meaning>>, response: Response<List<Meaning>>) {
//                var error: String? = null
//                val items = (response.body()).also { if (it == null) error = "empty item is gotten"  }
//                val result: MutableMap<Int, Meaning> = HashMap()
//                items?.forEach { result.put(it.id, it) }
//                callback.onComplited(result, error)
//            }
//
//            override fun onFailure(call: Call<List<Meaning>>, t: Throwable) {
//                if (call.isCanceled) { return }
//
//                //rintln(t.message)
//                callback.onComplited(null,t.message)
//            }
//        })
//    }

    //now no any singlelton class
    private fun checkInternet(): Boolean {
        return isNetworkAvailable(context)
    }

    companion object {

        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected
        }
    }
}