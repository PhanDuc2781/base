package com.example.base_project.speed.presenter

import com.example.base_project.command.BaseCommand
import com.example.base_project.common.util.AudioPath
import com.example.base_project.common.util.CommonConstants
import com.example.base_project.select.bean.Audio
import com.example.base_project.select.util.SelectConstants
import com.example.base_project.speed.command.SpeedCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeedPresenter @Inject constructor() {
    suspend fun executeSpeed(
        fileName: String,
        audio: Audio,
        speed: Float,
        onStart: () -> Unit = {},
        onFail: (String) -> Unit = {},
        onSuccess: () -> Unit = {},
        onProgress: (Int) -> Unit = {},
    ) = withContext(Dispatchers.Default) {
        val saveDir = AudioPath.getSaveDirBy(SelectConstants.FROM_SPEED_EDITOR)
        val str2 =
            saveDir + File.separator + fileName + CommonConstants.UNDERLINE + AudioPath.getSaveDate() + CommonConstants.DOT + audio.extension.lowercase()
        val speedCommand = SpeedCommand(speed, audio.path, str2)

        speedCommand.setListener(object : BaseCommand.Listener {
            override fun onFailure(str: String?) {
                onFail.invoke(str ?: "")
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

        speedCommand.execute()
    }
}