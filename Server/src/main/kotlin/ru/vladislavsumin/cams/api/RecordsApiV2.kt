package ru.vladislavsumin.cams.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler
import ru.vladislavsumin.cams.domain.RecordManager
import ru.vladislavsumin.cams.entity.Record
import java.io.IOException
import java.nio.file.Path
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("api/v2/records")
class RecordsApiV2 {
    @Autowired
    lateinit var recordManager: RecordManager

    @Autowired
    lateinit var handler: VideoHandler

    @GetMapping
    @ResponseBody
    fun getList(
            @RequestParam(name = "date_filter_from", required = false) dateFilterFrom: Long?,
            @RequestParam(name = "date_filter_to", required = false) dateFilterTo: Long?,
            @RequestParam(name = "only_saved", required = false) onlySaved: Boolean?
    ): Iterable<Record> {
        return recordManager.getFiltered(
                dateFilterFrom ?: 0L,
                dateFilterTo ?: System.currentTimeMillis(),
                onlySaved ?: false
        )
    }

    @GetMapping("/record/{id}")
    @Throws(ServletException::class, IOException::class)
    fun getVideo(
            request: HttpServletRequest,
            response: HttpServletResponse,
            @PathVariable id: Long
    ) {
        //TODO тут нужна защита поля name от недопустимых символов
        //TODO вернуть 404 если файл не найден
        val encodedVideo = recordManager.getPath(id)
        request.setAttribute(VideoHandler.ATTR_FILE, encodedVideo)
        handler.handleRequest(request, response)
    }

    @PostMapping("/save")
    fun save(
            @RequestParam id: Long,
            @RequestParam(required = false) name: String?
    ): Record {
        return recordManager.save(id, name)
    }

    @PostMapping("/delete")
    fun delete(
            @RequestParam id: Long
    ): Record {
        return recordManager.delete(id)
    }

    @Component
    class VideoHandler : ResourceHttpRequestHandler() {

        override fun getResource(request: HttpServletRequest): Resource {
            val file = request.getAttribute(ATTR_FILE) as Path
            return FileSystemResource(file)
        }

        companion object {
            val ATTR_FILE = VideoHandler::class.java.name + ".file"
        }
    }
}