package com.example.picsee.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.picsee.api.Hit

@Dao
interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(image: Hit): Long

    @Delete
    suspend fun delete(image: Hit)

    @Query("SELECT * FROM picture_table")
    fun getAllImages() : LiveData<List<Hit>>

}