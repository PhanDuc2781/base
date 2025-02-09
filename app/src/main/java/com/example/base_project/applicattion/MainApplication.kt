package com.example.base_project.applicattion

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import com.example.base_project.MainActivity
import com.example.base_project.ext.WindowInsetModel
import com.example.base_project.util.NetworkConnectionManager
import com.example.base_project.util.Storage
import com.example.base_project.util.isInternetAvailable
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    }

    fun getActiveActivity(): AppCompatActivity? =
        activityLifecycleCallbacks.getActiveActivity() as? AppCompatActivity

    suspend fun getFirebaseToken(): String? = withContext(Dispatchers.IO) {
        return@withContext suspendCoroutine<String?> {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    it.resume(token)
                } else {
                    it.resume(null)
                }
            }
        }
    }
}

fun activeActivity(): AppCompatActivity? = MainApplication.instance.getActiveActivity()

suspend fun firebaseToken(): String? = MainApplication.instance.getFirebaseToken()

val isInternetAvailable: Boolean
    get() = MainApplication.instance.isInternetAvailable()

val storage: Storage
    get() = MainApplication.instance.storage