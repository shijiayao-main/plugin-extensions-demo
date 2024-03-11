package com.jiaoay.extensions

import android.content.Context
import android.os.Looper
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val mainScope = CoroutineScope(Dispatchers.Main)

fun Context.shortToast(message: String) {
    if (Looper.myLooper() == Looper.myLooper()) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    } else {
        mainScope.launch {
            Toast.makeText(this@shortToast, message, Toast.LENGTH_SHORT).show()
        }
    }
}
