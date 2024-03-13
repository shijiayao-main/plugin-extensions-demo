package com.jiaoay.extensions

import android.app.Application
import android.util.Log
import com.jiaoay.plugins.core.PluginExtensions

class DemoApplication : Application() {

    companion object {
        private const val TAG = "DemoApplication"
    }

    override fun onCreate() {
        super.onCreate()
        PluginExtensions.output = {
            Log.d(TAG, "PluginExtensions.output: $it")
        }
    }
}
