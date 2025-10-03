package com.example.base_project.trim.presenter

import com.example.base_project.command.BaseCommand
import com.example.base_project.common.util.AudioPath
import com.example.base_project.common.util.CommonConstants
import com.example.base_project.select.bean.Audio
import com.example.base_project.select.util.SelectConstants
import com.example.base_project.trim.command.RemoveCommand
import com.example.base_project.trim.command.TrimCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemovePresenter @Inject constructor() {
    suspend fun remove(
        audio: Audio,
        fileName: String,
        endTime: Float,
        startTime: Float,
        onError: (String) -> Unit = {},
        onSuccess: () -> Unit = {},
        onStart: () -> Unit = {},
        onProgress: (Int) -> Unit = {},
    ) = withContext(Dispatchers.Default) {
        if (startTime > 0.0f || endTime < audio.duration) {
            val f = startTime
            if (f > 0.0f) {
                val f2 = endTime
                if (f2 > 0.0f) {
                    val saveFileDir = AudioPath.getSaveDirBy(SelectConstants.FROM_REMOVE_PART)
                    val file = File(saveFileDir, "Reserve")
                    if (!file.exists()) {
                        file.mkdirs()
                    }
                    if (!file.exists()) {
                        onError.invoke("Save Fail")
                        return@withContext
                    }

                    val absolutePath = file.absolutePath
                    val file2 = File(saveFileDir, "Audio")
                    if (!file2.exists()) {
                        file2.mkdirs()
                    }

                    if (!file2.exists()) {
                        onError("Save Fail")
                        return@withContext
                    }

                    val absolutePath2 = file2.absolutePath

                    val savePath =
                        absolutePath2 + File.separator + fileName + CommonConstants.UNDERLINE + AudioPath.getSaveDate() + CommonConstants.DOT + audio.extension.lowercase()
                    if (endTime < audio.duration) {
                        val removeCommand = RemoveCommand(
                            f,
                            f2,
                            audio.duration.toFloat(),
                            audio.path,
                            absolutePath + File.separator + UUID.randomUUID() + CommonConstants.DOT + audio.extension.lowercase(),
                            absolutePath + File.separator + UUID.randomUUID() + CommonConstants.DOT + audio.extension.lowercase(),
                            savePath
                        )
                        removeCommand.setListener(object : BaseCommand.Listener {
                            override fun onFailure(str: String?) {
                                str?.let { onError.invoke(it) }
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
                        removeCommand.execute()
                    } else {
                        val duration = f
                        val command = TrimCommand(
                            0.0f,
                            duration,
                            audio.path,
                            savePath
                        )
                        command.setListener(object : BaseCommand.Listener {
                            override fun onFailure(str: String?) {
                                str?.let { onError.invoke(it) }
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

                        command.execute()
                    }
                }
            }
        }
    }
}