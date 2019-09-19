package ru.vladislavsumin.cams.network

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class NetworkService {
    companion object {
        //        const val BASE_HOST = "http://10.0.0.96:8080"
//        const val BASE_HOST = "http://10.0.1.97:8080"
        private const val BASE_HOST = "base_host"
        private val TAG = NetworkService::class.java.simpleName
    }

    private val mRetrofit: Retrofit

    init {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(ChangeBaseUrlInterceptor(BASE_HOST))
            .build()

        mRetrofit = Retrofit.Builder()
            .baseUrl("http://$BASE_HOST")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        Log.i(TAG, "NetworkService initialized")
    }

    fun <T> create(api: Class<T>): T {
        return mRetrofit.create(api)
    }
}