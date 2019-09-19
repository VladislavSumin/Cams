package ru.vladislavsumin.cams.entity

import android.os.Parcel
import android.os.Parcelable

data class Camera(
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

    companion object CREATOR : Parcelable.Creator<Camera> {
        override fun createFromParcel(parcel: Parcel): Camera {
            return Camera(parcel)
        }

        override fun newArray(size: Int): Array<Camera?> {
            return arrayOfNulls(size)
        }
    }

}