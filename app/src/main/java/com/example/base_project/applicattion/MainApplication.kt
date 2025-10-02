package com.example.base_project.applicattion

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import com.example.base_project.ext.WindowInsetModel
import com.example.base_project.ui.main.language.LanguageModel
import com.example.base_project.util.NetworkConnectionManager
import com.example.base_project.util.Storage
import com.example.base_project.util.isInternetAvailable
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltAndroidApp
class MainApplication : Application() {
    private val activityLifecycleCallbacks by lazy { ActiveActivityLifecycleCallbacks() }

    companion object {
        lateinit var instance: MainApplication
            private set

        var windowInset = WindowInsetModel(0, 0)
    }

    @Inject
    lateinit var storage: Storage

    override fun onCreate() {
        super.onCreate()
        instance = this
        storage.migrate()
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        NetworkConnectionManager.shared.registerForNetworkUpdates(this)

        checkAndUpdateFirstOpen()
    }

    private fun checkAndUpdateFirstOpen() {
        if (!storage.isFirstOpenApp) {
            val deviceLg = Locale.getDefault().language
            val index = LanguageModel.getIndex(deviceLg)
            storage.appLanguage = index
            LanguageModel.setCurrent(index)
        } else {
            val index = storage.appLanguage
            LanguageModel.setCurrent(index)
        }
        storage.isFirstOpenApp = true
    }

    fun getActiveActivity(): AppCompatActivity? =
        activityLifecycleCallbacks.getActiveActivity() as? AppCompatActivity
}

fun activeActivity(): AppCompatActivity? = MainApplication.instance.getActiveActivity()

val isInternetAvailable: Boolean
    get() = MainApplication.instance.isInternetAvailable()

val storage: Storage
    get() = MainApplication.instance.storage