package com.example.base_project.trim.presenter

import com.example.base_project.command.BaseCommand
import com.example.base_project.common.util.AudioPath
import com.example.base_project.common.util.CommonConstants
import com.example.base_project.select.bean.Audio
import com.example.base_project.select.util.SelectConstants
import com.example.base_project.trim.command.TrimCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrimPresenter @Inject constructor() {
    suspend fun executeTrim(
        audio: Audio,
        fileName: String,
        startTime: Float,
        duration: Float,
        onStart: () -> Unit,
        onProgress: (Int) -> Unit,
        onSuccess: () -> Unit,
        onFail: () -> Unit,
    ) = withContext(Dispatchers.Default) {
        val saveDir = AudioPath.getSaveDirBy(SelectConstants.FROM_TRIM_AUDIO)
        val str2 =
            saveDir + File.separator + fileName + CommonConstants.UNDERLINE + AudioPath.getSaveDate() + CommonConstants.DOT + audio.extension.lowercase()
        val trimCommand = TrimCommand(startTime, duration, audio.path, str2)
        trimCommand.setListener(object : BaseCommand.Listener {
            override fun onFailure(str: String?) {
                onFail.invoke()
            }

            override fun onProgress(progress: Int) {
                onProgress.invoke(progress)
            }

            override fun onStart() {
                onStart.invoke()
            }

            override fun onSuccess() {
                onSuccess.invoke()
            }
        })
        trimCommand.execute()
    }
}