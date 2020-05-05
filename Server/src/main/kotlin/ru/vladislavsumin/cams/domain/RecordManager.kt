package ru.vladislavsumin.cams.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.vladislavsumin.cams.api.RecourseNotFoundException
import ru.vladislavsumin.cams.entity.CameraEntity
import ru.vladislavsumin.cams.entity.RecordEntity
import ru.vladislavsumin.cams.repository.RecordRepository
import ru.vladislavsumin.cams.utils.logger
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Service
class RecordManager @Autowired constructor(
        private val recordRepository: RecordRepository,
        private val encoderService: VideoEncoderService,
        @Value("\${pRecordPath}") private val rootPath: String
) {
    companion object {
        private const val SAVE_TIME: Long = 90L * 24 * 60 * 60 * 1000
        private val logger = logger<RecordManager>()
    }

    init {
        Paths.get(rootPath).resolve("records").toFile().mkdirs()
    }

    fun add(record: Path, camera: CameraEntity, timestamp: Long) {
        val size = record.toFile().length()
        val duration = encoderService.getDuration(record)
        logger.info("New record duration: $duration")
        val record1 = RecordEntity(
                timestamp = timestamp,
                fileSize = size,
                duration = duration,
                camera = camera
        )
        val save = recordRepository.save(record1)
        Files.move(record, getPath(save.id))
    }

    fun getAllSorted(): List<RecordEntity> = recordRepository.findAll()
            .sortedBy { it.timestamp }
            .reversed() //TODO replace to sql sort

    fun getAll(): List<RecordEntity> = recordRepository.findAll().toList()

    fun getInterval(begin: Date, period: Long): Iterable<RecordEntity> {
        return getAllSorted()
                .filter { it.timestamp > begin.time && it.timestamp < begin.time + period }
    }

    fun getPath(id: Long): Path {
        return Paths.get(rootPath).resolve("records").resolve("$id.mp4")
    }

    fun save(id: Long, name: String?): RecordEntity {
        val record = recordRepository.findById(id)
        if (!record.isPresent) throw RecourseNotFoundException()
        val copy = record.get().copy(keepForever = true, name = name)
        recordRepository.save(copy)
        return copy
    }

    fun delete(id: Long): RecordEntity {
        val record = recordRepository.findById(id)
        if (!record.isPresent) throw RecourseNotFoundException()
        val copy = record.get().copy(keepForever = false, name = null)
        recordRepository.save(copy)
        return copy
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 100, initialDelay = 5 * 60 * 1000)
    fun clean() {
        val timestamp = System.currentTimeMillis() - SAVE_TIME

        val recordsToDelete = recordRepository.findAll()
                .filter { !it.keepForever }
                .filter { it.timestamp < timestamp }

        logger.info("Find ${recordsToDelete.size} records to delete")

        recordsToDelete.forEach {
            val file = getPath(it.id).toFile()
            if (file.delete()) {
                recordRepository.delete(it)
                logger.trace("Record $it, path $file deleted")
            } else logger.warn("Can not delete file $file")
        }
    }
}