package com.example.picsee.repository

import com.example.picsee.api.Hit
import com.example.picsee.database.ImageDatabase
import com.example.picsee.network.ImageApiService
import com.example.picsee.network.RetrofitInstance
import retrofit2.Retrofit

/**
 * Repository connects ViewModel with the database, which means we get all the functions from query and give them to the ViewModel
 * here alse we have taken the methods from Retrofit that will be used to get the json data and give access to different urls within json.
 */
class ImageRepository(val database: ImageDatabase) {

    suspend fun searchPhotos(apiKey: String, searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForImages(apiKey, searchQuery, pageNumber)

    suspend fun insertOrUpdate(image: Hit) = database.imageDao.upsert(image)

    suspend fun delete(image: Hit) = database.imageDao.delete(image)

    fun getAllImages() = database.imageDao.getAllImages()

}