package com.example.base_project.speed

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
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
import com.example.base_project.databinding.ActivitySpeddBinding
import com.example.base_project.dialog.DialogSaveFile
import com.example.base_project.ext.formatFileSize
import com.example.base_project.ext.formatSecondsToTime
import com.example.base_project.ext.parcelable
import com.example.base_project.ext.toOneDecimal
import com.example.base_project.folder.MyFolderActivity
import com.example.base_project.folder.enum.FolderType
import com.example.base_project.select.bean.Audio
import com.example.base_project.sound.SoundManager
import com.example.base_project.speed.presenter.SpeedPresenter
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
class SpeedActivity :
    BaseVMActivity<ActivitySpeddBinding, SpeedViewModel>(ActivitySpeddBinding::inflate) {
    override val viewModel: SpeedViewModel by viewModels()
    private var audio: Audio? = null

    private var minValue = 0.5
    private var maxValue = 2
    private var speed = 1f
    private var totalDuration = 0
    private var job: Job? = null
    private var currentSeconds = 0

    @Inject
    lateinit var soundManager: SoundManager
    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        onBackPressedDispatcher.addCallback { finish() }
        audio = intent.extras?.parcelable<Audio>(AUDIO_ARG)

        binding.preview.progressCircular.isEnabled = false

        audio?.let {
            initAudio(it)
            viewModel.setCurrentValue(it.duration)
            binding.preview.timeEnd.text = it.duration.formatSecondsToTime()
            totalDuration = viewModel.currentDuration.value
            Log.d("totalDuration", totalDuration.toString())
            binding.preview.progressCircular.max = totalDuration
        }
    }

    private fun initAudio(audio: Audio) {
        binding.txtType.text = audio.extension
        binding.txtTitle.text = audio.title
        binding.txtSize.text = audio.size.formatFileSize()
        binding.txtDuration.text = audio.duration.formatSecondsToTime()
    }

    fun startSeekBarProgress() {
        val startTime = System.currentTimeMillis()
        var lastSecondMark = 0

        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                val elapsed = System.currentTimeMillis() - startTime
                binding.preview.progressCircular.progress = min(elapsed.toInt(), totalDuration)

                val elapsedSeconds = elapsed
                if (elapsedSeconds > lastSecondMark) {
                    lastSecondMark = elapsedSeconds.toInt()
                    currentSeconds = elapsedSeconds.toInt()

                    binding.preview.timeStart.text = currentSeconds.formatSecondsToTime()
                }

                if (elapsed >= totalDuration) {
                    viewModel.isPlaying.value = false
                    break
                }
                delay(16)
            }
        }
    }

    fun stopSeekBarProgress() {
        job?.cancel()
    }

    override fun clickListener() {
        super.clickListener()
        binding.icBack.setOnClickListener {
            finish()
        }

        binding.icDownload.setOnClickListener {
            if (speed == 1f) return@setOnClickListener

            DialogSaveFile.show(supportFragmentManager) {
                showLoading(true)
                viewModel.executeSpeed(
                    fileName = it,
                    audio = audio ?: return@show,
                    speed = speed,
                    onFail = {
                        Log.d("ON_STATUS_SPEED", it)
                        showLoading(false)
                    },
                    onSuccess = {
                        showLoading(false)
                        MyFolderActivity.onStart(FolderType.SPEED, this)
                    },
                    onProgress = {
                        Log.d("ON_STATUS_SPEED", "$it")
                    },
                    onStart = {

                    }
                )
            }
        }

        binding.preview.icPlayPreviewCompress.setOnClickListener {
            viewModel.isPlaying.value = !(viewModel.isPlaying.value ?: false)
        }

        viewModel.isPlaying.observe(this) {
            binding.progress.isEnabled = !it
            if (it) {
                binding.preview.icPlayPreviewCompress.setImageResource(R.drawable.ic_pause)
                soundManager.playSound(audio ?: return@observe, speed = speed)
                startSeekBarProgress()
            } else {
                binding.preview.icPlayPreviewCompress.setImageResource(R.drawable.ic_play_preview)
                soundManager.pauseSound()
                stopSeekBarProgress()
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentDuration.collect {
                    totalDuration = it
                    binding.preview.progressCircular.max = it
                    binding.preview.timeEnd.text = it.formatSecondsToTime()
                }
            }
        }


        binding.progress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("DefaultLocale", "SetTextI18n")
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean,
            ) {
                val value =
                    minValue + (progress.toDouble() / (seekBar?.max ?: 1)) * (maxValue - minValue)
                speed = value.toFloat()
                viewModel.setCurrentValue((audio?.duration!! / value).toInt())
                binding.txtSpeed.text = "${value.toOneDecimal()}X"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }

}

@HiltViewModel
class SpeedViewModel @Inject constructor(private val speedPresenter: SpeedPresenter) :
    BaseViewModel() {
    val isPlaying = MutableLiveData(false)
    val currentDuration = MutableStateFlow(0)

    fun setCurrentValue(value: Int) {
        currentDuration.value = value
    }

    fun executeSpeed(
        fileName: String,
        audio: Audio,
        speed: Float,
        onStart: () -> Unit = {},
        onFail: (String) -> Unit = {},
        onSuccess: () -> Unit = {},
        onProgress: (Int) -> Unit = {},
    ) = viewModelScope.launch {
        speedPresenter.executeSpeed(fileName, audio, speed, onStart, onFail, onSuccess, onProgress)
    }
}