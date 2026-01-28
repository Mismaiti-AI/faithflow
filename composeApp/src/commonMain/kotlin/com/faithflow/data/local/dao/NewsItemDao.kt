package com.faithflow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.faithflow.data.local.entity.NewsItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsItemDao {
    @Query("SELECT * FROM news_items ORDER BY publish_date DESC")
    fun observeAll(): Flow<List<NewsItemEntity>>

    @Query("SELECT * FROM news_items ORDER BY publish_date DESC")
    suspend fun getAll(): List<NewsItemEntity>

    @Query("SELECT * FROM news_items WHERE id = :id")
    suspend fun getById(id: String): NewsItemEntity?

    @Query("SELECT * FROM news_items WHERE id = :id")
    fun observeById(id: String): Flow<NewsItemEntity?>

    @Query("SELECT * FROM news_items WHERE category = :category ORDER BY publish_date DESC")
    fun observeByCategory(category: String): Flow<List<NewsItemEntity>>

    @Query("SELECT * FROM news_items WHERE is_urgent = 1 ORDER BY publish_date DESC")
    fun observeUrgent(): Flow<List<NewsItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(newsItem: NewsItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(newsItems: List<NewsItemEntity>)

    @Update
    suspend fun update(newsItem: NewsItemEntity)

    @Query("DELETE FROM news_items WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM news_items")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(newsItems: List<NewsItemEntity>) {
        deleteAll()
        insertAll(newsItems)
    }
}
