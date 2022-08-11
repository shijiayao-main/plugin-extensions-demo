package com.jiaoay.plugins.sdk_patcher.config

import com.jiaoay.plugins.core.config.PluginConfig

class SdkPatcherConfigImpl: PluginConfig {
    override fun getConfigClass(): Class<*> {
        return SdkPatcherConfig::class.java
    }

    override fun getConfigName(): String {
        return SdkPatcherConfig.Name
    }
}