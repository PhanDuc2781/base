package com.example.base_project.volume

import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.base_project.R
import com.example.base_project.base.BaseVMActivity
import com.example.base_project.base.BaseViewModel
import com.example.base_project.base.setOnSingleClickListener
import com.example.base_project.databinding.ActivityVolumeBinding
import com.example.base_project.dialog.DialogSaveFile
import com.example.base_project.ext.formatSecondsToTime
import com.example.base_project.ext.parcelable
import com.example.base_project.folder.MyFolderActivity
import com.example.base_project.folder.enum.FolderType
import com.example.base_project.select.bean.Audio
import com.example.base_project.sound.SoundManager
import com.example.base_project.util.AppConstance.AUDIO_ARG
import com.example.base_project.volume.presenter.VolumePresenter
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min

@AndroidEntryPoint
class VolumeActivity :
    BaseVMActivity<ActivityVolumeBinding, VolumeViewModel>(ActivityVolumeBinding::inflate) {
    override val viewModel: VolumeViewModel by viewModels()

    @Inject
    lateinit var soundManager: SoundManager
    private var audio: Audio? = null
    private var totalDuration = 0
    private var minValue = -20
    private var maxValue = 20
    private var volume = 0
    private var job: Job? = null
    private var currentSeconds = 0
    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)

        audio = intent.extras?.parcelable<Audio>(AUDIO_ARG)

        audio?.let {
            binding.txtType.text = it.extension
            binding.txtTitle.text = it.title
            binding.txtSize.text = it.size.toString()
            binding.txtDuration.text = it.duration.toString()


            totalDuration = it.duration
            binding.preview.progressCircular.max = totalDuration
            binding.preview.timeEnd.text = totalDuration.formatSecondsToTime()
        }
    }

    override fun clickListener() {
        super.clickListener()
        binding.progress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean,
            ) {
                val value =
                    minValue + (progress.toDouble() / (seekBar?.max ?: 1)) * (maxValue - minValue)
                volume = value.toInt()

                val textValue = if (value.toInt() > 0) {
                    "+${value.toInt()} dB"
                } else {
                    "${value.toInt()} dB"
                }
                binding.txtSpeed.text = textValue
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        binding.preview.icPlayPreviewCompress.setOnClickListener {
            viewModel.isPlaying.value = viewModel.isPlaying.value != true
        }

        binding.icBack.setOnClickListener {
            finish()
        }

        binding.icDownload.setOnSingleClickListener {
            DialogSaveFile.show(supportFragmentManager) {
                showLoading(true)
                viewModel.executeVolume(
                    fileName = it,
                    audio = audio ?: return@show,
                    db = volume,
                    onFail = {
                        showLoading(false)
                        Log.d("ON_STATUS_VOLUME", it)
                    },
                    onSuccess = {
                        showLoading(false)
                        MyFolderActivity.onStart(FolderType.VOLUME, this)
                    },
                    onProgress = {
                        Log.d("ON_STATUS_VOLUME", "$it")
                    },
                    onStart = {}
                )
            }
        }

        viewModel.isPlaying.observe(this) {
            binding.progress.isEnabled = !it
            if (it) {
                binding.preview.icPlayPreviewCompress.setImageResource(R.drawable.ic_pause)
                soundManager.playSound(audio ?: return@observe, volume = volume.toFloat())
                startSeekBarProgress()
            } else {
                binding.preview.icPlayPreviewCompress.setImageResource(R.drawable.ic_play_preview)
                soundManager.pauseSound()
                stopSeekBarProgress()
            }
        }
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
}

@HiltViewModel
class VolumeViewModel @Inject constructor(private val volumePresenter: VolumePresenter) :
    BaseViewModel() {
    val isPlaying = MutableLiveData(false)

    fun executeVolume(
        fileName: String,
        audio: Audio,
        db: Int,
        onStart: () -> Unit = {},
        onFail: (String) -> Unit = {},
        onSuccess: () -> Unit = {},
        onProgress: (Int) -> Unit = {},
    ) {
        viewModelScope.launch {
            volumePresenter.executeVolume(
                fileName,
                audio,
                db,
                onStart,
                onFail,
                onSuccess,
                onProgress
            )
        }
    }
}