package ru.vladislavsumin.cams.entity


data class Record(
    val id: Long = 0L,
    val name: String? = null,
    val timestamp: Long = 0L,
    val fileSize: Long = 0L,
    val keepForever: Boolean = false,
    val camera: CameraDAO? = null
)