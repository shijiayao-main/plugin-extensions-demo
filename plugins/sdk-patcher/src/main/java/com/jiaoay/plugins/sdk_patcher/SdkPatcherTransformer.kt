package com.jiaoay.plugins.sdk_patcher

import com.didiglobal.booster.transform.TransformContext
import com.jiaoay.plugins.core.asm.ClassTransformer
import com.google.auto.service.AutoService
import org.objectweb.asm.tree.ClassNode

@AutoService(ClassTransformer::class)
class SdkPatcherTransformer : ClassTransformer {

    companion object {
        private const val TAG = "SdkPatcherTransformer"
    }

    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        println("$TAG transform, klass: ${klass.name}")
        throw RuntimeException()
//        return super.transform(context, klass)
    }
}