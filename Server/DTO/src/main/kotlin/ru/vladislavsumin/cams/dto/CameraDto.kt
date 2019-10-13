package ru.vladislavsumin.cams.dto

data class CameraDto(
        val id: Long,
        val name: String,
        val ip: String,
        val port: Int,
        val login: String,
        val password: String,
        val enabled: Boolean,
        val deleted: Boolean
)