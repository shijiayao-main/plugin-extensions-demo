package com.jiaoay.plugins.core.task.spi

import com.android.build.gradle.api.BaseVariant

interface VariantProcessor {

    fun process(variant: BaseVariant)

}
