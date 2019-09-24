package ru.vladislavsumin.cams.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T> logger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}

@Suppress("UNUSED_PARAMETER")
inline fun <reified T> logger(clazz: T): Logger {
    return LoggerFactory.getLogger(T::class.java)
}