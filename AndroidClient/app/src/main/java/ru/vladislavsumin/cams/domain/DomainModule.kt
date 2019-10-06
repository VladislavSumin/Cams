package ru.vladislavsumin.cams.domain

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.vladislavsumin.cams.domain.Impl.ConnectionManager
import ru.vladislavsumin.cams.domain.Impl.NetworkDiscoveryManager
import ru.vladislavsumin.cams.domain.Impl.VibrationManager
import ru.vladislavsumin.cams.domain.interfaces.ConnectionManagerI
import ru.vladislavsumin.cams.domain.interfaces.NetworkDiscoveryManagerI
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
    fun provideNetworkDiscoveryManager(): NetworkDiscoveryManagerI {
        return NetworkDiscoveryManager()
    }


    @Provides
    @Singleton
    fun provideConnectionManager(aboutApi: AboutApi): ConnectionManagerI {
        return ConnectionManager(aboutApi)
    }
}