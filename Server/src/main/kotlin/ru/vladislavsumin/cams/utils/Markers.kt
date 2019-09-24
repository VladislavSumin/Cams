package ru.vladislavsumin.cams.utils

@Retention(AnnotationRetention.SOURCE)
annotation class ThreadSafe

@Retention(AnnotationRetention.SOURCE)
annotation class NotThreadSafe

@Retention(AnnotationRetention.SOURCE)
annotation class CalledOnThread(val threadName: String)