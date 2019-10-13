package ru.vladislavsumin.cams.network

import dagger.Module
import dagger.Provides
import ru.vladislavsumin.cams.network.api.AboutApi
import ru.vladislavsumin.cams.network.api.CamsApi
import ru.vladislavsumin.cams.network.api.RecordsApiV1
import ru.vladislavsumin.cams.network.api.RecordsApiV2
import javax.inject.Singleton

@Module
class NetworkModule {
    @Provides
    @Singleton
    fun provideNetworkService(): NetworkService {
        return NetworkService()
    }

    @Provides
    @Singleton
    fun provideCamsApi(networkService: NetworkService): CamsApi {
        return networkService.create(CamsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRecordsApiV1(networkService: NetworkService): RecordsApiV1 {
        return networkService.create(RecordsApiV1::class.java)
    }

    @Provides
    @Singleton
    fun provideRecordsApiV2(networkService: NetworkService): RecordsApiV2 {
        return networkService.create(RecordsApiV2::class.java)
    }

    @Provides
    @Singleton
    fun provideAboutApi(networkService: NetworkService): AboutApi {
        return networkService.create(AboutApi::class.java)
    }


}