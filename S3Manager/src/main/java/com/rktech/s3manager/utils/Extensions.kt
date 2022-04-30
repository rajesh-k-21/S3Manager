package com.rktech.s3manager.utils

import java.io.File
import java.util.*
import kotlin.math.abs

fun File?.getRandomName(): String? {
    return if (this == null)
        null
    else
        Random().nextInt(abs(System.currentTimeMillis().toInt()))
            .toString() + "" + this.absolutePath.getExtension()
}

fun String?.getExtension(): String? {
    return when {
        this == null -> null
        this.lastIndexOf(".") >= 0 -> this.substring(this.lastIndexOf("."))
        else -> ""
    }
}