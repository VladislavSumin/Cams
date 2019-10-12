package ru.vladislavsumin.cams.entity

import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import ru.vladislavsumin.cams.dto.CameraDTO
import javax.persistence.*


@Entity(name = "cameras")
data class CameraEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(unique = true, nullable = false)
        val id: Long = 0L,

        @Column(nullable = false)
        val name: String = "",

        @Column(nullable = false)
        val ip: String = "",

        @Column(nullable = false)
        val port: Int = 34567,

        @Column(nullable = false)
        val login: String = "admin",

        @Column(nullable = false)
        val password: String = "",

        @Column(nullable = false)
        val enabled: Boolean = true,

        @Column(nullable = false)
        val deleted: Boolean = false
) {
    @OneToMany(mappedBy = "camera", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.NO_ACTION) //TODO verify
    private val records: Set<Record>? = null
}

fun CameraEntity.toDTO() = CameraDTO(
        id = this.id,
        name = this.name,
        ip = this.ip,
        port = this.port,
        login = this.login,
        password = this.password, //TODO think about it
        enabled = this.enabled,
        deleted = this.deleted)

fun List<CameraEntity>.toDTO() = this.map { it.toDTO() }

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