package com.example.picsee.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picsee.ImageApplication
import com.example.picsee.api.Hit
import com.example.picsee.api.ImageEnities
import com.example.picsee.repository.ImageRepository
import com.example.picsee.util.Constants.Companion.API_KEY
import com.example.picsee.util.Resource
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

/**
 * All the background work is done in the viewmodel and then handed over to the view to be displayed on the screen,
 * in this case handling response getting the database functions, checking for internet connection etc...
 */
class ImageViewModel(app: Application, val imgRepository: ImageRepository) : AndroidViewModel(app) {

    val searchImages: MutableLiveData<Resource<ImageEnities>> = MutableLiveData()
    var searchImagePage = 1
    var searchImagesNextPage: ImageEnities? = null

    fun searchImages(searchQuery: String) = viewModelScope.launch {
        searchImages.postValue(Resource.Loading())
        safeImageSearchCall(searchQuery)
    }


    private fun handleSearchImagesResponse(response: Response<ImageEnities>): Resource<ImageEnities> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchImagePage++
                if (searchImagesNextPage == null) {
                    searchImagesNextPage = resultResponse
                } else {
                    val oldImagePage = searchImagesNextPage?.hits
                    val newImagePage = resultResponse.hits
                    oldImagePage?.addAll(newImagePage)
                }
                if (searchImagesNextPage != null) {
                    return Resource.Success(resultResponse)

                }


            }
        }
        return Resource.Error(response.message())
    }

    fun saveImage(image: Hit) = viewModelScope.launch {
        imgRepository.insertOrUpdate(image)
    }

    fun getAllImages() = imgRepository.getAllImages()

    fun deleteImage(image: Hit) = viewModelScope.launch {
        imgRepository.delete(image)
    }

    private suspend fun safeImageSearchCall(searchQuery: String) {
        try {

            if (hasInternetConnection()) {
                val response = imgRepository.searchPhotos(API_KEY, searchQuery, searchImagePage)
                searchImages.postValue(handleSearchImagesResponse(response))
            } else {
                searchImages.postValue(Resource.Error("No Internet Connection"))
            }

        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchImages.postValue(Resource.Error("Network failure"))
                else -> searchImages.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    //This method can be used in future project as well to check for internet connection
    private fun hasInternetConnection(): Boolean {

        val connectivityManager = getApplication<ImageApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }

        return false
    }
}