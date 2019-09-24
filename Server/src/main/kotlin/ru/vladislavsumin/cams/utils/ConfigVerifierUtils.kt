package ru.vladislavsumin.cams.utils

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

object ConfigVerifierUtils {
    /**
     * Copy file from resources to project dir if not exist
     * pair key - resource name
     * pair value - project dir path
     *
     * @return true - if all resources exist, else false
     */
    fun copyFromResource(vararg configs: Pair<String, String>): Boolean = copyFromResource(configs.toMap())

    /**
     * Copy file from resources to project dir if not exist
     * map key - resource name
     * map value - project dir path
     *
     * @return true - if all resources exist, else false
     */
    fun copyFromResource(configs: Map<String, String>): Boolean {
        configs
                .map { copyFromResource(it.key, it.value) }
                .forEach { if (!it) return false }
        return true
    }

    /**
     * Copy file from resource to project dir if not exist
     *
     * @param from - resource name
     * @param to - path to copy
     *
     * @return true - if resource exist, false - if copy from template
     */
    private fun copyFromResource(from: String, to: String): Boolean {
        val configFile = File(to)
        if (configFile.exists()) return true
        println("$to file does not exist, creating from template")

        val classLoader = ConfigVerifierUtils::class.java.classLoader
        classLoader.getResourceAsStream(from)!!.use { `is` -> Files.copy(`is`, Paths.get(to)) }
        return false
    }
}