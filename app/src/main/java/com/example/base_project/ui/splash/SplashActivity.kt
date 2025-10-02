package com.example.base_project.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.base_project.R
import com.example.base_project.applicattion.storage
import com.example.base_project.base.BaseActivity
import com.example.base_project.databinding.ActivitySplashBinding
import com.example.base_project.ui.main.MainActivity
import com.example.base_project.ui.main.language.LanguageActivity
import com.example.base_project.ui.tutorial.TutorialActivity
import com.example.base_project.util.AppConstance.FROM_SPLASH
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)

        lifecycleScope.launch {
            delay(5000)
            val targetScreen = if (storage.passLanguage) {
                TutorialActivity::class.java
            } else {
                LanguageActivity::class.java
            }
            val intent = Intent(this@SplashActivity, targetScreen ).apply {
                putExtra(FROM_SPLASH, true)
            }
            startActivity(intent)
            finish()
        }

        Glide.with(this).asGif().load(R.raw.anim_splash).into(binding.imgAnim)

    }
}