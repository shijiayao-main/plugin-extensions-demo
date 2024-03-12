package com.jiaoay.extensions

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.jiaoay.plugin_extensions_demo.R
import com.jiaoay.plugin_extensions_demo.databinding.ActivityMainBinding
import com.jiaoay.plugins.trace.Trace

@Trace
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Glide.with(this)
            .asDrawable()
            .load(Uri.EMPTY)
            .placeholder(R.drawable.ic_launcher_background)
            .theme(theme)
            .into(binding.image)

        binding.image.setOnClickListener {
            if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES,
                )
            } else {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO,
                )
            }
        }
    }
}
