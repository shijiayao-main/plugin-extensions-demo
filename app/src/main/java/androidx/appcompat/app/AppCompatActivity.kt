/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.appcompat.app

import android.R
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.contextaware.OnContextAvailableListener
import androidx.activity.setViewTreeOnBackPressedDispatcherOwner
import androidx.annotation.CallSuper
import androidx.annotation.ContentView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.VectorEnabledTintResources
import androidx.core.app.ActivityCompat
import androidx.core.app.NavUtils
import androidx.core.app.TaskStackBuilder
import androidx.core.app.TaskStackBuilder.SupportParentable
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.jiaoay.extensions.shortToast
import com.jiaoay.plugins.core.Replace

/**
 * Base class for activities that wish to use some of the newer platform features on older
 * Android devices. Some of these backported features include:
 *
 *
 *  * Using the action bar, including action items, navigation modes and more with
 * the [.setSupportActionBar] API.
 *  * Built-in switching between light and dark themes by using the
 * [Theme.AppCompat.DayNight][androidx.appcompat.R.style.Theme_AppCompat_DayNight] theme
 * and [AppCompatDelegate.setDefaultNightMode] API.
 *  * Integration with `DrawerLayout` by using the
 * [.getDrawerToggleDelegate] API.
 *
 *
 *
 * Note that every activity that extends this class has to be themed with
 * [Theme.AppCompat][androidx.appcompat.R.style.Theme_AppCompat] or a theme that extends
 * that theme.
 *
 * <div class="special reference">
 * <h3>Developer Guides</h3>
 *
 *
 * For information about how to use the action bar, including how to add action items, navigation
 * modes and more, read the [Action
 * Bar]({@docRoot}guide/topics/ui/actionbar.html) API guide.
</div> *
 */
