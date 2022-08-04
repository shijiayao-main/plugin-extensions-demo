package com.jiaoay.plugins.core.transform

import com.jiaoay.plugins.core.transform.TransformContext

/**
 * Represents the transform lifecycle listener
 *
 * @author johnsonlee
 */
interface TransformListener {

    fun onPreTransform(context: TransformContext) {}

    fun onPostTransform(context: TransformContext) {}

}