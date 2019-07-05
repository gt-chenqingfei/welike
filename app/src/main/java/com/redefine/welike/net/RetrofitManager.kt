package com.redefine.welike.net

import com.google.gson.GsonBuilder
import com.redefine.welike.business.publisher.api.bean.Topic
import com.redefine.welike.net.serializer.TopicJsonDeserializer
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitManager private constructor() {
    private val jsonDeserializer = GsonBuilder()
            .registerTypeAdapter(Topic::class.java, TopicJsonDeserializer())
            .create()

    var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(mBaseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(jsonDeserializer))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()

    companion object {
        fun getInstance() = Holder.INSTANCE

        fun init(url: String, okClient: OkHttpClient) {
            mBaseUrl = url
            client = okClient
        }

        private lateinit var mBaseUrl: String
        private lateinit var client: OkHttpClient
    }

    private object Holder {
        val INSTANCE = RetrofitManager()
    }

}




