package ru.vladislavsumin.cams.domain.interfaces

import io.reactivex.Flowable
import ru.vladislavsumin.cams.database.entity.CameraEntity

interface CamsManagerI {
    fun observeAll(): Flowable<List<CameraEntity>>
}