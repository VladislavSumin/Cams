package ru.vladislavsumin.cams.dto

data class CameraDTO(
        val id: Long,
        val name: String?,
        val ip: String,
        val port: Int,
        val login: String,
        val password: String,
        val enabled: Boolean,
        val deleted: Boolean
)