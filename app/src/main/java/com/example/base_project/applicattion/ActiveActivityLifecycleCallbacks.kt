package com.example.base_project.applicattion

import android.app.Activity
import android.app.Application
import android.os.Bundle

class ActiveActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks{
    private var activeActivity: Activity? = null

    fun getActiveActivity(): Activity? = activeActivity

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activeActivity = activity
    }

    override fun onActivityStarted(p0: Activity) {}

    override fun onActivityResumed(p0: Activity) {
        activeActivity = p0
    }

    override fun onActivityPaused(p0: Activity) {}

    override fun onActivityStopped(p0: Activity) {}

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        if (activity === activeActivity) {
            activeActivity = null
        }
    }
}