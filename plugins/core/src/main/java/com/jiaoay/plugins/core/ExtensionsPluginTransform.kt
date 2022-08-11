package com.jiaoay.plugins.core

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.didiglobal.booster.gradle.GTE_V3_4
import com.didiglobal.booster.gradle.SCOPE_FULL_PROJECT
import com.didiglobal.booster.gradle.SCOPE_FULL_WITH_FEATURES
import com.didiglobal.booster.gradle.SCOPE_PROJECT
import com.didiglobal.booster.gradle.project
import com.jiaoay.plugins.core.config.ExtensionsPluginConfig
import com.jiaoay.plugins.core.internal.PluginTransformV34
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.io.FileInputStream

open class ExtensionsPluginTransform protected constructor(
    internal val parameter: TransformParameter
) : Transform() {

    internal val verifyEnabled by lazy {
        parameter.properties[OPT_TRANSFORM_VERIFY]?.toString()?.toBoolean() ?: false
    }

    override fun getName() = parameter.name

    override fun isIncremental() = !verifyEnabled

    override fun isCacheable() = !verifyEnabled

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> = TransformManager.CONTENT_CLASS

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> = when {
        parameter.transformers.isEmpty() -> mutableSetOf()
        parameter.plugins.hasPlugin("com.android.library") -> SCOPE_PROJECT
        parameter.plugins.hasPlugin("com.android.application") -> SCOPE_FULL_PROJECT
        parameter.plugins.hasPlugin("com.android.dynamic-feature") -> SCOPE_FULL_WITH_FEATURES
        else -> TODO("Not an Android project")
    }

    override fun getReferencedScopes(): MutableSet<in QualifiedContent.Scope> = when {
        parameter.transformers.isEmpty() -> when {
            parameter.plugins.hasPlugin("com.android.library") -> SCOPE_PROJECT
            parameter.plugins.hasPlugin("com.android.application") -> SCOPE_FULL_PROJECT
            parameter.plugins.hasPlugin("com.android.dynamic-feature") -> SCOPE_FULL_WITH_FEATURES
            else -> TODO("Not an Android project")
        }
        else -> super.getReferencedScopes()
    }

    private var config: ExtensionsPluginConfig? = null

    final override fun transform(invocation: TransformInvocation) {

        val extensionsPluginConfig: ExtensionsPluginConfig = config ?: let {
            val config = ExtensionsPluginConfig.get(invocation.project)
            this.config = config
            config
        }

        if (extensionsPluginConfig.isEnableSdkPatcher) {
            initSdkPatcher(invocation = invocation)
        }

        PluginTransformInvocation(invocation, this).apply {

            // TODO: 判断下是否启用替换jar中class的功能，启用需要处理下这种情况
            logger("isIncremental: $isIncremental")
            if (isIncremental) {
                doIncrementalTransform()
            } else {
                outputProvider?.deleteAll()
                doFullTransform()
            }
        }
    }

    /**
     * 找到那些类添加了注解
     */
    private fun initSdkPatcher(invocation: TransformInvocation) {
        // 不支持增量编译
        val outputProvider = invocation.outputProvider
        if (invocation.isIncremental.not()) {
            outputProvider.deleteAll()
        }
        invocation.inputs.forEach { input ->
            // TODO: 当前仅处理目录中的，如果之后jar中也有，再单独处理
            input.directoryInputs.forEach { directoryInput ->
                val file = directoryInput.file
                if (file.isDirectory.not()) {
                    return
                }
                handleFile(inputFile = file)
            }
        }
    }

    private fun handleFile(inputFile: File) {
        val files: Array<File> = inputFile.listFiles()!!
        for (file in files) {
            if (file.isDirectory) {
                handleFile(file)
            } else if (file.isFile) {
                val fileInputStream = FileInputStream(file)
                fileInputStream.use { inputStream ->
                    try {
                        val classReader = ClassReader(inputStream)
                        if (classReader.isQualifiedClass.not()) {
                            return@use
                        }
                        val classNode = ClassNode()
                        classReader.accept(classNode, 0)
                        logger("className: ${classNode.name}")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        logger("error: file: ${file.name}")
                    }
                }
            }
        }
    }

    companion object {

        fun newInstance(project: Project, name: String = "plugin-extensions"): ExtensionsPluginTransform {
            val parameter = project.newTransformParameter(name)
            return when {
                GTE_V3_4 -> PluginTransformV34(parameter)
                else -> ExtensionsPluginTransform(parameter)
            }
        }

    }

}

/**
 * The option for transform outputs verifying, default is false
 */
private const val OPT_TRANSFORM_VERIFY = "booster.transform.verify"
