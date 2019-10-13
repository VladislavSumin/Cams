package ru.vladislavsumin.cams.database.combined

import androidx.room.Embedded
import androidx.room.Relation
import ru.vladislavsumin.cams.database.entity.CameraEntity
import ru.vladislavsumin.cams.database.entity.RecordEntity

data class RecordWithCamera(
        @Embedded
        val record: RecordEntity,
//TODO change db structure to different id for table
        @Relation(
                parentColumn = "camera_id",
                entityColumn = "id",
                entity = CameraEntity::class
        )
        val cameraList: List<CameraEntity?>
)