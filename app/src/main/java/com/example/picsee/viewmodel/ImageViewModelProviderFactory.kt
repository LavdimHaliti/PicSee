package com.example.picsee.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.picsee.repository.ImageRepository

/**
 * Is used to show the ViewModelProvider how to create a ViewModel.
 */
class ImageViewModelProviderFactory(val app: Application, val imageRepository: ImageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ImageViewModel(app, imageRepository) as T
    }
}