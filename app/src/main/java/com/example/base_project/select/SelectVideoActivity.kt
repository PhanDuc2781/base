package com.example.base_project.select

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.example.base_project.base.BaseVMActivity
import com.example.base_project.base.BaseViewModel
import com.example.base_project.databinding.ActivitySelectVideoBinding
import com.example.base_project.ext.playVideoPath
import com.example.base_project.select.adapter.SelectVideoAdapter
import com.example.base_project.select.bean.Video
import com.example.base_project.select.model.SelectVideoModel
import com.example.base_project.util.AppConstance.VIDEO_ARG
import com.example.base_project.videoConverter.VideoConverterActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SelectVideoActivity :
    BaseVMActivity<ActivitySelectVideoBinding, SelectVideoViewModel>(ActivitySelectVideoBinding::inflate) {
    override val viewModel: SelectVideoViewModel by viewModels()

    private val videoAdapter by lazy {
        SelectVideoAdapter(this, onClickItem = { video ->
            val intent = Intent(this, VideoConverterActivity::class.java).apply {
                putExtra(VIDEO_ARG, video)
            }
            startActivity(intent)
        }, onClickPlay = {
            playVideoPath(it.path)
        })
    }

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        onBackPressedDispatcher.addCallback { finish() }


        initAdapter()
        initData()
    }

    private fun initData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentList.collect {
                    videoAdapter.submitList(it)
                }
            }
        }
    }

    override fun clickListener() {
        super.clickListener()
        binding.icBack.setOnClickListener {
            finish()
        }

        binding.swipeLayout.setOnRefreshListener {
            viewModel.getVideoList()
            binding.swipeLayout.isRefreshing = false
        }
    }

    private fun initAdapter() {
        binding.recAudio.adapter = videoAdapter
    }
}

@HiltViewModel
class SelectVideoViewModel @Inject constructor() : BaseViewModel() {
    private val _currentList = MutableStateFlow<List<Video>>(emptyList())
    val currentList: StateFlow<List<Video>> = _currentList

    fun getVideoList() = viewModelScope.launch {
        SelectVideoModel.queryVideoList().collect { result ->
            when (result) {
                is Result.Success -> {
                    showProgress(false)
                    _currentList.value = result.data
                }

                is Result.Error -> {
                    showProgress(false)
                }

                is Result.Loading -> {
                    showProgress(true)
                }
            }
        }
    }

    init {
        getVideoList()
    }
}