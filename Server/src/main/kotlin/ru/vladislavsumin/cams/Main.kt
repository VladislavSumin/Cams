package ru.vladislavsumin.cams

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import ru.vladislavsumin.cams.utils.ConfigVerifierUtils

@SpringBootApplication
@EnableScheduling //TODO move to config
@EnableWebMvc
open class Main

fun main(args: Array<String>) {
    if (!ConfigVerifierUtils.copyFromResource(
                    "application.properties.template" to "application.properties"
            )
    ) return

    runApplication<Main>(*args)
}