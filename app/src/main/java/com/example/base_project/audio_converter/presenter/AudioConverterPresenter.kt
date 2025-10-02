package com.example.base_project.audio_converter.presenter

import com.example.base_project.audio_converter.command.ConvertCommand
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
class AudioConverterPresenter @Inject constructor() {

    suspend fun executeConvert(
        audio: Audio,
        fileName: String,
        type: String,
        onFail: (String) -> Unit = {},
        onSuccess: () -> Unit = {},
        onProgress: (Int) -> Unit = { },
        onStart: () -> Unit = {},
    ) = withContext(Dispatchers.Default) {
        val saveDirBy = AudioPath.getSaveDirBy(SelectConstants.FROM_AUDIO_CONVERTER)
        val str =
            saveDirBy + File.separator + fileName + CommonConstants.UNDERLINE + AudioPath.getSaveDate() + CommonConstants.DOT + type
        val convertCommand = ConvertCommand(audio.path, str)

        convertCommand.setListener(object : BaseCommand.Listener {
            override fun onFailure(str: String?) {
                onFail.invoke(str.toString())
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
        convertCommand.execute()
    }
}