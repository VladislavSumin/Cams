package ru.vladislavsumin.cams.network.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import ru.vladislavsumin.cams.dto.RecordDto

interface RecordsApi {
    @GET("api/v2/records")
    fun getAll(): Single<List<RecordDto>>

    @POST("api/v2/records/save")
    fun save(@Query("id") id: Long, @Query("name") name: String?): Single<RecordDto>

    @POST("api/v2/records/delete")
    fun delete(@Query("id") id: Long): Single<RecordDto>
}