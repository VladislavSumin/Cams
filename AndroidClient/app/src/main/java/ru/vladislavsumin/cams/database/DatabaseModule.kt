package ru.vladislavsumin.cams.database

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.vladislavsumin.cams.database.dao.CameraDao
import ru.vladislavsumin.cams.database.dao.RecordDao
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(context: Context): Database {
        return Database.createInstance(context)
    }

    @Provides
    fun provideCameraDao(database: Database): CameraDao {
        return database.getCameraDao()
    }

    @Provides
    fun provideRecordDao(database: Database): RecordDao {
        return database.getRecordDao()
    }
}