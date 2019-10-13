package ru.vladislavsumin.cams.dto

data class ServerInfoDto(
        val address: String
) {
    companion object {
        fun fromByteArray(array: ByteArray): ServerInfoDto {
            val address = array.inputStream().bufferedReader().readLine()
            return ServerInfoDto(address)
        }
    }

    fun toByteArray(): ByteArray {
        return "$address\n".toByteArray()
    }
}