package ru.vladislavsumin.cams.dto

data class ServerInfoDTO(
        val address: String
) {
    companion object {
        fun fromByteArray(array: ByteArray): ServerInfoDTO {
            val address = array.inputStream().bufferedReader().readLine()
            return ServerInfoDTO(address)
        }
    }

    fun toByteArray(): ByteArray {
        return "$address\n".toByteArray()
    }
}