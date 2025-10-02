package com.example.base_project.select

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.base_project.R
import com.example.base_project.audio_converter.AudioConverterActivity
import com.example.base_project.base.BaseVMActivity
import com.example.base_project.base.BaseViewModel
import com.example.base_project.base.setOnSingleClickListener
import com.example.base_project.compress.CompressActivity
import com.example.base_project.util.AppConstance.TYPE_ARG
import com.example.base_project.databinding.ActivitySelectAudioBinding
import com.example.base_project.enum.Type
import com.example.base_project.ext.parcelable
import com.example.base_project.ext.textChanges
import com.example.base_project.ext.visibleOrGone
import com.example.base_project.merge.MergeAudioActivity
import com.example.base_project.mix.MixActivity
import com.example.base_project.select.adapter.SelectAudioAdapter
import com.example.base_project.select.bean.Audio
import com.example.base_project.select.model.SelectAudioModel
import com.example.base_project.select.util.SelectUtils
import com.example.base_project.sound.SoundManager
import com.example.base_project.speed.SpeedActivity
import com.example.base_project.trim.ActivityEditorAudio
import com.example.base_project.util.AppConstance.AUDIO_ARG
import com.example.base_project.util.AppConstance.AUDIO_SELECTED_ARG
import com.example.base_project.volume.VolumeActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class SelectAudioActivity :
    BaseVMActivity<ActivitySelectAudioBinding, SelectDataViewModel>(ActivitySelectAudioBinding::inflate) {
    override val viewModel: SelectDataViewModel by viewModels()

    @Inject
    lateinit var soundManager: SoundManager

    private val selectAudioAdapter by lazy {
        SelectAudioAdapter(
            context = this@SelectAudioActivity,
            type = type,
            onClickItem = {
                handelClickItem(it, type)
                Log.d("onClickItem", it.toString())
            },
            onClickPlay = { audio, isPlay ->
                Log.d("SOUND_STATUS", "$audio - $isPlay")
                if (!isPlay) {
                    soundManager.pauseSound()
                } else {
                    soundManager.playSound(audioModel = audio)
                }
            },
            onClickAdd = { audio ->
                viewModel.selectedAudio(audio, type)
            },
            onClickMore = { audio, _ ->
                Log.d("onClickMore", audio.toString())
            }, onMaxSelectedReached = {
                Toast.makeText(this, "Only choose 2 item", Toast.LENGTH_SHORT).show()
            })
    }

    private fun handelClickItem(
        audio: Audio,
        type: Type,
    ) {
        when (type) {
            Type.TRIM -> {
                checkDuration(audio, onContinue = {
                    val intent = Intent(this, ActivityEditorAudio::class.java).apply {
                        putExtra(TYPE_ARG, Type.TRIM as Parcelable)
                        putExtra(AUDIO_ARG, audio)
                    }
                    startActivity(intent)
                })
            }

            Type.MERGE_AUDIO -> {}
            Type.VIDEO_CONVERTER -> {}
            Type.AUDIO_CONVERTER -> {
                val intent = Intent(this, AudioConverterActivity::class.java).apply {
                    putExtra(AUDIO_ARG, audio)
                }
                startActivity(intent)
            }

            Type.MIX -> {}
            Type.SPLIT -> {
                checkDuration(audio, onContinue = {
                    val intent = Intent(this, ActivityEditorAudio::class.java).apply {
                        putExtra(TYPE_ARG, Type.SPLIT as Parcelable)
                        putExtra(AUDIO_ARG, audio)
                    }
                    startActivity(intent)
                })
            }

            Type.REMOVE_PART -> {
                checkDuration(audio, onContinue = {
                    val intent = Intent(this, ActivityEditorAudio::class.java).apply {
                        putExtra(TYPE_ARG, Type.REMOVE_PART as Parcelable)
                        putExtra(AUDIO_ARG, audio)
                    }
                    startActivity(intent)
                })

            }

            Type.MUTE_PART -> {
                checkDuration(audio, onContinue = {
                    val intent = Intent(this, ActivityEditorAudio::class.java).apply {
                        putExtra(TYPE_ARG, Type.MUTE_PART as Parcelable)
                        putExtra(AUDIO_ARG, audio)
                    }
                    startActivity(intent)
                })
            }

            Type.VOLUME -> {
                val intent = Intent(this, VolumeActivity::class.java).apply {
                    putExtra(AUDIO_ARG, audio)
                }
                startActivity(intent)
            }

            Type.SPEED -> {
                val intent = Intent(this, SpeedActivity::class.java).apply {
                    putExtra(AUDIO_ARG, audio)
                }
                startActivity(intent)
            }

            Type.COMPRESS -> {
                val intent = Intent(this, CompressActivity::class.java).apply {
                    putExtra(AUDIO_ARG, audio)
                }
                startActivity(intent)
            }

            Type.REVERSE -> {
                val intent = Intent(
                    this,
                    com.example.base_project.reverse.ReverseActivity::class.java
                ).apply {
                    putExtra(AUDIO_ARG, audio)
                }
                startActivity(intent)
            }
        }
    }

    private fun checkDuration(
        audio: Audio,
        onContinue: () -> Unit = {},
    ) {
        val second = TimeUnit.MILLISECONDS.toSeconds(audio.duration.toLong()) % 60
        val minute = TimeUnit.MILLISECONDS.toMinutes(audio.duration.toLong())
        if (second < 10) {
            Toast.makeText(this, getString(R.string.duration_sort), Toast.LENGTH_SHORT).show()
        }
        if (minute > 15) {
            Toast.makeText(this, getString(R.string.duration_long), Toast.LENGTH_SHORT).show()
        } else {
            onContinue.invoke()
        }
    }

    private var type: Type = Type.TRIM
    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        type = intent.extras?.parcelable<Type>(TYPE_ARG) ?: Type.TRIM

        onBackPressedDispatcher.addCallback {
            finish()
        }

        initAdapter()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.audioList.collect { result ->
                    when (result) {
                        is Result.Success -> {
                            selectAudioAdapter.submitList(result.data)
                            Log.d("AUDIO_LIST", result.data.toString())
                            binding.linEmpty.visibleOrGone(result.data.isEmpty())
                            handleViewEmpty(result.data)
                            showProgress(false)
                        }

                        is Result.Error -> {
//                            Toast.makeText(
//                                this@SelectAudioActivity,
//                                result.exception.toString(),
//                                Toast.LENGTH_SHORT
//                            ).show()
                            showProgress(false)
                        }

                        is Result.Loading -> {
                            showProgress(true)
                        }
                    }
                }
            }
        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.audioSelected.collect {
                    Log.d("AUDIO_SELECTED_SIZE", "${it.size}")
                    binding.frameChoose.visibleOrGone(it.isNotEmpty() && type == Type.MERGE_AUDIO)
                    binding.frameChooseMix.visibleOrGone(it.size >= 2 && type == Type.MIX)
                    binding.txtChoose.text = getString(R.string.choose, it.size)
                }
            }
        }
    }

    private fun handleViewEmpty(data: List<Audio>) {
        binding.linEmpty.visibleOrGone(data.isEmpty())
        binding.icEmpty.setImageResource(if (data.isEmpty() && viewModel.isSearching.value) R.drawable.img_null_search else R.drawable.img_empty_audio)
        binding.txtContentEmpty.text =
            if (data.isEmpty() && viewModel.isSearching.value) getString(R.string.no_result_found) else getString(
                R.string.empty_audio_file
            )
    }

    private fun initAdapter() {
        binding.recAudio.apply {
            adapter = selectAudioAdapter
            layoutManager =
                LinearLayoutManager(this@SelectAudioActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    @OptIn(FlowPreview::class)
    override fun clickListener() {
        super.clickListener()
        binding.icBack.setOnSingleClickListener {
            finish()
        }

        lifecycleScope.launch {
            binding.edtSearch.textChanges()
                .debounce(200)
                .distinctUntilChanged()
                .collectLatest {
                    binding.icClose.visibleOrGone(it.isNotEmpty())
                    if (it.isEmpty()) {
                        viewModel.getMp3AudioList()
                    } else {
                        viewModel.searchAudio(it)
                    }
                }
        }

        binding.icClose.setOnSingleClickListener {
            binding.edtSearch.setText("")
        }

        binding.swipeLayout.setOnRefreshListener {
            viewModel.getMp3AudioList()
            binding.swipeLayout.isRefreshing = false
        }

        binding.txtChoose.setOnSingleClickListener {
            if (viewModel.audioSelected.value.size <= 1) {
                Toast.makeText(this, "Please select more than 1 audio", Toast.LENGTH_SHORT).show()
                return@setOnSingleClickListener
            }
            val intent = Intent(this, MergeAudioActivity::class.java).apply {
                putExtra(AUDIO_SELECTED_ARG, ArrayList(viewModel.audioSelected.value))
            }
            startActivity(intent)
        }

        binding.frameChooseMix.setOnSingleClickListener {
            if (viewModel.audioSelected.value.size > 2) {
                Toast.makeText(this, "Only choose 2 item", Toast.LENGTH_SHORT).show()
                return@setOnSingleClickListener
            } else {
                val intent = Intent(this, MixActivity::class.java).apply {
                    putExtra(AUDIO_SELECTED_ARG, ArrayList(viewModel.audioSelected.value))
                }
                startActivity(intent)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        soundManager.pauseSound()
        selectAudioAdapter.togglePlayPause(-1)
        selectAudioAdapter.isPlaying = false
    }
}

@HiltViewModel
class SelectDataViewModel @Inject constructor() : BaseViewModel() {
    private val _audioList = MutableStateFlow<Result<List<Audio>>>(Result.Loading)
    val audioList: StateFlow<Result<List<Audio>>> get() = _audioList

    private val _audioSelected = MutableStateFlow<List<Audio>>(emptyList())
    val audioSelected: StateFlow<List<Audio>> get() = _audioSelected

    private var fullAudioList: List<Audio> = emptyList()
    val isSearching = MutableStateFlow(false)

    private var currentJob: Job? = null

    fun getMp3AudioList() {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            SelectAudioModel.getAllAudio(
                q1 = SelectUtils.getAllAudioMimeTypeList(),
                q2 = SelectUtils.getAllAudioExtensionList()
            ).collect { result ->
                if (result is Result.Success) {
                    fullAudioList = result.data
                }
                _audioList.value = result
            }
        }
    }

    fun searchAudio(query: String) {
        isSearching.value = query.isNotEmpty()
        viewModelScope.launch {
            _audioList.value = Result.Loading
            val filtered = fullAudioList.filter {
                it.title.contains(query, ignoreCase = true)
            }

            _audioList.value = Result.Success(filtered)
        }
    }

    fun selectedAudio(audio: Audio, type: Type) {
        val currentList = _audioSelected.value.toMutableList()

        if (currentList.contains(audio)) {
            currentList.remove(audio)
        } else {
            if (type == Type.MIX) {
                if (currentList.size >= 2) {
                    return
                }
            }
            currentList.add(audio)
        }

        _audioSelected.value = currentList
    }


    init {
        getMp3AudioList()
    }
}

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}