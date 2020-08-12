package com.example.picsee.api

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * The class hit creates the table for the database
 */
data class ImageEnities(
    val hits: MutableList<Hit>,
    val total: Int,
    val totalHits: Int
)

@Entity(tableName = "picture_table")
@Parcelize
data class Hit(
    val comments: Int,
    val downloads: Int,
    val favorites: Int,

    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val imageHeight: Int,
    val imageSize: Int,
    val imageWidth: Int,
    val largeImageURL: String,
    val likes: Int,
    val pageURL: String,
    val previewHeight: Int,
    val previewURL: String,
    val previewWidth: Int,
    val tags: String,
    val type: String,
    val user: String,
    val userImageURL: String,
    val user_id: Int,
    val views: Int,
    val webformatHeight: Int,
    val webformatURL: String,
    val webformatWidth: Int
): Parcelable