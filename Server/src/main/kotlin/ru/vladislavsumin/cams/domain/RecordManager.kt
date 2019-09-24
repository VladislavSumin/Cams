package ru.vladislavsumin.cams.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.vladislavsumin.cams.api.RecourseNotFoundException
import ru.vladislavsumin.cams.entity.Camera
import ru.vladislavsumin.cams.entity.Record
import ru.vladislavsumin.cams.repository.RecordRepository
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Service
class RecordManager
@Autowired constructor(
    private val recordRepository: RecordRepository,
    @Value("\${pRecordPath}") private val rootPath: String
) {

    init {
        Paths.get(rootPath).resolve("records").toFile().mkdirs()
    }

    fun add(record: Path, camera: Camera, timestamp: Long) {
        val size = record.toFile().length()
        val record1 = Record(
            timestamp = timestamp,
            fileSize = size,
            camera = camera
        )
        val save = recordRepository.save(record1)
        Files.move(record, Paths.get(rootPath).resolve("records").resolve("${save.id}.mp4"))
    }

    fun getAll(): Iterable<Record> = recordRepository.findAll()
        .sortedBy { it.timestamp }
        .reversed() //TODO replace to sql sort

    fun getInterval(begin: Date, period: Long): Iterable<Record> {
        return getAll()
            .filter { it.timestamp > begin.time && it.timestamp < begin.time + period }
    }

    fun getPath(id: Long): Path {
        return Paths.get(rootPath).resolve("records").resolve("$id.mp4")
    }

    fun save(id: Long, name: String?): Record {
        val record = recordRepository.findById(id)
        if (!record.isPresent) throw RecourseNotFoundException()
        val copy = record.get().copy(keepForever = true, name = name)
        recordRepository.save(copy)
        return copy
    }

    fun delete(id: Long): Record {
        val record = recordRepository.findById(id)
        if (!record.isPresent) throw RecourseNotFoundException()
        val copy = record.get().copy(keepForever = false, name = null)
        recordRepository.save(copy)
        return copy
    }
}