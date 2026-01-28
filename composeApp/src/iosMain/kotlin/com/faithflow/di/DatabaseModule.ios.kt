package com.faithflow.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.faithflow.core.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSHomeDirectory

actual fun getAppDatabase(): AppDatabase {
    val dbFilePath = NSHomeDirectory() + "/faithflow.db"
    return Room.databaseBuilder<AppDatabase>(
        name = dbFilePath
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
