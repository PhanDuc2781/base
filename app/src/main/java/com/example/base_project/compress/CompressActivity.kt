package com.example.base_project.compress

import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.example.base_project.R
import com.example.base_project.base.AdapterEquatable
import com.example.base_project.base.BaseVMActivity
import com.example.base_project.base.BaseViewModel
import com.example.base_project.base.setOnSingleClickListener
import com.example.base_project.compress.adapter.BitrateAdapter
import com.example.base_project.compress.adapter.ChannelAdapter
import com.example.base_project.compress.adapter.SampleRateAdapter
import com.example.base_project.compress.presenter.CompressPresenter
import com.example.base_project.databinding.ActivityCompressBinding
import com.example.base_project.dialog.DialogSaveFile
import com.example.base_project.ext.formatFileSize
import com.example.base_project.ext.formatSecondsToTime
import com.example.base_project.ext.parcelable
import com.example.base_project.folder.MyFolderActivity
import com.example.base_project.folder.enum.FolderType
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
class CompressActivity :
    BaseVMActivity<ActivityCompressBinding, CompressViewModel>(ActivityCompressBinding::inflate) {
    override val viewModel: CompressViewModel by viewModels()
    private var audio: Audio? = null
    private var hasChange = false
    private var bitrate = Bitrate._32_KBPS
    private var channel = Channel.MONO
    private var sampleRate = SampleRate._44100_HZ

    private var totalDuration = 0
    private var job: Job? = null
    private var currentSeconds = 0

    private lateinit var channelAdapter: ChannelAdapter
    private lateinit var bitrateAdapter: BitrateAdapter
    private lateinit var sampleRateAdapter: SampleRateAdapter

    @Inject
    lateinit var soundManager: SoundManager

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        onBackPressedDispatcher.addCallback { finish() }
        audio = intent.extras?.parcelable<Audio>(AUDIO_ARG)
        Log.d("AUDIO_ARG", audio.toString())

        initAdapter()
        audio?.let {
            initView(it)
            totalDuration = it.duration
            binding.progressCircular.max = totalDuration
            binding.timeEnd.text = totalDuration.formatSecondsToTime()
        }
    }

    private fun initView(audio: Audio) {
        binding.txtType.text = audio.extension.uppercase()
        binding.txtTitle.text = audio.title
        binding.txtPath.text = audio.path
        binding.txtDuration.text = audio.duration.formatSecondsToTime()
        binding.txtSize.text = audio.size.formatFileSize()
    }

    private fun initAdapter() {
        /*Chanel Adapter*/
        channelAdapter = ChannelAdapter {
            channelAdapter.setCurrent(it)
            channel = it
            hasChange = true
        }
        binding.recChannel.adapter = channelAdapter
        channelAdapter.setCurrent(Channel.MONO)

        /*Bitrate Adapter*/
        bitrateAdapter = BitrateAdapter {
            bitrateAdapter.setCurrent(it)
            bitrate = it
            hasChange = true
        }
        binding.recBitrate.adapter = bitrateAdapter
        bitrateAdapter.setCurrent(Bitrate._32_KBPS)

        /*Sample Rate*/
        sampleRateAdapter = SampleRateAdapter {
            sampleRateAdapter.setCurrent(it)
            sampleRate = it
            hasChange = true
        }
        binding.recSampleRate.adapter = sampleRateAdapter
        sampleRateAdapter.setCurrent(SampleRate._8000_HZ)
    }

    override fun clickListener() {
        super.clickListener()
        binding.icBack.setOnSingleClickListener {
            finish()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isPlaying.collect {
                    if (it) {
                        setSeekBarPreview()
                        soundManager.playSound(audio ?: return@collect)
                        binding.icPlayPreviewCompress.setImageResource(R.drawable.ic_pause_preview)
                    } else {
                        binding.icPlayPreviewCompress.setImageResource(R.drawable.ic_play_preview)
                        stopSeekBarPreview()
                        soundManager.pauseSound()
                    }
                }
            }
        }

        binding.icPlayPreviewCompress.setOnClickListener {
            viewModel.isPlaying.value = !viewModel.isPlaying.value
        }

        binding.icDownload.setOnSingleClickListener {
            DialogSaveFile.show(supportFragmentManager) {
                showLoading(true)
                viewModel.executeCompress(
                    it,
                    audio!!,
                    channel.channel,
                    bitrate.bitrate,
                    sampleRate.sampleRate,
                    onFail = {
                        showLoading(false)
                        Log.d("ON_STATUS_COMPRESS", it)
                    },
                    onSuccess = {
                        showLoading(false)
                        MyFolderActivity.onStart(FolderType.COMPRESS, this)
                    },
                    onStart = {},
                    onProgress = {
                        Log.d("ON_STATUS_COMPRESS", "$it")
                    })
            }
        }
    }

    private fun setSeekBarPreview() {
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
                    viewModel.isPlaying.value = false
                    break
                }
                delay(16)
            }
        }
    }

    private fun stopSeekBarPreview() {
        job?.cancel()
        job = null
    }

    override fun onPause() {
        super.onPause()
        viewModel.isPlaying.value = false
    }
}

@HiltViewModel
class CompressViewModel @Inject constructor(private val compressPresenter: CompressPresenter) :
    BaseViewModel() {

    val isPlaying = MutableStateFlow(false)

    fun executeCompress(
        fileName: String,
        audio: Audio,
        channel: Int,
        bitrate: Int,
        sampleRate: Int,
        onFail: (String) -> Unit = {},
        onSuccess: () -> Unit = {},
        onProgress: (Int) -> Unit = { },
        onStart: () -> Unit = {},
    ) = viewModelScope.launch {
        compressPresenter.executeCompress(
            fileName,
            audio,
            channel,
            bitrate,
            sampleRate,
            onFail,
            onSuccess,
            onProgress,
            onStart
        )
    }
}

enum class Channel(val typeName: String, val channel: Int) : AdapterEquatable {
    MONO("Mono", 1),
    STEREO("Stereo", 2)
}

enum class Bitrate(val typeName: String, val bitrate: Int) : AdapterEquatable {
    _32_KBPS("32 kbps", 32000),
    _96_KBPS("96 kbps", 96000),
    _128_KBPS("128 kbps", 128000),
    _192_KBPS("192 kbps", 192000),
    _256_KBPS("256 kbps", 256000),
    _320_KBPS("320 kbps", 320000)
}

enum class SampleRate(val typeName: String, val sampleRate: Int) : AdapterEquatable {
    _8000_HZ("8000 Hz", 8000),
    _11025_HZ("11025 Hz", 11025),
    _22050_HZ("22050 Hz", 22050),
    _32000_HZ("32000 Hz", 32000),
    _44100_HZ("44100 Hz", 44100),
    _48000_HZ("48000 Hz", 48000)
}