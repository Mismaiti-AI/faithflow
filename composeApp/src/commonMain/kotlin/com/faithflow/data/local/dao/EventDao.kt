package com.faithflow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.faithflow.data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY date DESC")
    fun observeAll(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events ORDER BY date DESC")
    suspend fun getAll(): List<EventEntity>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getById(id: String): EventEntity?

    @Query("SELECT * FROM events WHERE id = :id")
    fun observeById(id: String): Flow<EventEntity?>

    @Query("SELECT * FROM events WHERE category = :category ORDER BY date DESC")
    fun observeByCategory(category: String): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE is_featured = 1 ORDER BY date DESC")
    fun observeFeatured(): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventEntity>)

    @Update
    suspend fun update(event: EventEntity)

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM events")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(events: List<EventEntity>) {
        deleteAll()
        insertAll(events)
    }
}
