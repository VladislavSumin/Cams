package ru.vladislavsumin.cams.entity

import javax.persistence.*

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
    val camera: CameraEntity? = null
)