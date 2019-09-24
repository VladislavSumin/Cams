package ru.vladislavsumin.cams.camera.controller

import ru.vladislavsumin.cams.camera.protocol.Msg

class UnexpectedResponseException(msg: Msg) : Exception(msg.toString())