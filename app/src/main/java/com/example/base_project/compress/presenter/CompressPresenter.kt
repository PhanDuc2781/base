package com.example.base_project.compress.presenter

import com.example.base_project.command.BaseCommand
import com.example.base_project.common.util.AudioPath
import com.example.base_project.common.util.CommonConstants
import com.example.base_project.compress.command.CompressCommand
import com.example.base_project.select.bean.Audio
import com.example.base_project.select.util.SelectConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompressPresenter @Inject constructor() {
    suspend fun executeCompress(
        fileName: String,
        audio: Audio,
        channel: Int,
        bitrate: Int,
        sampleRate: Int,
        onFail: (String) -> Unit = {},
        onSuccess: () -> Unit = {},
        onProgress: (Int) -> Unit = { },
        onStart: () -> Unit = {},
    ) = withContext(Dispatchers.Default) {
        val saveDirBy = AudioPath.getSaveDirBy(SelectConstants.FROM_COMPRESS_AUDIO)
        val str2 =
            saveDirBy + File.separator + fileName + CommonConstants.UNDERLINE + AudioPath.getSaveDate() + CommonConstants.DOT + audio.extension.lowercase()
        val compressCommand = CompressCommand(channel, bitrate, sampleRate, audio.path, str2)
        compressCommand.setListener(object : BaseCommand.Listener {
            override fun onFailure(str: String?) {
                onFail(str.toString())
            }

            override fun onProgress(progress: Int) {
                onProgress.invoke(progress)
            }

            override fun onStart() {
                onStart()
            }

            override fun onSuccess() {
                onSuccess()
            }
        })

        compressCommand.execute()
    }
}