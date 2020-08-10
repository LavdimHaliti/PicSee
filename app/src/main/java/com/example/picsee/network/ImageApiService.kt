package com.example.picsee.network

import com.example.picsee.api.ImageEnities
import com.example.picsee.util.Constants.Companion.API_KEY
import com.example.picsee.util.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class RetrofitInstance{
 companion object{

     private val retrofit by lazy {

         val logging = HttpLoggingInterceptor()
         logging.setLevel(HttpLoggingInterceptor.Level.BODY)
         val client = OkHttpClient.Builder()
             .addInterceptor(logging)
             .build()


         Retrofit.Builder()
             .addConverterFactory(GsonConverterFactory.create())
             .baseUrl(BASE_URL)
             .client(client)
             .build()
     }

     val api by lazy {
         retrofit.create(ImageApiService::class.java)
     }
 }
}

interface ImageApiService{

    @GET("/api/")
    suspend fun searchForImages(

        @Query("key")
        apiKey: String = API_KEY,

        @Query("q")
        searchQuery: String,

        @Query("page")
        pageNumber: Int = 1

    ): Response<ImageEnities>
}