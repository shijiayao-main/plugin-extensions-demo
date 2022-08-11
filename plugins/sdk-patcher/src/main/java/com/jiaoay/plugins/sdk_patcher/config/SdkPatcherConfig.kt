package com.jiaoay.plugins.sdk_patcher.config

import org.gradle.api.Project

open class SdkPatcherConfig {

    companion object {
        const val Name = "sdkPatcher"

        fun get(project: Project): SdkPatcherConfig {
            val config = project.extensions.getByName(Name)
            if (config is SdkPatcherConfig) {
                return config
            }
            return SdkPatcherConfig()
        }
    }

    var isEnable: Boolean = false
}
