package ru.vladislavsumin.cams.domain

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.vladislavsumin.cams.domain.impl.ConnectionManager
import ru.vladislavsumin.cams.domain.impl.NetworkDiscoveryManager
import ru.vladislavsumin.cams.domain.impl.NetworkManager
import ru.vladislavsumin.cams.domain.impl.VibrationManager
import ru.vladislavsumin.cams.domain.interfaces.ConnectionManagerI
import ru.vladislavsumin.cams.domain.interfaces.NetworkDiscoveryManagerI
import ru.vladislavsumin.cams.domain.interfaces.NetworkManagerI
import ru.vladislavsumin.cams.domain.interfaces.VibrationManagerI
import ru.vladislavsumin.cams.network.api.AboutApi
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
}