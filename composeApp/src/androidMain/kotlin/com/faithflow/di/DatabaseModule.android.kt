package com.faithflow.di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.faithflow.core.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.mp.KoinPlatform.getKoin

actual fun getAppDatabase(): AppDatabase {
    val context = getKoin().get<Context>()
    val dbFile = context.getDatabasePath("faithflow.db")
    return Room.databaseBuilder<AppDatabase>(
        context = context,
        name = dbFile.absolutePath
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
