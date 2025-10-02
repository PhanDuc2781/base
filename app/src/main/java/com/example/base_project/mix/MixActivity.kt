package com.example.base_project.mix

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.example.base_project.R
import com.example.base_project.base.BaseVMActivity
import com.example.base_project.base.BaseViewModel
import com.example.base_project.base.setOnSingleClickListener
import com.example.base_project.databinding.ActivityMixBinding
import com.example.base_project.dialog.DialogSaveFile
import com.example.base_project.ext.formatFileSize
import com.example.base_project.ext.formatSecondsToTime
import com.example.base_project.ext.getParcelableArrayList
import com.example.base_project.ext.gone
import com.example.base_project.ext.onSeekChange
import com.example.base_project.folder.MyFolderActivity
import com.example.base_project.folder.enum.FolderType
import com.example.base_project.mix.presenter.MixPresenter
import com.example.base_project.select.bean.Audio
import com.example.base_project.sound.SoundManager
import com.example.base_project.util.AppConstance.AUDIO_SELECTED_ARG
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min

@AndroidEntryPoint
class MixActivity : BaseVMActivity<ActivityMixBinding, MixViewModel>(ActivityMixBinding::inflate) {
    override val viewModel: MixViewModel by viewModels()
    private var audioList: List<Audio> = mutableListOf()

    @Inject
    lateinit var sound1: SoundManager

    @Inject
    lateinit var sound2: SoundManager

    private var totalDuration = 0
    private var job: Job? = null
    private var currentSeconds = 0

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        onBackPressedDispatcher.addCallback { finish() }

        audioList = intent.getParcelableArrayList<Audio>(AUDIO_SELECTED_ARG) ?: emptyList()
        Log.d("AUDIO_LIST_MIX", "setupView: $audioList")

        initData(audioList)

        val minDurationItem = audioList.minByOrNull { it.duration }

        minDurationItem?.let {
            totalDuration = it.duration
            binding.progressCircular.max = totalDuration
            binding.timeEnd.text = totalDuration.formatSecondsToTime()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initData(audio: List<Audio>) {
        /*audio 1*/
        binding.layoutAudio1.layoutAudio.txtType.text = audio[0].extension
        binding.layoutAudio1.layoutAudio.txtTitle.text = audio[0].title
        binding.layoutAudio1.layoutAudio.txtDuration.text = audio[0].duration.formatSecondsToTime()
        binding.layoutAudio1.layoutAudio.txtSize.text = audio[0].size.formatFileSize()
        binding.layoutAudio1.layoutAudio.icPlay.gone()

        binding.layoutAudio1.progress.onSeekChange {
            viewModel.progress1.value = it
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.progress1.collect {
                    sound1.setVolume(it / 100f)
                    binding.layoutAudio1.progress.progress = it
                    binding.layoutAudio1.baseTextview2.text = "$it%"
                }
            }
        }

        /*audio 2*/
        binding.layoutAudio2.layoutAudio.txtType.text = audio[1].extension
        binding.layoutAudio2.layoutAudio.txtTitle.text = audio[1].title
        binding.layoutAudio2.layoutAudio.txtDuration.text = audio[1].duration.formatSecondsToTime()
        binding.layoutAudio2.layoutAudio.txtSize.text = audio[1].size.formatFileSize()
        binding.layoutAudio2.layoutAudio.icPlay.gone()

        binding.layoutAudio2.progress.onSeekChange {
            sound2.setVolume(it / 100f)
            viewModel.progress2.value = it
            Log.d("TAG", "initData: $it")
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.progress2.collect {
                    binding.layoutAudio2.progress.progress = it
                    binding.layoutAudio2.baseTextview2.text = "$it%"
                }
            }
        }

    }

    fun startSeekBarProgress() {
        val startTime = System.currentTimeMillis()
        var lastSecondMark = 0

        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                val elapsed = System.currentTimeMillis() - startTime
                binding.progressCircular.progress = min(elapsed.toInt(), totalDuration)

                val elapsedSeconds = elapsed
                if (elapsedSeconds > lastSecondMark) {
                    lastSecondMark = elapsedSeconds.toInt()
                    currentSeconds = elapsedSeconds.toInt()

                    binding.timeStart.text = currentSeconds.formatSecondsToTime()
                }

                if (elapsed >= totalDuration) {
                    viewModel.isPlayPreview.value = false
                    break
                }
                delay(16)
            }
        }
    }

    private fun stopSeekbarProgress() {
        job?.cancel()
    }

    override fun clickListener() {
        super.clickListener()
        binding.icBack.setOnSingleClickListener {
            finish()
        }

        binding.icPlayPreview.setOnClickListener {
            viewModel.isPlayPreview.value = viewModel.isPlayPreview.value != true
        }


        viewModel.isPlayPreview.observe(this@MixActivity) {
            binding.layoutAudio1.progress.isEnabled = !it
            binding.layoutAudio2.progress.isEnabled = !it
            if (it) {
                sound1.playSound(
                    audioModel = audioList[0],
                    volume = viewModel.progress1.value.toFloat() / 100
                )
                sound2.playSound(
                    audioModel = audioList[1],
                    volume = viewModel.progress2.value.toFloat() / 100
                )

                Log.d(
                    "MEDIA_STATUS",
                    "${sound1.mediaPlayer?.isPlaying} - ${sound2.mediaPlayer?.isPlaying}"
                )

                startSeekBarProgress()
            } else {
                stopSeekbarProgress()
                sound1.pauseSound()
                sound2.pauseSound()
            }
            binding.icPlayPreview.setImageResource(if (it) R.drawable.ic_pause_preview else R.drawable.ic_play_preview)
        }

        binding.icDownload.setOnSingleClickListener {
            DialogSaveFile.show(supportFragmentManager) {
                showLoading(true)
                viewModel.executeMix(
                    audio1 = audioList[0],
                    audio2 = audioList[1],
                    volume1 = viewModel.progress1.value,
                    volume2 = viewModel.progress2.value,
                    fileName = it,
                    onStart = {
                    },
                    onProgress = {
                        Log.d("ON_STATUS_MIX", "onProgress: $it")
                    },
                    onFail = {
                        showLoading(false)
                        Log.d("ON_STATUS_MIX", "onFail: $it")
                    },
                    onSuccess = {
                        showLoading(false)
                        MyFolderActivity.onStart(FolderType.MIX, this)
                    }
                )
            }
        }
    }
}

@HiltViewModel
class MixViewModel @Inject constructor(private val mixPresenter: MixPresenter) : BaseViewModel() {
    val progress1 = MutableStateFlow(100)
    val progress2 = MutableStateFlow(100)
    val isPlayPreview = MutableLiveData(false)

    fun executeMix(
        audio1: Audio,
        audio2: Audio,
        volume1: Int,
        volume2: Int,
        fileName: String,
        onStart: () -> Unit = {},
        onProgress: (Int) -> Unit = {},
        onFail: (String) -> Unit = {},
        onSuccess: () -> Unit = {},
    ) = viewModelScope.launch {
        mixPresenter.executeMix(
            audio1 = audio1,
            audio2 = audio2,
            volume1 = volume1,
            volume2 = volume2,
            fileName = fileName,
            onStart = onStart,
            onProgress = onProgress,
            onFail = onFail,
            onSuccess = onSuccess
        )
    }
}