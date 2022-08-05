package com.jiaoay.plugins.core

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.didiglobal.booster.gradle.GTE_V3_6
import com.didiglobal.booster.gradle.getAndroid
import com.jiaoay.plugins.core.task.spi.VariantProcessor
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.reflect.TypeOf

class ExtensionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.findByName("android") ?: throw GradleException("$project is not an Android project")

        if (!GTE_V3_6) {
            project.gradle.addListener(
                TransformTaskExecutionListener(
                    project = project
                )
            )
        }

//         AutoService
        val processors = loadVariantProcessors(project)
        if (project.state.executed) {
            project.setup(processors)
        } else {
            project.afterEvaluate {
                project.setup(processors)
            }
        }
    }

    private fun Project.setup(processors: List<VariantProcessor>) {
        val android = project.getAndroid<BaseExtension>()
        val extensions: AppExtension? = project.extensions.findByType(TypeOf.typeOf(AppExtension::class.java))
        when (android) {
            is AppExtension -> android.applicationVariants
            is LibraryExtension -> android.libraryVariants
            else -> emptyList<BaseVariant>()
        }.takeIf<Collection<BaseVariant>>(Collection<BaseVariant>::isNotEmpty)?.let { variants ->
            extensions?.registerTransform(PluginTransform.newInstance(project))
            variants.forEach { variant ->
                processors.forEach { processor ->
                    processor.process(variant)
                }
            }
        }
    }

}