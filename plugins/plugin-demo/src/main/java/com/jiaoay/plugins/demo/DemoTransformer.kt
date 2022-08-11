package com.jiaoay.plugins.demo

import com.android.build.api.transform.TransformInvocation
import com.didiglobal.booster.gradle.project
import com.jiaoay.plugins.core.transform.TransformContext
import com.jiaoay.plugins.core.transform.Transformer
import com.jiaoay.plugins.demo.config.DemoConfig
import org.gradle.api.logging.LogLevel
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

class DemoTransformer : Transformer {

    companion object {
        private const val TAG = "SdkPatcherTransformer"
    }

    private var config: DemoConfig? = null

    override fun onPreTransform(context: TransformContext) {
        super.onPreTransform(context)
        context.logger("onPreTransform: ")
    }

    override fun onPostTransform(context: TransformContext) {
        super.onPostTransform(context)
        context.logger("onPostTransform: ")
    }

    override fun transform(context: TransformContext, bytecode: ByteArray): ByteArray {
        val sdkPatcherConfig: DemoConfig? = this.config ?: let {
            if (context is TransformInvocation) {
                val config = DemoConfig.get(context.project)
                this.config = config
                config
            } else {
                null
            }
        }
        if (sdkPatcherConfig != null) {
            if (sdkPatcherConfig.isEnable.not()) {
                return bytecode
            }
        }
        val classReader = ClassReader(bytecode)

        if (classReader.isQualifiedClass.not()) {
            return bytecode
        }

        return ClassWriter(ClassWriter.COMPUTE_MAXS).also { writer ->
            val classNode = ClassNode().also { cn ->
                classReader.accept(cn, 0)
            }
//            if (classNode.name.contains("DrawableDecoderCompat", ignoreCase = true)) {
//                context.logger("name: ${classNode.name}, sourceFile: ${classNode.sourceFile}")
//            }
            context.logger("name: ${classNode.name}")
            classNode.accept(writer)
        }.toByteArray()
    }

    private val ClassReader.isQualifiedClass: Boolean
        get() {
            return !(access.and(Opcodes.ACC_INTERFACE) != 0 ||
                    access.and(Opcodes.ACC_ABSTRACT) != 0 ||
                    access.and(Opcodes.ACC_ENUM) != 0 ||
                    access.and(Opcodes.ACC_ANNOTATION) != 0 ||
                    access.and(Opcodes.ACC_MODULE) != 0 ||
                    access.and(Opcodes.ACC_SYNTHETIC) != 0)
        }


    private fun TransformContext.logger(message: String, level: LogLevel = LogLevel.ERROR) {
        if (this is TransformInvocation) {
            this.project.logger.log(level, "${config?.tag ?: TAG}: $message")
        }
    }
}