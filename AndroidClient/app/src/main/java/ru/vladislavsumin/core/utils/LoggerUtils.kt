package ru.vladislavsumin.core.utils

inline fun <reified T> tag(): String {
    return T::class.java.simpleName
}