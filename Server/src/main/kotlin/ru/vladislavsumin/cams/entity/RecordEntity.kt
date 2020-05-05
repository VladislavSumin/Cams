package ru.vladislavsumin.cams.entity

import ru.vladislavsumin.cams.dto.RecordDto
import javax.persistence.*

@Entity(name = "records")
data class RecordEntity(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(unique = true, nullable = false)
        val id: Long = 0L,

        @Column(nullable = true)
        val name: String? = null,

        @Column(nullable = false)
        val timestamp: Long = 0L,

        @Column(nullable = false)
        val fileSize: Long = 0L,

        @Column(nullable = true)
        val duration: Double? = null,

        @Column(nullable = false)
        val keepForever: Boolean = false,

        @ManyToOne
        val camera: CameraEntity? = null
)

fun RecordEntity.toDto(): RecordDto {
    return RecordDto(
            id = this.id,
            name = this.name,
            timestamp = this.timestamp,
            duration = this.duration,
            fileSize = this.fileSize,
            keepForever = this.keepForever,
            cameraId = camera?.id
    )
}

fun List<RecordEntity>.toDto(): List<RecordDto> {
    return this.map { it.toDto() }
}