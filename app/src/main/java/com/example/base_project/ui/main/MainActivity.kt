package com.example.base_project.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.base_project.R
import com.example.base_project.applicattion.storage
import com.example.base_project.base.BaseVMActivity
import com.example.base_project.base.BaseViewModel
import com.example.base_project.base.setStatusBarColor
import com.example.base_project.util.AppConstance.TYPE_ARG
import com.example.base_project.databinding.ActivityMainBinding
import com.example.base_project.enum.Type
import com.example.base_project.folder.MyFolderActivity
import com.example.base_project.select.SelectAudioActivity
import com.example.base_project.ui.main.main.HomeDataView
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.base_project.ext.requestQueryAllFiles
import com.example.base_project.select.SelectVideoActivity
import com.example.base_project.ui.setting.SettingActivity

@AndroidEntryPoint
class MainActivity :
    BaseVMActivity<ActivityMainBinding, MainViewModel>(ActivityMainBinding::inflate) {

    override val viewModel: MainViewModel by viewModels()

    private val mainAdapter by lazy {
        MainAdapter(
            this,
            onSettingClick = {
                startActivity(Intent(this, SettingActivity::class.java))
            },
            onClickTrimAudio = {
                sendType(Type.TRIM)
            },
            onClickMergeAudio = {
                sendType(Type.MERGE_AUDIO)
            },
            onClickVideoConverter = {
                startActivity(Intent(this, SelectVideoActivity::class.java))
            },
            onClickMyFolder = {
                startActivity(Intent(this, MyFolderActivity::class.java))
            })
    }

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        requestQueryAllFiles()
        setStatusBarColor(R.color.white_color)

        if (!storage.passLanguage) {
            storage.passLanguage = true
        }

        binding.recyclerHome.adapter = mainAdapter

        viewModel.homeItems.observe(this) {
            mainAdapter.submitList(it)
        }

        permissionsResult.launch(arrayOf())
    }

    fun handleMenuItem(item: ItemOtherOption) {
        when (item) {
            ItemOtherOption.AUDIO_CONVERT -> {
                sendType(Type.AUDIO_CONVERTER)
            }

            ItemOtherOption.MIX -> {
                sendType(Type.MIX)
            }

            ItemOtherOption.SPLIT -> {
                sendType(Type.SPLIT)
            }

            ItemOtherOption.REMOVE_PART -> {
                sendType(Type.REMOVE_PART)
            }

            ItemOtherOption.MUTE_PART -> {
                sendType(Type.MUTE_PART)
            }

            ItemOtherOption.VOLUME -> {
                sendType(Type.VOLUME)
            }

            ItemOtherOption.SPEED -> {
                sendType(Type.SPEED)
            }

            ItemOtherOption.COMPRESS -> {
                sendType(Type.COMPRESS)
            }

            ItemOtherOption.REVERSE -> {
                sendType(Type.REVERSE)
            }
        }
    }

    private fun sendType(type: Type) {
        val intent = Intent(this, SelectAudioActivity::class.java)
        intent.putExtra(TYPE_ARG, type as Parcelable?)
        startActivity(intent)
    }

}

@HiltViewModel
class MainViewModel @Inject constructor() : BaseViewModel() {
    val defaultValues = listOf(
        HomeDataView.HeaderView,
        HomeDataView.MainFeatureView,
        HomeDataView.OtherView
    )

    val homeItems = MutableLiveData(defaultValues)

    private fun fetchData() {
        viewModelScope.launch {
            homeItems.postValue(defaultValues.toMutableList())
        }
    }

    init {
        fetchData()
    }
}