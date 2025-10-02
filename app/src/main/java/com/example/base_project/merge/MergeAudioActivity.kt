package com.example.base_project.merge

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.example.base_project.R
import com.example.base_project.base.AlertData
import com.example.base_project.base.BaseVMActivity
import com.example.base_project.base.BaseViewModel
import com.example.base_project.base.setOnSingleClickListener
import com.example.base_project.base.showCustomAlertDialog
import com.example.base_project.databinding.ActivityMergeAudioBinding
import com.example.base_project.dialog.DialogSaveFile
import com.example.base_project.enum.Type
import com.example.base_project.ext.getParcelableArrayList
import com.example.base_project.folder.MyFolderActivity
import com.example.base_project.folder.enum.FolderType
import com.example.base_project.merge.presenter.MergePresenter
import com.example.base_project.select.adapter.SelectAudioAdapter
import com.example.base_project.select.bean.Audio
import com.example.base_project.util.AppConstance.AUDIO_SELECTED_ARG
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MergeAudioActivity :
    BaseVMActivity<ActivityMergeAudioBinding, MergeAudioViewModel>(ActivityMergeAudioBinding::inflate) {
    override val viewModel: MergeAudioViewModel by viewModels()

    private var listAudio: List<Audio> = mutableListOf()

    val adapter by lazy {
        SelectAudioAdapter(
            context = this@MergeAudioActivity,
            type = Type.MERGE_AUDIO,
            onClickRemove = { audio ->
                showDialogRemove(audio)
            },
            needShowRemove = true
        )
    }

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        onBackPressedDispatcher.addCallback {
            finish()
        }

        listAudio = intent.getParcelableArrayList(AUDIO_SELECTED_ARG) ?: emptyList()
        viewModel.initListAudio(listAudio)
        Log.d("AUDIO_LIST_SELECTED", listAudio.toString())

        initAdapter()
    }

    private fun initAdapter() {
        binding.recAudio.adapter = adapter
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentList.collect {
                    adapter.submitList(it)
                }
            }
        }
    }

    private fun showDialogRemove(audio: Audio) {
        showCustomAlertDialog(
            AlertData(
                title = getString(R.string.mes_delete_audio),
                posTitle = getString(R.string.delete),
                nevTitle = getString(R.string.cancel),
                callback = { action ->
                    if (action) {
                        viewModel.removeItemAudio(audio) {
                            Toast.makeText(
                                this,
                                "Please select more than 1 audio",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    }
                }
            )
        )
    }

    override fun clickListener() {
        super.clickListener()
        binding.icBack.setOnSingleClickListener {
            finish()
        }

        binding.icDownload.setOnSingleClickListener {
            showSaveAlertDialog()
        }
    }

    private fun showSaveAlertDialog() {
        DialogSaveFile.show(supportFragmentManager) { fileName ->
            showLoading(true)
            viewModel.executeMerge(
                fileName = fileName,
                onProgress = { progress ->
//                    Toast.makeText(this, "$progress", Toast.LENGTH_SHORT).show()
                },
                onStart = {},
                onSuccess = {
                    showLoading(false)
                    MyFolderActivity.onStart(FolderType.MERGED, this@MergeAudioActivity)
                    finish()
                },
                onFail = {
                    showLoading(false)
                }
            )
        }
    }
}

@HiltViewModel
class MergeAudioViewModel @Inject constructor(private val mergePresenter: MergePresenter) :
    BaseViewModel() {
    private val _currentList = MutableStateFlow<List<Audio>>(emptyList())
    val currentList: StateFlow<List<Audio>> = _currentList

    init {
        viewModelScope.launch {
            _currentList.collect { newList ->
                mergePresenter.setMergeView(ArrayList(newList))
            }
        }
    }

    fun initListAudio(list: List<Audio>) {
        _currentList.value = list
    }

    fun removeItemAudio(audio: Audio, onFinish: () -> Unit = {}) {
        val list = _currentList.value.toMutableList()
        list.remove(audio)
        _currentList.value = list
        if (_currentList.value.size <= 1) {
            onFinish()
        }
    }

    fun executeMerge(
        fileName: String,
        onFail: (String) -> Unit = {},
        onSuccess: () -> Unit = {},
        onProgress: (Int) -> Unit = { },
        onStart: () -> Unit = {},
    ) = viewModelScope.launch {
        mergePresenter.executeMerge(fileName, onFail, onSuccess, onProgress, onStart)
    }

}