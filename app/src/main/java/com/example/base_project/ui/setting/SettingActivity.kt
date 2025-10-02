package com.example.base_project.ui.setting

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import com.example.base_project.base.BaseActivity
import com.example.base_project.base.setOnSingleClickListener
import com.example.base_project.databinding.ActivitySettingBinding
import com.example.base_project.ui.main.language.LanguageActivity
import com.example.base_project.util.AppConstance.FROM_SETTING

class SettingActivity : BaseActivity<ActivitySettingBinding>(ActivitySettingBinding::inflate) {
    private val adapter by lazy {
        SettingAdapter {
            handleItemClick(it)
        }
    }

    private fun handleItemClick(it: SettingItem) {
        when (it) {
            SettingItem.LANGUAGE -> {
                val intent = Intent(this, LanguageActivity::class.java).apply {
                    putExtra(FROM_SETTING, true)
                }
                startActivity(intent)
            }

            SettingItem.RATE_APP -> {

            }

            SettingItem.FEEDBACK -> {

            }

            SettingItem.PRIVACY_POLICY -> {

            }

            SettingItem.VERSION -> {

            }
        }
    }

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        onBackPressedDispatcher.addCallback { finish() }
        binding.appCompatImageView3.setOnSingleClickListener {
            finish()
        }

        binding.recSetting.adapter = adapter
    }

}