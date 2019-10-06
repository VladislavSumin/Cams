package ru.vladislavsumin.cams.dao

import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import javax.persistence.*


@Entity(name = "cameras")
data class CameraDAO(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    val id: Long = 0L,

    @Column(nullable = true)
    val name: String? = null,

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