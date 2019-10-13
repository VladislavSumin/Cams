package ru.vladislavsumin.cams.database.entity

import androidx.room.*
import androidx.room.ForeignKey.SET_NULL

@Entity(tableName = "records", foreignKeys = [
    ForeignKey(
            entity = CameraEntity::class,
            parentColumns = ["id"],
            childColumns = ["camera_id"],
            onDelete = SET_NULL
    )
])
data class RecordEntity(
        @PrimaryKey
        val id: Long = 0L,
        val name: String? = null,
        val timestamp: Long = 0L,
        val fileSize: Long = 0L,
        val keepForever: Boolean = false,

        @ColumnInfo(name = "camera_id")
        val cameraId: Long? = null
)