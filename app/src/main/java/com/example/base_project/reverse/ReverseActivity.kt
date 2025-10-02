package com.example.base_project.reverse

import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.room.Insert
import com.example.base_project.R
import com.example.base_project.base.BaseVMActivity
import com.example.base_project.base.BaseViewModel
import com.example.base_project.base.setOnSingleClickListener
import com.example.base_project.databinding.ActivityReverseBinding
import com.example.base_project.dialog.DialogSaveFile
import com.example.base_project.ext.formatFileSize
import com.example.base_project.ext.formatSecondsToTime
import com.example.base_project.ext.parcelable
import com.example.base_project.folder.MyFolderActivity
import com.example.base_project.folder.enum.FolderType
import com.example.base_project.reverse.presenter.ReversePresenter
import com.example.base_project.select.bean.Audio
import com.example.base_project.sound.SoundManager
import com.example.base_project.util.AppConstance.AUDIO_ARG
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
class ReverseActivity :
    BaseVMActivity<ActivityReverseBinding, ReverseViewModel>(ActivityReverseBinding::inflate) {
    override val viewModel: ReverseViewModel by viewModels()
    private var audio: Audio? = null
    private var totalDuration = 0
    private var job: Job? = null
    private var currentSeconds = 0

    @Inject
    lateinit var soundManager: SoundManager

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        onBackPressedDispatcher.addCallback {
            finish()
        }

        audio = intent.extras?.parcelable<Audio>(AUDIO_ARG)
        binding.previewReverse.progressCircular.isEnabled = false
        audio?.let {
            initAudio(it)
            totalDuration = it.duration
            binding.previewReverse.progressCircular.max = totalDuration
            binding.previewReverse.timeEnd.text = totalDuration.formatSecondsToTime()
        }
    }

    private fun initAudio(audio: Audio) {
        binding.txtTitle.text = audio.title
        binding.txtDuration.text = audio.duration.formatSecondsToTime()
        binding.txtType.text = audio.extension
        binding.txtSize.text = audio.size.formatFileSize()
    }

    override fun clickListener() {
        super.clickListener()
        binding.icBack.setOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isPlaying.collect {
                    if (it) {
                        binding.previewReverse.icPlayPreviewCompress.setImageResource(R.drawable.ic_pause_preview)
                        startProgressSeekBar()
                        soundManager.playSound(audio ?: return@collect)
                    } else {
                        stopProgressSeekBar()
                        binding.previewReverse.icPlayPreviewCompress.setImageResource(R.drawable.ic_play_preview)
                        soundManager.pauseSound()
                    }
                }
            }
        }

        binding.previewReverse.icPlayPreviewCompress.setOnClickListener {
            viewModel.isPlaying.value = !viewModel.isPlaying.value
        }

        binding.icDownload.setOnSingleClickListener {
            DialogSaveFile.show(supportFragmentManager) {
                showLoading(true)
                viewModel.executeReverse(
                    fileName = it,
                    audio = audio ?: return@show,
                    onFail = {
                        showLoading(false)
                        Log.d("ON_STATUS_REVERSE", it)
                    },
                    onSuccess = {
                        showLoading(false)
                        MyFolderActivity.onStart(FolderType.REVERSE, this)
                    },
                    onStart = {},
                    onProgress = {
                        showLoading(false)
                        Log.d("ON_STATUS_REVERSE", "$it")
                    }
                )
            }
        }
    }

    private fun startProgressSeekBar() {
        val startTime = System.currentTimeMillis()
        var lastSecondMark = 0

        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                val elapsed = System.currentTimeMillis() - startTime
                binding.previewReverse.progressCircular.progress =
                    min(elapsed.toInt(), totalDuration)

                val elapsedSeconds = elapsed
                if (elapsedSeconds > lastSecondMark) {
                    lastSecondMark = elapsedSeconds.toInt()
                    currentSeconds = elapsedSeconds.toInt()

                    binding.previewReverse.timeStart.text = currentSeconds.formatSecondsToTime()
                }

                if (elapsed >= totalDuration) {
                    viewModel.isPlaying.value = false
                    break
                }
                delay(16)
            }
        }
    }

    private fun stopProgressSeekBar() {
        job?.cancel()
    }

    override fun onPause() {
        super.onPause()
        soundManager.pauseSound()
        stopProgressSeekBar()
        viewModel.isPlaying.value = false
    }
}

@HiltViewModel
class ReverseViewModel @Inject constructor(private val reversePresenter: ReversePresenter) :
    BaseViewModel() {
    val isPlaying = MutableStateFlow(false)

    fun executeReverse(
        fileName: String,
        audio: Audio,
        onStart: () -> Unit = {},
        onFail: (String) -> Unit = {},
        onSuccess: () -> Unit = {},
        onProgress: (Int) -> Unit = { },
    ) = viewModelScope.launch {
        reversePresenter.executeReverse(fileName, audio, onFail, onSuccess, onProgress, onStart)
    }
}