package ru.vladislavsumin.cams.entity

import android.os.Parcel
import android.os.Parcelable
import ru.vladislavsumin.cams.dto.CameraDTO

data class CameraDAO(
        val id: Long = 0L,
        val name: String? = null,
        val ip: String = "",
        val port: Int = 34567,
        val login: String = "admin",
        val password: String = "",
        val enabled: Boolean = true,
        val deleted: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(ip)
        parcel.writeInt(port)
        parcel.writeString(login)
        parcel.writeString(password)
        parcel.writeByte(if (enabled) 1 else 0)
        parcel.writeByte(if (deleted) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CameraDAO> {
        override fun createFromParcel(parcel: Parcel): CameraDAO {
            return CameraDAO(parcel)
        }

        override fun newArray(size: Int): Array<CameraDAO?> {
            return arrayOfNulls(size)
        }
    }
}

fun CameraDTO.toDAO() = CameraDAO(
        id = this.id,
        name = this.name,
        ip = this.ip,
        port = this.port,
        login = this.login,
        password = this.password,
        enabled = this.enabled,
        deleted = this.deleted
)

fun List<CameraDTO>.toDAO() = this.map { it.toDAO() }

fun CameraDAO.toDTO() = CameraDTO(
        id = this.id,
        name = this.name,
        ip = this.ip,
        port = this.port,
        login = this.login,
        password = this.password,
        enabled = this.enabled,
        deleted = this.deleted)