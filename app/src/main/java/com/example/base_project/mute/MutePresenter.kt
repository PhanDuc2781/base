package com.example.base_project.mute

import com.example.base_project.command.BaseCommand
import com.example.base_project.common.util.AudioPath
import com.example.base_project.common.util.CommonConstants
import com.example.base_project.select.bean.Audio
import com.example.base_project.select.util.SelectConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MutePresenter @Inject constructor() {

    suspend fun mute(
        audio: Audio,
        filePath: String,
        startTime: Float,
        endTime: Float,
        onStart: () -> Unit,
        onFail: (String) -> Unit,
        onSuccess: () -> Unit,
        onProgress: (Int) -> Unit,
    ) = withContext(Dispatchers.Default) {
        val f = startTime
        if (f >= 0.0f) {
            val f2 = endTime
            if (f2 >= 0.0f) {
                val saveDirBy = AudioPath.getSaveDirBy(SelectConstants.FROM_MUTE_PART)
                val str2 =
                    saveDirBy + File.separator + filePath + CommonConstants.UNDERLINE + AudioPath.getSaveDate() + CommonConstants.DOT + audio.extension.lowercase()

                val muteCommand = MuteCommand(f, f2, audio.path, str2)
                muteCommand.setListener(object : BaseCommand.Listener {
                    override fun onFailure(str: String?) {
                        str?.let { onFail.invoke(it) }
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
                muteCommand.execute()
            }
        }
    }
}