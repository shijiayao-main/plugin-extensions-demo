package com.jiaoay.plugins.sdk_patcher

import com.jiaoay.plugins.core.asm.ClassTransformer
import com.jiaoay.plugins.core.transform.TransformContext
import org.objectweb.asm.tree.ClassNode

class SdkPatcherTransformer : ClassTransformer {

    companion object {
        private const val TAG = "SdkPatcherTransformer"
    }

    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        println("$TAG transform, klass: ${klass.name}")
        return super.transform(context, klass)
    }
}