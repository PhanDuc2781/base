package com.example.base_project.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.base_project.ext.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

open class BaseViewModel : ViewModel() {
    val progress = SingleLiveEvent<Boolean>()

    fun showProgress(isShow: Boolean) {
        progress.postValue(isShow)
    }
}

fun BaseViewModel.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = viewModelScope.launch(context, start, block)