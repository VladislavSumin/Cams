package ru.vladislavsumin.cams.database.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.vladislavsumin.cams.dto.CameraDTO

@Entity(tableName = "cams")
data class CameraEntity(
        @PrimaryKey
        val id: Long = 0L,
        val name: String? = null,
        val ip: String = "",
        val port: Int = 34567,
        val login: String = "admin",
        val password: String = "",
        val enabled: Boolean = true,
        val deleted: Boolean = false
) : Parcelable {


    //*************************************************************//
    //                     Parcelable methods                      //
    //*************************************************************//
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

    companion object CREATOR : Parcelable.Creator<CameraEntity> {
        override fun createFromParcel(parcel: Parcel): CameraEntity {
            return CameraEntity(parcel)
        }

        override fun newArray(size: Int): Array<CameraEntity?> {
            return arrayOfNulls(size)
        }
    }
}

fun CameraDTO.toEntity() = CameraEntity(
        id = this.id,
        name = this.name,
        ip = this.ip,
        port = this.port,
        login = this.login,
        password = this.password,
        enabled = this.enabled,
        deleted = this.deleted
)

fun List<CameraDTO>.toEntity() = this.map { it.toEntity() }

fun CameraEntity.toDTO() = CameraDTO(
        id = this.id,
        name = this.name,
        ip = this.ip,
        port = this.port,
        login = this.login,
        password = this.password,
        enabled = this.enabled,
        deleted = this.deleted)