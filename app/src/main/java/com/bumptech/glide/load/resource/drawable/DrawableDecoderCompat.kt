package com.bumptech.glide.load.resource.drawable

import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.jiaoay.plugins.core.Replace

@Replace(name = "glide-4.13.2")
object DrawableDecoderCompat {

    @Volatile
    private var shouldCallAppCompatResources = true

    @JvmStatic
    fun getDrawable(
        ourContext: Context,
        targetContext: Context,
        @DrawableRes id: Int,
    ): Drawable? {
        return getDrawable(ourContext, targetContext, id, null)
    }

    @JvmStatic
    fun getDrawable(
        ourContext: Context,
        @DrawableRes id: Int,
        theme: Theme?,
    ): Drawable? {
        return getDrawable(ourContext, ourContext, id, theme)
    }

    private fun getDrawable(
        ourContext: Context,
        targetContext: Context,
        @DrawableRes id: Int,
        theme: Theme?,
    ): Drawable? {
        try {
            // Race conditions may cause us to attempt to load using v7 more than once. That's ok since
            // this check is a modest optimization and the output will be correct anyway.
            if (shouldCallAppCompatResources) {
                return loadDrawableV7(targetContext, id, theme)
            }
        } catch (error: NoClassDefFoundError) {
            shouldCallAppCompatResources = false
        } catch (e: IllegalStateException) {
            if (ourContext.packageName == targetContext.packageName) {
                throw e
            }
            return ContextCompat.getDrawable(targetContext, id)
        } catch (e: Resources.NotFoundException) {
            // Ignored, this can be thrown when drawable compat attempts to decode a canary resource. If
            // that decode attempt fails, we still want to try with the v4 ResourcesCompat below.
        }
        return loadDrawableV4(targetContext, id, theme ?: targetContext.theme)
    }

    private fun loadDrawableV7(
        context: Context,
        @DrawableRes id: Int,
        theme: Theme?,
    ): Drawable? {
        val resourceContext: Context = if (theme != null) {
            val contextThemeWrapper = ContextThemeWrapper(context, theme)
            contextThemeWrapper.applyOverrideConfiguration(theme.resources.configuration)
            contextThemeWrapper
        } else {
            context
        }
        return AppCompatResources.getDrawable(resourceContext, id)
    }

    private fun loadDrawableV4(context: Context, @DrawableRes id: Int, theme: Theme?): Drawable? {
        val resources: Resources = if (theme != null) {
            theme.resources
        } else {
            context.resources
        }
        return ResourcesCompat.getDrawable(resources, id, theme)
    }
}
