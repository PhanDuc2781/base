package com.example.base_project.audio_converter

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.base_project.R
import com.example.base_project.audio_converter.presenter.AudioConverterPresenter
import com.example.base_project.base.BaseVMActivity
import com.example.base_project.base.BaseViewModel
import com.example.base_project.base.setOnSingleClickListener
import com.example.base_project.databinding.ActivityAudioConverterBinding
import com.example.base_project.dialog.DialogSaveFile
import com.example.base_project.ext.formatFileSize
import com.example.base_project.ext.formatSecondsToTime
import com.example.base_project.ext.parcelable
import com.example.base_project.folder.MyFolderActivity
import com.example.base_project.folder.enum.FolderType
import com.example.base_project.select.bean.Audio
import com.example.base_project.sound.SoundManager
import com.example.base_project.util.AppConstance.AUDIO_ARG
import com.example.base_project.videoConverter.adapter.FormatTypeVideoAdapter
import com.example.base_project.videoConverter.adapter.toggetSelected
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AudioConverterActivity :
    BaseVMActivity<ActivityAudioConverterBinding, AudioConverterViewModel>(
        ActivityAudioConverterBinding::inflate
    ) {
    override val viewModel: AudioConverterViewModel by viewModels()
    private var audio: Audio? = null
    private var defaultFormat = ""

    private val formatAdapter by lazy {
        FormatTypeVideoAdapter(onCLick = { format ->
            defaultFormat = format.type
        }, false)
    }

    @Inject
    lateinit var soundManager: SoundManager

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        onBackPressedDispatcher.addCallback {
            finish()
        }

        audio = intent.extras?.parcelable<Audio>(AUDIO_ARG)
        audio?.let {
            loadDetailAudio(it)
        }

        initAdapter()
    }

    private fun initAdapter() {
        binding.recFormat.adapter = formatAdapter
        formatAdapter.toggetSelected(0)
        audio?.let { formatAdapter.checkFormat(it.extension) }
    }

    @SuppressLint("SetTextI18n")
    private fun loadDetailAudio(audio: Audio) {
        defaultFormat = audio.extension
        binding.itemAudio.icThumb.setImageResource(R.drawable.ic_thumb_default)
        binding.itemAudio.txtTitle.apply {
            text = audio.title
            isSelected = true
        }
        binding.itemAudio.txtDuration.text = audio.duration.formatSecondsToTime()
        binding.itemAudio.txtSize.text = audio.size.formatFileSize()
        binding.itemAudio.txtType.text = audio.extension.uppercase()
        binding.txtPath.text = "Path: ${audio.path}"

        binding.itemAudio.icPlay.setOnSingleClickListener {
            viewModel.isPlaying.value = !viewModel.isPlaying.value!!
        }


        viewModel.isPlaying.observe(this) {
            binding.itemAudio.icPlay.setImageResource(if (it) R.drawable.ic_pause else R.drawable.ic_play)
            if (it) {
                soundManager.playSound(audioModel = audio)
            } else {
                soundManager.pauseSound()
            }
        }
    }


    override fun clickListener() {
        super.clickListener()
        binding.icBack.setOnClickListener {
            finish()
        }

        binding.icDownload.setOnSingleClickListener {
            DialogSaveFile.show(supportFragmentManager) { fileName ->
                showLoading(true)
                viewModel.executeConvert(
                    audio = audio ?: return@show,
                    fileName = fileName,
                    type = defaultFormat,
                    onFail = {
                        showLoading(false)
                        Log.d("ON_CONVERT", it)
                    },
                    onProgress = {
                        Log.d("ON_CONVERT", "$it")
                    },
                    onSuccess = {
                        showLoading(false)
                        MyFolderActivity.onStart(FolderType.AUDIO_CONVERTER , this)
                    },
                    onStart = {}
                )
            }
        }
    }
}


@HiltViewModel
class AudioConverterViewModel @Inject constructor(private val convertPresenter: AudioConverterPresenter) :
    BaseViewModel() {
    val isPlaying = MutableLiveData(false)


    fun executeConvert(
        audio: Audio,
        fileName: String,
        type: String,
        onStart: () -> Unit = {},
        onFail: (String) -> Unit = {},
        onSuccess: () -> Unit = {},
        onProgress: (Int) -> Unit = { },
    ) = viewModelScope.launch {
        convertPresenter.executeConvert(
            audio,
            fileName,
            type,
            onFail,
            onSuccess,
            onProgress,
            onStart
        )
    }
}
