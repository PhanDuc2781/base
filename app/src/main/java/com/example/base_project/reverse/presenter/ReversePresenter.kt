package com.example.base_project.reverse.presenter

import com.example.base_project.command.BaseCommand
import com.example.base_project.common.util.AudioPath
import com.example.base_project.common.util.CommonConstants
import com.example.base_project.reverse.command.ReverseCommand
import com.example.base_project.select.bean.Audio
import com.example.base_project.select.util.SelectConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReversePresenter @Inject constructor() {
    suspend fun executeReverse(
        fileName: String,
        audio: Audio,
        onFail: (String) -> Unit = {},
        onSuccess: () -> Unit = {},
        onProgress: (Int) -> Unit = { },
        onStart: () -> Unit = {},
    ) = withContext(Dispatchers.Default) {
        val saveDir = AudioPath.getSaveDirBy(SelectConstants.FROM_REVERSE_AUDIO)
        val str2 =
            saveDir + File.separator + fileName + CommonConstants.UNDERLINE + AudioPath.getSaveDate() + CommonConstants.DOT + audio.extension.lowercase()
        val revertCommand = ReverseCommand(audio.path, str2)

        revertCommand.setListener(object : BaseCommand.Listener {
            override fun onFailure(str: String?) {
                onFail(str.toString())
            }

            override fun onProgress(progress: Int) {
                onProgress(progress)
            }

            override fun onStart() {
                onStart()
            }

            override fun onSuccess() {
                onSuccess()
            }
        })
        revertCommand.execute()
    }
}