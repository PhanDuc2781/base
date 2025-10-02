package com.example.base_project.volume.presenter

import com.example.base_project.command.BaseCommand
import com.example.base_project.common.util.AudioPath
import com.example.base_project.common.util.CommonConstants
import com.example.base_project.select.bean.Audio
import com.example.base_project.select.util.SelectConstants
import com.example.base_project.volume.command.VolumeCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VolumePresenter @Inject constructor() {
    suspend fun executeVolume(
        fileName: String,
        audio: Audio,
        db: Int,
        onStart: () -> Unit = {},
        onFail: (String) -> Unit = {},
        onSuccess: () -> Unit = {},
        onProgress: (Int) -> Unit = {},
    ) = withContext(Dispatchers.Default) {
        val saveDir = AudioPath.getSaveDirBy(SelectConstants.FROM_VOLUME_BOOSTER)
        val str2 =
            saveDir + File.separator + fileName + CommonConstants.UNDERLINE + AudioPath.getSaveDate() + CommonConstants.DOT + audio.extension.uppercase()
        val volumeCommand = VolumeCommand(db, audio.path, str2)

        volumeCommand.setListener(object : BaseCommand.Listener {
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

        volumeCommand.execute()

    }
}