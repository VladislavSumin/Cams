package ru.vladislavsumin.cams.domain

import android.content.Context
import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import ru.vladislavsumin.cams.database.dao.CameraDao
import ru.vladislavsumin.cams.database.dao.RecordDao
import ru.vladislavsumin.cams.domain.impl.*
import ru.vladislavsumin.cams.domain.interfaces.*
import ru.vladislavsumin.cams.network.api.AboutApi
import ru.vladislavsumin.cams.network.api.CamsApi
import ru.vladislavsumin.cams.network.api.RecordsApi
import ru.vladislavsumin.cams.storage.CredentialStorage
import javax.inject.Singleton

@Module
class DomainModule {
    @Provides
    @Singleton
    fun provideVibrationManager(context: Context): VibrationManagerI {
        return VibrationManager(context)
    }

    @Provides
    @Singleton
    fun provideNetworkDiscoveryManager(networkManager: NetworkManagerI): NetworkDiscoveryManagerI {
        return NetworkDiscoveryManager(networkManager)
    }

    @Provides
    @Singleton
    fun provideNetworkManager(connectivityManager: ConnectivityManager): NetworkManagerI {
        return NetworkManager(connectivityManager)
    }

    @Provides
    @Singleton
    fun provideConnectionManager(aboutApi: AboutApi): ConnectionManagerI {
        return ConnectionManager(aboutApi)
    }

    @Provides
    @Singleton
    fun provideCamsManager(cameraDao: CameraDao, camsApi: CamsApi): CamsManagerI {
        return CamsManager(cameraDao, camsApi)
    }

    @Provides
    @Singleton
    fun provideRecordManager(recordDao: RecordDao, recordsApi: RecordsApi,
                             credentialStorage: CredentialStorage,
                             cameraManager: CamsManagerI): RecordManagerI {
        return RecordManager(recordDao, recordsApi, credentialStorage, cameraManager)
    }
}