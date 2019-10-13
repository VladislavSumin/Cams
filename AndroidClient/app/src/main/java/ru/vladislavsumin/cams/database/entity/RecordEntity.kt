package ru.vladislavsumin.cams.database.entity

import androidx.room.*
import androidx.room.ForeignKey.SET_NULL
import ru.vladislavsumin.cams.database.dao.RecordDao
import ru.vladislavsumin.cams.dto.CameraDto
import ru.vladislavsumin.cams.dto.RecordDto
import ru.vladislavsumin.cams.utils.SortedListDiff

@Entity(tableName = "records",
        foreignKeys = [
            ForeignKey(
                    entity = CameraEntity::class,
                    parentColumns = ["id"],
                    childColumns = ["camera_id"],
                    onDelete = SET_NULL
            )
        ],
        indices = [
            Index("id"),
            Index("camera_id")
        ]

)
data class RecordEntity(
        @PrimaryKey
        val id: Long = 0L,
        val name: String? = null,
        val timestamp: Long = 0L,
        val fileSize: Long = 0L,
        val keepForever: Boolean = false,

        @ColumnInfo(name = "camera_id")
        val cameraId: Long? = null
) {
    companion object : SortedListDiff.Comparator<RecordEntity> {
        override fun compare(o1: RecordEntity, o2: RecordEntity): Int {
            return o1.id.compareTo(o2.id)
        }

        override fun sameContent(o1: RecordEntity, o2: RecordEntity): Boolean {
            return o1 == o2
        }

    }
}

fun RecordDto.toEntity(): RecordEntity {
    return RecordEntity(
            id = this.id,
            name = this.name,
            timestamp = this.timestamp,
            fileSize = this.fileSize,
            keepForever = this.keepForever,
            cameraId = this.cameraId
    )
}

fun List<RecordDto>.toEntity(): List<RecordEntity> {
    return this.map { it.toEntity() }
}