package ru.vladislavsumin.cams.database

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.vladislavsumin.cams.database.dao.CameraDao
import ru.vladislavsumin.cams.database.dao.RecordDao
import ru.vladislavsumin.cams.database.entity.CameraEntity
import ru.vladislavsumin.cams.database.entity.RecordEntity
import ru.vladislavsumin.core.utils.tag

@androidx.room.Database(entities = [
    CameraEntity::class,
    RecordEntity::class
], exportSchema = false, version = 1)
abstract class Database : RoomDatabase() {
    companion object {
        private const val DATABASE_FILE = "database"
        private val TAG = tag<Database>()

        fun createInstance(context: Context): Database {
            Log.i(TAG, "creating database instance")
            return Room.databaseBuilder<Database>(context, Database::class.java, DATABASE_FILE)
                    .build()
        }
    }

    abstract fun getCameraDao(): CameraDao
    abstract fun getRecordDao(): RecordDao
}