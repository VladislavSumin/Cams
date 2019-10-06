package ru.vladislavsumin.cams.dao

import javax.persistence.*

@Deprecated("rename to DAO")
@Entity(name = "records")
data class Record(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    val id: Long = 0L,

    @Column(nullable = true)
    val name: String? = null,

    @Column(nullable = false)
    val timestamp: Long = 0L,

    @Column(nullable = false)
    val fileSize: Long = 0L,

    @Column(nullable = false)
    val keepForever: Boolean = false,

    @ManyToOne
    val camera: CameraDAO? = null
)