package ru.vladislavsumin.cams.dto

data class RecordDto(
        val id: Long = 0L,
        val name: String? = null,
        val timestamp: Long = 0L,
        val fileSize: Long = 0L,
        val keepForever: Boolean = false,
        val cameraId: Long? = null
)