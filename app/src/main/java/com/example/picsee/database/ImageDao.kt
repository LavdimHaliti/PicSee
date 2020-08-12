package com.example.picsee.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.picsee.api.Hit

/**
 * ImageDao creates the necessary queries and functions to manipulate with the database
 */
@Dao
interface ImageDao {

    //Deals with inserting and updating the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(image: Hit): Long

    //Deletes a picture from the database
    @Delete
    suspend fun delete(image: Hit)


    //Get all images from the database
    @Query("SELECT * FROM picture_table")
    fun getAllImages() : LiveData<List<Hit>>

}