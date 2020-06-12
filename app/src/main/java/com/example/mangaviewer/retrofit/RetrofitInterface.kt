package com.example.mangaviewer.retrofit

import com.example.mangaviewer.data.*
import com.example.mangaviewer.json.JsonforBookmark
import com.example.mangaviewer.json.JsonforContents
import com.example.mangaviewer.json.JsonforEpisodes
import com.example.mangaviewer.json.JsonforSearch
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitInterface {
    @POST("/post/search")
    fun getSearchedData(@Body json : JsonforSearch) : Call<ArrayList<Searched>>

    @POST("/post/episodes")
    fun getEpData(@Body json : JsonforEpisodes) : Call<Episodes>

    @POST("/post/contents")
    fun getContentsData(@Body json : JsonforContents) : Call<Contents>

    @POST("/post/updates")
    fun getUpdatedData() : Call<ArrayList<Updates>>

    @POST("/post/getBookmarks")
    fun getBookmarks() : Call<ArrayList<Searched>>

    @POST("/post/setBookmark")
    fun setBookmark(@Body json : JsonforBookmark) : Call<ResultBookmark>
}