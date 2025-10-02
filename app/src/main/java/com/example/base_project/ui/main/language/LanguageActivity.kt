package com.example.base_project.ui.main.language

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import com.example.base_project.R
import com.example.base_project.applicattion.storage
import com.example.base_project.base.BaseVMActivity
import com.example.base_project.base.BaseViewModel
import com.example.base_project.base.setOnSingleClickListener
import com.example.base_project.databinding.ActivityLanguageBinding
import com.example.base_project.ext.gone
import com.example.base_project.ext.show
import com.example.base_project.ui.main.MainActivity
import com.example.base_project.ui.tutorial.TutorialActivity
import com.example.base_project.util.AppConstance.FROM_SPLASH
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class LanguageActivity : BaseVMActivity<ActivityLanguageBinding, LanguageViewModel>(
    ActivityLanguageBinding::inflate
) {
    override val viewModel: LanguageViewModel by viewModels()
    private lateinit var adapter: LanguageAdapter
    private var selected = -1
    private var isSelected = false
    private var isChooseLanguage = false

    private var fromSplash = false


    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        fromSplash = intent.extras?.getBoolean(FROM_SPLASH, false) == true
        if (fromSplash) binding.imageView10.gone() else binding.imageView10.show()

        val currentLanguage = LanguageModel.get(storage.appLanguage)
        currentLanguage?.let {
            binding.txtLg.text = getString(it.nameLanguage)
            binding.icFlag.setImageResource(it.iconFlag)
        }

        onBackPressedDispatcher.addCallback {
            if (!fromSplash) finish()
            return@addCallback
        }

        binding.imageView10.setOnSingleClickListener {
            finish()
        }

        adapter = LanguageAdapter(this) {
            viewModel.isChooseCurrentLanguage.value = false
            selected = it.id
            isChooseLanguage = true
            binding.imageView11.show()
        }

        binding.recLanguage.adapter = adapter
    }

    override fun clickListener() {
        super.clickListener()
        binding.relativeLayout9.setOnSingleClickListener {
            viewModel.isChooseCurrentLanguage.value = !viewModel.isChooseCurrentLanguage.value!!
        }

        binding.imageView11.setOnSingleClickListener {
            if (!isChooseLanguage) {
                Toast.makeText(this, getString(R.string.please_choose_language), Toast.LENGTH_SHORT).show()
                return@setOnSingleClickListener
            }

            storage.appLanguage = selected
            LanguageModel.setCurrent(selected)
            LocaleHelper.setLocale(this@LanguageActivity, LanguageModel.current.localizeCode, false)

            val targetIntent = if (fromSplash) {
                TutorialActivity::class.java
            } else {
                MainActivity::class.java
            }
            val intent = Intent(this, targetIntent).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)

            setResult(RESULT_OK)
            finish()
        }

        viewModel.isChooseCurrentLanguage.observe(this) {
            if (it) {
                binding.imageView11.show()
                isChooseLanguage = true
                binding.relativeLayout9.setBackgroundResource(R.drawable.bg_choose_lg)
                binding.icRadio.setImageResource(R.drawable.ic_radio_checked)
                adapter.clearSelection()
            } else {
                binding.relativeLayout9.setBackgroundResource(R.drawable.bg_language)
                binding.icRadio.setImageResource(R.drawable.ic_unselected_languae)
            }
        }
    }
}

@HiltViewModel
class LanguageViewModel @Inject constructor() : BaseViewModel() {
    val isChooseLanguage = MutableLiveData(false)
    val isChooseCurrentLanguage = MutableLiveData(false)
}