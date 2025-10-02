package com.example.base_project.trim

import androidx.lifecycle.viewModelScope
import com.example.base_project.base.BaseViewModel
import com.example.base_project.select.bean.Audio
import com.example.base_project.split.SplitPresenter
import com.example.base_project.trim.presenter.TrimPresenter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrimViewmodel @Inject constructor(
    private val trimPresenter: TrimPresenter,
    private val splitPresenter: SplitPresenter,
) : BaseViewModel() {

    val selectedStart = MutableStateFlow(-1)
    val selectedEnd = MutableStateFlow(-1)
    val selectedDuration = MutableStateFlow(-1)

    val isPlay1 = MutableStateFlow(false)
    val isPlay2 = MutableStateFlow(false)

    fun setStart(start: Int) {
        selectedStart.value = start
    }

    fun setEnd(end: Int) {
        selectedEnd.value = end
    }

    fun split(
        audio: Audio,
        fileName1: String,
        fileName2: String,
        splitTime: Float,
        onStart: () -> Unit,
        onFail: (String) -> Unit,
        onSuccess: () -> Unit,
        onProgress: (Int) -> Unit,
    ) = viewModelScope.launch {
        splitPresenter.split(
            audio,
            fileName1,
            fileName2,
            splitTime,
            onStart,
            onFail,
            onSuccess,
            onProgress
        )
    }

    fun trim(
        audio: Audio,
        fileName: String,
        startTime: Float,
        duration: Float,
        onStart: () -> Unit = {},
        onProgress: (Int) -> Unit = {},
        onSuccess: () -> Unit = {},
        onFail: () -> Unit = {},
    ) = viewModelScope.launch {
        trimPresenter.executeTrim(
            audio,
            fileName,
            startTime,
            duration,
            onStart,
            onProgress,
            onSuccess,
            onFail
        )
    }
}