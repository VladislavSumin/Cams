package ru.vladislavsumin.cams

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import ru.vladislavsumin.cams.utils.ConfigVerifierUtils

@SpringBootApplication
open class Main

fun main(args: Array<String>) {
    if (!ConfigVerifierUtils.copyFromResource(
            mapOf(
                "application.properties.template" to "application.properties"
            )
        )
    ) return

    runApplication<Main>(*args)
}