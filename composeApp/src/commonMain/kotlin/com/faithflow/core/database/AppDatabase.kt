package com.faithflow.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.faithflow.data.local.dao.ChurchProfileDao
import com.faithflow.data.local.dao.EventDao
import com.faithflow.data.local.dao.NewsItemDao
import com.faithflow.data.local.entity.ChurchProfileEntity
import com.faithflow.data.local.entity.EventEntity
import com.faithflow.data.local.entity.NewsItemEntity

@Database(
    entities = [
        EventEntity::class,
        NewsItemEntity::class,
        ChurchProfileEntity::class
    ],
    version = 1,
    exportSchema = true
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun newsItemDao(): NewsItemDao
    abstract fun churchProfileDao(): ChurchProfileDao
}
