package com.jiaoay.plugins.core.extensions

import com.android.SdkConstants
import com.android.build.api.transform.TransformInvocation
import com.jiaoay.plugins.core.Replace
import com.jiaoay.plugins.core.asm.getValue
import com.jiaoay.plugins.core.config.ExtensionsPluginConfig
import com.jiaoay.plugins.core.isQualifiedClass
import com.jiaoay.plugins.core.logger
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InnerClassNode
import java.io.File
import java.io.FileInputStream


/**
 * 获取需要替换的class
 */
fun initSdkPatcher(invocation: TransformInvocation): Map<String, List<String>> {
    val replaceClassMap: MutableMap<String, MutableList<String>> = HashMap()
    // 不支持增量编译
    val outputProvider = invocation.outputProvider
    if (invocation.isIncremental.not()) {
        outputProvider.deleteAll()
    }
    invocation.inputs.forEach { input ->
        // 当前仅处理目录中的，如果之后jar中也有，再单独处理
        input.directoryInputs.forEach { directoryInput ->
            val file = directoryInput.file
            if (file.isDirectory.not()) {
                return mapOf()
            }
            handleFile(inputFile = file).forEach { pair ->
                val packageName = pair.first
                val className = pair.second
                if (replaceClassMap.containsKey(packageName)) {
                    replaceClassMap[packageName]?.add(className)
                } else {
                    replaceClassMap.set(packageName, mutableListOf(className))
                }
            }
        }
    }
    return replaceClassMap
}

private val replaceAnnotationType: Type by lazy {
    Type.getType(Replace::class.java)
}

private fun handleFile(inputFile: File): List<Pair<String, String>> {
    val files: Array<File> = inputFile.listFiles()!!
    val replaceItemList: MutableSet<Pair<String, String>> = HashSet()
    for (file in files) {
        if (file.isDirectory) {
            val list = handleFile(file)
            list.forEach {
                replaceItemList.add(it)
            }
        } else if (file.isFile && file.name.endsWith(SdkConstants.DOT_CLASS, ignoreCase = true)) {
            val fileInputStream = FileInputStream(file)
            fileInputStream.use { inputStream ->
                try {
                    val classReader = ClassReader(inputStream)
                    if (classReader.isQualifiedClass.not()) {
                        return@use
                    }
                    val classNode = ClassNode()
                    classReader.accept(classNode, 0)
                    classNode.invisibleAnnotations?.forEach { annotationNode ->
                        if (annotationNode.desc == replaceAnnotationType.descriptor) {
                            val packageName = annotationNode.getValue<String>("name")
                            val className = classNode.name.plus(SdkConstants.DOT_CLASS)

                            logger("package name: $packageName, className: $className")
                            if (packageName != null && packageName.isNotEmpty()) {
                                replaceItemList.add(Pair(packageName, className))
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    logger("error: file: ${file.name}")
                }
            }
        }
    }
    return replaceItemList.toList()
}

fun ExtensionsPluginConfig.isTargetJar(jarName: String): Boolean {
    if (isEnableSdkPatcher.not()) {
        return false
    }
    val map = replaceClassMap ?: return false
    map.keys.forEach {
        if (jarName.contains(it)) {
            return true
        }
    }
    return false
}

fun ExtensionsPluginConfig.isTargetClass(jarName: String, className: String): Boolean {
    if (isEnableSdkPatcher.not()) {
        return false
    }

    val map = replaceClassMap ?: return false
    val keyList: MutableList<String> = ArrayList()
    map.keys.forEach {
        if (jarName.contains(it)) {
            keyList.add(it)
        }
    }

    keyList.forEach { key ->
        replaceClassMap?.getOrDefault(key, null)?.let {
            if (it.contains(className)) {
                return true
            }
        }
    }

    return false
}