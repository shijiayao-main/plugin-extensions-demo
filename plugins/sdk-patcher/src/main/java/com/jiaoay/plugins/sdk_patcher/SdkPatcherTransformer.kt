package com.jiaoay.plugins.sdk_patcher

import com.android.build.api.transform.TransformInvocation
import com.didiglobal.booster.gradle.project
import com.jiaoay.plugins.core.asm.ClassTransformer
import com.jiaoay.plugins.core.transform.TransformContext
import com.jiaoay.plugins.sdk_patcher.config.SdkPatcherConfig
import org.objectweb.asm.tree.ClassNode

class SdkPatcherTransformer : ClassTransformer {

    companion object {
        private const val TAG = "SdkPatcherTransformer"
    }

    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {

        if (context is TransformInvocation) {
            val pluginIsEnable = SdkPatcherConfig.get(context.project).isEnable
            if (pluginIsEnable.not()) {
                return super.transform(context, klass)
            }
        }

        println("$TAG transform, klass: ${klass.name}")
        return super.transform(context, klass)
    }
}