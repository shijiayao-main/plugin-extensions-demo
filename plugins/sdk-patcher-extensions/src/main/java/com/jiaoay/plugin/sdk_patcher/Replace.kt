package com.jiaoay.plugin.sdk_patcher

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class Replace(
    val name: String = ""
)