package com.jiaoay.plugins.core.util

import com.didiglobal.booster.kotlinx.NCPU
import com.didiglobal.booster.kotlinx.redirect
import com.didiglobal.booster.kotlinx.search
import com.didiglobal.booster.kotlinx.touch
import com.jiaoay.plugins.core.config.ExtensionsPluginConfig
import com.jiaoay.plugins.core.extensions.isTargetClass
import com.jiaoay.plugins.core.extensions.getTargetJarList
import com.jiaoay.plugins.core.logger
import org.apache.commons.compress.archivers.jar.JarArchiveEntry
import org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.apache.commons.compress.parallel.InputStreamSupplier
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.jar.JarFile
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

/**
 * Transform this file or directory to the output by the specified transformer
 *
 * @param output The output location
 * @param transformer The byte data transformer
 */
fun File.transform(
    output: File,
    config: ExtensionsPluginConfig,
    transformer: (ByteArray) -> ByteArray = { it -> it }
) {
    when {
        isDirectory -> this.toURI().let { base ->
            this.search().parallelStream().forEach {
                it.transform(
                    output = File(
                        output, base.relativize(it.toURI()).path
                    ),
                    config = config,
                    transformer = transformer
                )
            }
        }
        isFile -> when (extension.lowercase(Locale.getDefault())) {
            "jar" -> {
                val jarName = this.name
                JarFile(this).use {
                    val targetJarList = config.getTargetJarList(jarName = jarName)
                    if (targetJarList.isNotEmpty()) {
                        logger("targetJar: name: $jarName")
                        it.transformTargetJar(
                            output = output,
                            targetJarList = targetJarList,
                            entryFactory = ::JarArchiveEntry,
                            transformer = transformer
                        )
                    } else {
                        it.transform(
                            output = output,
                            entryFactory = ::JarArchiveEntry,
                            transformer = transformer
                        )
                    }
                }
            }
            "class" -> {
                this.inputStream().use {
                    it.transform(transformer).redirect(output)
                }
            }
            else -> this.copyTo(output, true)
        }
        else -> throw IOException("Unexpected file: ${this.canonicalPath}")
    }
}

fun InputStream.transform(transformer: (ByteArray) -> ByteArray): ByteArray {
    return transformer(readBytes())
}

fun ZipFile.transformTargetJar(
    output: OutputStream,
    targetJarList: List<String>,
    entryFactory: (ZipEntry) -> ZipArchiveEntry = ::ZipArchiveEntry,
    transformer: (ByteArray) -> ByteArray = { it -> it }
) {
    val entries = mutableSetOf<String>()
    val creator = ParallelScatterZipCreator(
        ThreadPoolExecutor(
            NCPU,
            NCPU,
            0L,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue<Runnable>(),
            Executors.defaultThreadFactory(),
            RejectedExecutionHandler { runnable, _ ->
                runnable.run()
            }
        )
    )

    entries().asSequence().forEach { entry ->
        if (!entries.contains(entry.name)) {
            if (isTargetClass(list = targetJarList, className = entry.name)) {
                logger("jarFile: targetClass: classPath: ${this.name}, className: ${entry.name}")
            } else {
                val zae = entryFactory(entry)
                val stream = InputStreamSupplier {
                    when (entry.name.substringAfterLast('.', "")) {
                        "class" -> {
                            getInputStream(entry).use { src ->
                                try {
                                    src.transform(transformer).inputStream()
                                } catch (e: Throwable) {
                                    System.err.println("Broken class: ${this.name}!/${entry.name}")
                                    getInputStream(entry)
                                }
                            }
                        }
                        else -> getInputStream(entry)
                    }
                }

                creator.addArchiveEntry(zae, stream)
                entries.add(entry.name)
            }
        } else {
            System.err.println("Duplicated jar entry: ${this.name}!/${entry.name}")
        }
    }

    ZipArchiveOutputStream(output).use(creator::writeTo)
}

