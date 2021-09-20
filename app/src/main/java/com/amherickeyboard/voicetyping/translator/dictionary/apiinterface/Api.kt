package com.amherickeyboard.voicetyping.translator.dictionary.apiinterface

import com.amherickeyboard.voicetyping.translator.dictionary.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Api {
    private var retrofit: Retrofit? = null

    // change your base URL
    //Creating object for our interface
    @JvmStatic
    val client: ApiInterface
        get() {
            val setTimeOut = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS).build()
            // change your base URL
            if (retrofit == null) {
                retrofit =
                    Retrofit.Builder()
                        .baseUrl(Constants.dictionaryLink)
                        .addConverterFactory(GsonConverterFactory.create()).client(setTimeOut)
                        .build()
            }
            //Creating object for our interface
            return retrofit!!.create(
                ApiInterface::class.java
            ) // return the APIInterface object
        }
    private var apiInterface: ApiInterface? = null
    private const val baseURL = Constants.baseurl
    fun getInstance(): ApiInterface =
        apiInterface ?: synchronized(this)
        {
            apiInterface
                ?: x().also { apiInterface = it }
        }

    private fun x() =
        Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create()).client(
                OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS).build()
            )
            .build().create(ApiInterface::class.java)
}