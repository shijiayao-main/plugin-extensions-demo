package com.jiaoay.extensions

import android.app.Application
import android.os.Looper
import android.util.Log
import com.jiaoay.plugins.core.PluginExtensions

class DemoApplication : Application() {

    companion object {
        private const val TAG = "DemoApplication"
    }

    override fun onCreate() {
        super.onCreate()
        PluginExtensions.output = output@{ info ->
            if (Thread.currentThread() == Looper.getMainLooper().thread) {
                if (info.getCostTime() < 5) {
                    return@output
                }
            } else {
                if (info.getCostTime() < 20) {
                    return@output
                }
            }
            Log.d(TAG, "PluginExtensions.output: $info")
        }
    }
}