fun ZipFile.transform(
    output: OutputStream,
    entryFactory: (ZipEntry) -> ZipArchiveEntry = ::ZipArchiveEntry,
    transformer: (ByteArray) -> ByteArray = { it -> it }
) {
    val entries = mutableSetOf<String>()
    val creator = ParallelScatterZipCreator(
        ThreadPoolExecutor(
            NCPU,
            NCPU,
            0L,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue<Runnable>(),
            Executors.defaultThreadFactory(),
            RejectedExecutionHandler { runnable, _ ->
                runnable.run()
            }
        )
    )

    entries().asSequence().forEach { entry ->
        if (!entries.contains(entry.name)) {
            val zae = entryFactory(entry)
            val stream = InputStreamSupplier {
                when (entry.name.substringAfterLast('.', "")) {
                    "class" -> getInputStream(entry).use { src ->
                        try {
                            src.transform(transformer).inputStream()
                        } catch (e: Throwable) {
                            System.err.println("Broken class: ${this.name}!/${entry.name}")
                            getInputStream(entry)
                        }
                    }
                    else -> getInputStream(entry)
                }
            }

            creator.addArchiveEntry(zae, stream)
            entries.add(entry.name)
        } else {
            System.err.println("Duplicated jar entry: ${this.name}!/${entry.name}")
        }
    }

    ZipArchiveOutputStream(output).use(creator::writeTo)
}

fun ZipFile.transformTargetJar(
    output: File,
    targetJarList: List<String>,
    entryFactory: (ZipEntry) -> ZipArchiveEntry = ::ZipArchiveEntry,
    transformer: (ByteArray) -> ByteArray = { it -> it }
) {
    logger("targetJar: outputFile: $output")
    output.touch().outputStream().buffered().use {
        transformTargetJar(
            output = it,
            targetJarList = targetJarList,
            entryFactory = entryFactory,
            transformer = transformer
        )
    }
}

fun ZipFile.transform(
    output: File,
    entryFactory: (ZipEntry) -> ZipArchiveEntry = ::ZipArchiveEntry,
    transformer: (ByteArray) -> ByteArray = { it -> it }
) = output.touch().outputStream().buffered().use {
    transform(it, entryFactory, transformer)
}

fun ZipInputStream.transform(
    output: OutputStream,
    entryFactory: (ZipEntry) -> ZipArchiveEntry = ::ZipArchiveEntry,
    transformer: (ByteArray) -> ByteArray
) {
    val creator = ParallelScatterZipCreator()
    val entries = mutableSetOf<String>()

    while (true) {
        val entry = nextEntry?.takeIf { true } ?: break
        if (!entries.contains(entry.name)) {
            val zae = entryFactory(entry)
            val data = readBytes()
            val stream = InputStreamSupplier {
                transformer(data).inputStream()
            }
            creator.addArchiveEntry(zae, stream)
            entries.add(entry.name)
        }
    }

    ZipArchiveOutputStream(output).use(creator::writeTo)
}

fun ZipInputStream.transform(
    output: File,
    entryFactory: (ZipEntry) -> ZipArchiveEntry = ::ZipArchiveEntry,
    transformer: (ByteArray) -> ByteArray
) = output.touch().outputStream().buffered().use {
    transform(it, entryFactory, transformer)
}

private const val DEFAULT_BUFFER_SIZE = 8 * 1024

private fun InputStream.readBytes(estimatedSize: Int = DEFAULT_BUFFER_SIZE): ByteArray {
    val buffer = ByteArrayOutputStream(maxOf(estimatedSize, this.available()))
    copyTo(buffer)
    return buffer.toByteArray()
}

private fun InputStream.copyTo(out: OutputStream, bufferSize: Int = DEFAULT_BUFFER_SIZE): Long {
    var bytesCopied: Long = 0
    val buffer = ByteArray(maxOf(bufferSize, this.available()))
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        bytesCopied += bytes
        bytes = read(buffer)
    }
    return bytesCopied
}