@Replace(name = "appcompat:1.6.1")
open class AppCompatActivity :
    FragmentActivity,
    AppCompatCallback,
    SupportParentable,
    ActionBarDrawerToggle.DelegateProvider {
    private var mDelegate: AppCompatDelegate? = null
    private var mResources: Resources? = null

    override fun onResume() {
        super.onResume()
        shortToast("onResume")
    }

    override fun onPause() {
        super.onPause()
        shortToast("onPause")
    }

    /**
     * Default constructor for AppCompatActivity. All Activities must have a default constructor
     * for API 27 and lower devices or when using the default
     * [android.app.AppComponentFactory].
     */
    constructor() : super() {
        initDelegate()
    }

    /**
     * Alternate constructor that can be used to provide a default layout
     * that will be inflated as part of `super.onCreate(savedInstanceState)`.
     *
     *
     * This should generally be called from your constructor that takes no parameters,
     * as is required for API 27 and lower or when using the default
     * [android.app.AppComponentFactory].
     *
     * @see .AppCompatActivity
     */
    @ContentView
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId) {
        initDelegate()
    }

    private fun initDelegate() {
        // TODO: Directly connect AppCompatDelegate to SavedStateRegistry
        savedStateRegistry.registerSavedStateProvider(
            DELEGATE_TAG,
        ) {
            val outState = Bundle()
            delegate.onSaveInstanceState(outState)
            outState
        }
        addOnContextAvailableListener(object : OnContextAvailableListener {
            override fun onContextAvailable(context: Context) {
                val delegate: AppCompatDelegate = delegate
                delegate.installViewFactory()
                delegate.onCreate(
                    savedStateRegistry.consumeRestoredStateForKey(DELEGATE_TAG),
                )
            }
        })
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(delegate.attachBaseContext2(newBase))
    }

    override fun setTheme(@StyleRes resId: Int) {
        super.setTheme(resId)
        delegate.setTheme(resId)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        delegate.onPostCreate(savedInstanceState)
    }

    val supportActionBar: ActionBar?
        /**
         * Support library version of [android.app.Activity.getActionBar].
         *
         *
         * Retrieve a reference to this activity's ActionBar.
         *
         * @return The Activity's ActionBar, or null if it does not have one.
         */
        get() = delegate.supportActionBar

    /**
     * Set a [Toolbar][android.widget.Toolbar] to act as the
     * [androidx.appcompat.app.ActionBar] for this Activity window.
     *
     *
     * When set to a non-null value the [.getActionBar] method will return
     * an [androidx.appcompat.app.ActionBar] object that can be used to control the given
     * toolbar as if it were a traditional window decor action bar. The toolbar's menu will be
     * populated with the Activity's options menu and the navigation button will be wired through
     * the standard [home][android.R.id.home] menu select action.
     *
     *
     * In order to use a Toolbar within the Activity's window content the application
     * must not request the window feature
     * [FEATURE_SUPPORT_ACTION_BAR][android.view.Window.FEATURE_ACTION_BAR].
     *
     * @param toolbar Toolbar to set as the Activity's action bar, or `null` to clear it
     */
    fun setSupportActionBar(toolbar: Toolbar?) {
        delegate.setSupportActionBar(toolbar)
    }

    override fun getMenuInflater(): MenuInflater {
        return delegate.menuInflater
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        initViewTreeOwners()
        delegate.setContentView(layoutResID)
    }

    override fun setContentView(view: View) {
        initViewTreeOwners()
        delegate.setContentView(view)
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams) {
        initViewTreeOwners()
        delegate.setContentView(view, params)
    }

    override fun addContentView(view: View, params: ViewGroup.LayoutParams) {
        initViewTreeOwners()
        delegate.addContentView(view, params)
    }

    private fun initViewTreeOwners() {
        // Set the view tree owners before setting the content view so that the inflation process
        // and attach listeners will see them already present
        window.decorView.setViewTreeLifecycleOwner(this)
        window.decorView.setViewTreeViewModelStoreOwner(this)
        window.decorView.setViewTreeSavedStateRegistryOwner(this)
        window.decorView.setViewTreeOnBackPressedDispatcherOwner(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // The delegate may modify the real resources object or the config param to implement its
        // desired configuration overrides. Let it do it's thing and then use the resulting state.
        delegate.onConfigurationChanged(newConfig)

        // Manually propagate configuration changes to our unmanaged resources object.
        if (mResources != null) {
            val currConfig = super.getResources().configuration
            val currMetrics = super.getResources().displayMetrics
            mResources!!.updateConfiguration(currConfig, currMetrics)
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        delegate.onPostResume()
    }

    override fun onStart() {
        super.onStart()
        delegate.onStart()
    }

    override fun onStop() {
        super.onStop()
        delegate.onStop()
    }

    override fun <T : View?> findViewById(@IdRes id: Int): T? {
        return delegate.findViewById(id)
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        if (super.onMenuItemSelected(featureId, item)) {
            return true
        }
        val ab = supportActionBar
        return if (item.itemId == R.id.home && ab != null && ab.displayOptions and ActionBar.DISPLAY_HOME_AS_UP != 0) {
            onSupportNavigateUp()
        } else {
            false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        delegate.onDestroy()
    }

    override fun onTitleChanged(title: CharSequence, color: Int) {
        super.onTitleChanged(title, color)
        delegate.setTitle(title)
    }

    /**
     * Enable extended support library window features.
     *
     *
     * This is a convenience for calling
     * [getWindow().requestFeature()][android.view.Window.requestFeature].
     *
     *
     * @param featureId The desired feature as defined in
     * [android.view.Window] or [androidx.core.view.WindowCompat].
     * @return Returns true if the requested feature is supported and now enabled.
     * @see android.app.Activity.requestWindowFeature
     *
     * @see android.view.Window.requestFeature
     */
    fun supportRequestWindowFeature(featureId: Int): Boolean {
        return delegate.requestWindowFeature(featureId)
    }

    @Suppress("deprecation")
    override fun supportInvalidateOptionsMenu() {
        delegate.invalidateOptionsMenu()
    }

    override fun invalidateOptionsMenu() {
        delegate.invalidateOptionsMenu()
    }

    /**
     * Notifies the Activity that a support action mode has been started.
     * Activity subclasses overriding this method should call the superclass implementation.
     *
     * @param mode The new action mode.
     */
    @CallSuper
    override fun onSupportActionModeStarted(mode: ActionMode) {
    }

    /**
     * Notifies the activity that a support action mode has finished.
     * Activity subclasses overriding this method should call the superclass implementation.
     *
     * @param mode The action mode that just finished.
     */
    @CallSuper
    override fun onSupportActionModeFinished(mode: ActionMode) {
    }

    /**
     * Called when a support action mode is being started for this window. Gives the
     * callback an opportunity to handle the action mode in its own unique and
     * beautiful way. If this method returns null the system can choose a way
     * to present the mode or choose not to start the mode at all.
     *
     * @param callback Callback to control the lifecycle of this action mode
     * @return The ActionMode that was started, or null if the system should present it
     */
    override fun onWindowStartingSupportActionMode(callback: ActionMode.Callback): ActionMode? {
        return null
    }

    /**
     * Start an action mode.
     *
     * @param callback Callback that will manage lifecycle events for this context mode
     * @return The ContextMode that was started, or null if it was canceled
     */
    fun startSupportActionMode(callback: ActionMode.Callback): ActionMode? {
        return delegate.startSupportActionMode(callback)
    }

    @Deprecated("Progress bars are no longer provided in AppCompat.")
    fun setSupportProgressBarVisibility(visible: Boolean) {
    }

    @Deprecated("Progress bars are no longer provided in AppCompat.")
    fun setSupportProgressBarIndeterminateVisibility(visible: Boolean) {
    }

    @Deprecated("Progress bars are no longer provided in AppCompat.")
    fun setSupportProgressBarIndeterminate(indeterminate: Boolean) {
    }

    @Deprecated("Progress bars are no longer provided in AppCompat.")
    fun setSupportProgress(progress: Int) {
    }

    /**
     * Support version of [.onCreateNavigateUpTaskStack].
     * This method will be called on all platform versions.
     *
     *
     * Define the synthetic task stack that will be generated during Up navigation from
     * a different task.
     *
     *
     * The default implementation of this method adds the parent chain of this activity
     * as specified in the manifest to the supplied [androidx.core.app.TaskStackBuilder]. Applications
     * may choose to override this method to construct the desired task stack in a different
     * way.
     *
     *
     * This method will be invoked by the default implementation of [.onNavigateUp]
     * if [.shouldUpRecreateTask] returns true when supplied with the intent
     * returned by [.getParentActivityIntent].
     *
     *
     * Applications that wish to supply extra Intent parameters to the parent stack defined
     * by the manifest should override
     * [.onPrepareSupportNavigateUpTaskStack].
     *
     * @param builder An empty TaskStackBuilder - the application should add intents representing
     * the desired task stack
     */
    fun onCreateSupportNavigateUpTaskStack(builder: TaskStackBuilder) {
        builder.addParentStack(this)
    }

    /**
     * Support version of [.onPrepareNavigateUpTaskStack].
     * This method will be called on all platform versions.
     *
     *
     * Prepare the synthetic task stack that will be generated during Up navigation
     * from a different task.
     *
     *
     * This method receives the [androidx.core.app.TaskStackBuilder] with the constructed series of
     * Intents as generated by [.onCreateSupportNavigateUpTaskStack].
     * If any extra data should be added to these intents before launching the new task,
     * the application should override this method and add that data here.
     *
     * @param builder A TaskStackBuilder that has been populated with Intents by
     * onCreateNavigateUpTaskStack.
     */
    fun onPrepareSupportNavigateUpTaskStack(builder: TaskStackBuilder) {}

    /**
     * This method is called whenever the user chooses to navigate Up within your application's
     * activity hierarchy from the action bar.
     *
     *
     * If a parent was specified in the manifest for this activity or an activity-alias to it,
     * default Up navigation will be handled automatically. See
     * [.getSupportParentActivityIntent] for how to specify the parent. If any activity
     * along the parent chain requires extra Intent arguments, the Activity subclass
     * should override the method [.onPrepareSupportNavigateUpTaskStack]
     * to supply those arguments.
     *
     *
     * See [Tasks and
 * Back Stack]({@docRoot}guide/topics/fundamentals/tasks-and-back-stack.html) from the developer guide and
     * [Navigation]({@docRoot}design/patterns/navigation.html) from the design guide
     * for more information about navigating within your app.
     *
     *
     * See the [androidx.core.app.TaskStackBuilder] class and the Activity methods
     * [.getSupportParentActivityIntent], [.supportShouldUpRecreateTask], and
     * [.supportNavigateUpTo] for help implementing custom Up navigation.
     *
     * @return true if Up navigation completed successfully and this Activity was finished,
     * false otherwise.
     */
    fun onSupportNavigateUp(): Boolean {
        val upIntent = supportParentActivityIntent
        if (upIntent != null) {
            if (supportShouldUpRecreateTask(upIntent)) {
                val b = TaskStackBuilder.create(this)
                onCreateSupportNavigateUpTaskStack(b)
                onPrepareSupportNavigateUpTaskStack(b)
                b.startActivities()
                try {
                    ActivityCompat.finishAffinity(this)
                } catch (e: IllegalStateException) {
                    // This can only happen on 4.1+, when we don't have a parent or a result set.
                    // In that case we should just finish().
                    finish()
                }
            } else {
                // This activity is part of the application's task, so simply
                // navigate up to the hierarchical parent activity.
                supportNavigateUpTo(upIntent)
            }
            return true
        }
        return false
    }

    /**
     * Obtain an [android.content.Intent] that will launch an explicit target activity
     * specified by sourceActivity's [androidx.core.app.NavUtils.PARENT_ACTIVITY] &lt;meta-data&gt;
     * element in the application's manifest. If the device is running
     * Jellybean or newer, the android:parentActivityName attribute will be preferred
     * if it is present.
     *
     * @return a new Intent targeting the defined parent activity of sourceActivity
     */
    override fun getSupportParentActivityIntent(): Intent? {
        return NavUtils.getParentActivityIntent(this)
    }

    /**
     * Returns true if sourceActivity should recreate the task when navigating 'up'
     * by using targetIntent.
     *
     *
     * If this method returns false the app can trivially call
     * [.supportNavigateUpTo] using the same parameters to correctly perform
     * up navigation. If this method returns false, the app should synthesize a new task stack
     * by using [androidx.core.app.TaskStackBuilder] or another similar mechanism to perform up navigation.
     *
     * @param targetIntent An intent representing the target destination for up navigation
     * @return true if navigating up should recreate a new task stack, false if the same task
     * should be used for the destination
     */
    fun supportShouldUpRecreateTask(targetIntent: Intent): Boolean {
        return NavUtils.shouldUpRecreateTask(this, targetIntent)
    }

    /**
     * Navigate from sourceActivity to the activity specified by upIntent, finishing sourceActivity
     * in the process. upIntent will have the flag [android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP] set
     * by this method, along with any others required for proper up navigation as outlined
     * in the Android Design Guide.
     *
     *
     * This method should be used when performing up navigation from within the same task
     * as the destination. If up navigation should cross tasks in some cases, see
     * [.supportShouldUpRecreateTask].
     *
     * @param upIntent An intent representing the target destination for up navigation
     */
    fun supportNavigateUpTo(upIntent: Intent) {
        NavUtils.navigateUpTo(this, upIntent)
    }

    @Suppress("deprecation")
    override fun onContentChanged() {
        // Call onSupportContentChanged() for legacy reasons
        onSupportContentChanged()
    }

    @Deprecated("Use {@link #onContentChanged()} instead.")
    fun onSupportContentChanged() {
    }

    override fun getDrawerToggleDelegate(): ActionBarDrawerToggle.Delegate? {
        return delegate.drawerToggleDelegate
    }

    /**
     * {@inheritDoc}
     *
     *
     * Please note: AppCompat uses its own feature id for the action bar:
     * [FEATURE_SUPPORT_ACTION_BAR][AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR].
     */
    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        return super.onMenuOpened(featureId, menu)
    }

    /**
     * {@inheritDoc}
     *
     *
     * Please note: AppCompat uses its own feature id for the action bar:
     * [FEATURE_SUPPORT_ACTION_BAR][AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR].
     */
    override fun onPanelClosed(featureId: Int, menu: Menu) {
        super.onPanelClosed(featureId, menu)
    }

    val delegate: AppCompatDelegate
        /**
         * @return The [AppCompatDelegate] being used by this Activity.
         */
        get() {
            mDelegate?.let {
                return it
            }
            val appCompatDelegate = AppCompatDelegate.create(this, this)
            mDelegate = appCompatDelegate
            return appCompatDelegate
        }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // Let support action bars open menus in response to the menu key prioritized over
        // the window handling it
        val keyCode = event.keyCode
        val actionBar = supportActionBar
        return if (keyCode == KeyEvent.KEYCODE_MENU && actionBar != null && actionBar.onMenuKeyEvent(
                event,
            )
        ) {
            true
        } else {
            super.dispatchKeyEvent(event)
        }
    }

    override fun getResources(): Resources {
        if (mResources == null && VectorEnabledTintResources.shouldBeUsed()) {
            mResources = VectorEnabledTintResources(this, super.getResources())
        }
        return if (mResources == null) super.getResources() else mResources!!
    }

    /**
     * KeyEvents with non-default modifiers are not dispatched to menu's performShortcut in API 25
     * or lower. Here, we check if the keypress corresponds to a menuitem's shortcut combination
     * and perform the corresponding action.
     */
    private fun performMenuItemShortcut(event: KeyEvent): Boolean {
        if ((
                Build.VERSION.SDK_INT < 26 && !event.isCtrlPressed &&
                    !KeyEvent.metaStateHasNoModifiers(event.metaState) && event.repeatCount == 0
                ) && !KeyEvent.isModifierKey(
                event.keyCode,
            )
        ) {
            val currentWindow = window
            if (currentWindow != null && currentWindow.decorView != null) {
                val decorView = currentWindow.decorView
                if (decorView.dispatchKeyShortcutEvent(event)) {
                    return true
                }
            }
        }
        return false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (performMenuItemShortcut(event)) {
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    override fun openOptionsMenu() {
        val actionBar = supportActionBar
        if (window.hasFeature(Window.FEATURE_OPTIONS_PANEL) &&
            (actionBar == null || !actionBar.openOptionsMenu())
        ) {
            super.openOptionsMenu()
        }
    }

    override fun closeOptionsMenu() {
        val actionBar = supportActionBar
        if (window.hasFeature(Window.FEATURE_OPTIONS_PANEL) &&
            (actionBar == null || !actionBar.closeOptionsMenu())
        ) {
            super.closeOptionsMenu()
        }
    }

    /**
     * Called when the night mode has changed. See [AppCompatDelegate.applyDayNight] for
     * more information.
     *
     * @param mode the night mode which has been applied
     */
    protected fun onNightModeChanged(@AppCompatDelegate.NightMode mode: Int) {}

    /**
     * Called when the locales have been changed. See [AppCompatDelegate.applyAppLocales]
     * for more information.
     *
     * @param locales the localeListCompat which has been applied
     */
    protected fun onLocalesChanged(locales: LocaleListCompat) {}

    companion object {
        private const val DELEGATE_TAG = "androidx:appcompat"
    }
}
