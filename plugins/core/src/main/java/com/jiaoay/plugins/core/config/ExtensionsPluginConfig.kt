package com.jiaoay.plugins.core.config

import org.gradle.api.Project

open class ExtensionsPluginConfig {

    companion object {
        internal const val Name = "extensionsPlugin"

        fun get(project: Project): ExtensionsPluginConfig {
            val config = project.extensions.getByName(Name)
            if (config is ExtensionsPluginConfig) {
                return config
            }
            return ExtensionsPluginConfig()
        }
    }

    var isEnableSdkPatcher: Boolean = false

    var replaceClassMap: Map<String, List<String>>? = null
}