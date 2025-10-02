package com.example.base_project.videoConverter

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.base_project.base.BaseVMActivity
import com.example.base_project.base.BaseViewModel
import com.example.base_project.base.setOnSingleClickListener
import com.example.base_project.databinding.ActivityVideoConverterBinding
import com.example.base_project.dialog.DialogSaveFile
import com.example.base_project.ext.formatFileSize
import com.example.base_project.ext.formatSecondsToTime
import com.example.base_project.ext.parcelable
import com.example.base_project.ext.playVideoPath
import com.example.base_project.folder.MyFolderActivity
import com.example.base_project.folder.enum.FolderType
import com.example.base_project.select.bean.Video
import com.example.base_project.util.AppConstance.VIDEO_ARG
import com.example.base_project.videoConverter.adapter.FormatType
import com.example.base_project.videoConverter.adapter.FormatTypeVideoAdapter
import com.example.base_project.videoConverter.adapter.toggetSelected
import com.example.base_project.videoConverter.presenter.VideoPresenter
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class VideoConverterActivity :
    BaseVMActivity<ActivityVideoConverterBinding, VideoConverterViewModel>(
        ActivityVideoConverterBinding::inflate
    ) {
    override val viewModel: VideoConverterViewModel by viewModels()
    private var defaultFormat = FormatType.MP3

    private val formatAdapter by lazy {
        FormatTypeVideoAdapter(onCLick = { format ->
            defaultFormat = format
        })
    }

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        onBackPressedDispatcher.addCallback {
            finish()
        }

        val videoArg = intent.extras?.parcelable<Video>(VIDEO_ARG)

        videoArg?.let { loadDetailVideo(it) }
        initAdapter()
    }

    @SuppressLint("SetTextI18n")
    private fun loadDetailVideo(video: Video) {
        viewModel.initVideo(video)
        Glide.with(this).load(video.path).into(binding.layoutAudio.icThumb)
        binding.layoutAudio.txtDuration.text = video.duration.formatSecondsToTime()
        binding.layoutAudio.txtSize.text = video.size.formatFileSize()
        binding.layoutAudio.txtType.text = video.extension.uppercase()
        binding.layoutAudio.txtTitle.text = video.title
        binding.txtVideoPath.text = "Path: ${video.path}"

        binding.layoutAudio.icPlay.setOnSingleClickListener {
            playVideoPath(video.path)
        }
    }

    fun initAdapter() {
        formatAdapter.toggetSelected(0)
        binding.recFormat.adapter = formatAdapter
        formatAdapter.checkFormat("")
    }

    override fun clickListener() {
        super.clickListener()
        binding.icBack.setOnSingleClickListener {
            finish()
        }

        binding.icDownload.setOnSingleClickListener {
            DialogSaveFile.show(supportFragmentManager) { fileName ->
                Log.d("TEST_FILE_NAME", "$fileName - ${defaultFormat.type}")
                showLoading(true)
                viewModel.executeFormatVideo(
                    fileName = fileName,
                    format = defaultFormat.type,
                    onFail = {
                        /*Toast.makeText(this, it, Toast.LENGTH_SHORT).show()*/
                        showLoading(false)
                    },
                    onProgress = { progress ->
                        Log.d("TEST_PROGRESS", progress.toString())
                    },
                    onSuccess = {
                        showLoading(false)
                        MyFolderActivity.onStart(
                            FolderType.VIDEO_CONVERTER,
                            this@VideoConverterActivity
                        )
                    }
                )
            }
        }
    }

}

@HiltViewModel
class VideoConverterViewModel @Inject constructor(private val videoPresenter: VideoPresenter) :
    BaseViewModel() {

    fun initVideo(video: Video) {
        videoPresenter.initVideo(video)
    }

    fun executeFormatVideo(
        fileName: String,
        format: String,
        onFail: (String) -> Unit = {},
        onSuccess: () -> Unit = {},
        onProgress: (Int) -> Unit = { },
    ) = viewModelScope.launch {
        videoPresenter.executeFormatVideo(fileName, format, onFail, onSuccess, onProgress)
    }
}