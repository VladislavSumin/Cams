package ru.vladislavsumin.cams.domain

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.vladislavsumin.cams.database.dao.CameraDao
import ru.vladislavsumin.cams.domain.impl.*
import ru.vladislavsumin.cams.domain.interfaces.*
import ru.vladislavsumin.cams.network.api.AboutApi
import ru.vladislavsumin.cams.network.api.CamsApi
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
    fun provideNetworkManager(context: Context): NetworkManagerI {
        return NetworkManager(context)
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
}