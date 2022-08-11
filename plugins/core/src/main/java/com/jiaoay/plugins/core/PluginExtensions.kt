package com.jiaoay.plugins.core

import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes

private const val TAG = "ExtensionsPlugin"

internal var pluginProject: Project? = null

fun logger(message: String) {
    logger(level = LogLevel.ERROR, message = message)
}

fun logger(level: LogLevel, message: String) {
    pluginProject?.logger?.log(level, "$TAG: $message") ?: println("$TAG: $message")
}


val ClassReader.isQualifiedClass: Boolean
    get() {
        return !(access.and(Opcodes.ACC_INTERFACE) != 0 ||
                access.and(Opcodes.ACC_ABSTRACT) != 0 ||
                access.and(Opcodes.ACC_ENUM) != 0 ||
                access.and(Opcodes.ACC_ANNOTATION) != 0 ||
                access.and(Opcodes.ACC_MODULE) != 0 ||
                access.and(Opcodes.ACC_SYNTHETIC) != 0)
    }