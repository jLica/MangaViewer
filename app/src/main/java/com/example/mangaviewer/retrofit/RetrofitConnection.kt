package com.example.mangaviewer.retrofit

import android.content.Context
import com.example.mangaviewer.R
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitConnection(context : Context) {
    val URL : String = context.resources.getString(R.string.url)
    val client = OkHttpClient.Builder().connectTimeout(1, TimeUnit.MINUTES).readTimeout(1, TimeUnit.MINUTES).build()
    val retrofit : Retrofit = Retrofit.Builder().baseUrl(URL).client(client).addConverterFactory(GsonConverterFactory.create()).build()
    val server : RetrofitInterface = retrofit.create(
        RetrofitInterface::class.java)
}