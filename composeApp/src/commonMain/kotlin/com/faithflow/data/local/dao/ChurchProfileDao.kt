package com.faithflow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.faithflow.data.local.entity.ChurchProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChurchProfileDao {
    @Query("SELECT * FROM church_profile WHERE id = 'default'")
    fun observe(): Flow<ChurchProfileEntity?>

    @Query("SELECT * FROM church_profile WHERE id = 'default'")
    suspend fun get(): ChurchProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: ChurchProfileEntity)

    @Update
    suspend fun update(profile: ChurchProfileEntity)

    @Query("DELETE FROM church_profile")
    suspend fun delete()
}
