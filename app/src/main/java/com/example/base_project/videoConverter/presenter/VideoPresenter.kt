package com.example.base_project.videoConverter.presenter

import android.util.Log
import com.example.base_project.command.BaseCommand
import com.example.base_project.common.util.AudioPath
import com.example.base_project.common.util.CommonConstants
import com.example.base_project.select.bean.Video
import com.example.base_project.select.util.SelectConstants
import com.example.base_project.videoConverter.command.VideoToAudioCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoPresenter @Inject constructor() {
    private var video: Video? = null

    fun initVideo(video: Video) {
        this.video = video
    }

    suspend fun executeFormatVideo(
        fileName: String,
        format: String,
        onFail: (String) -> Unit = {},
        onSuccess: () -> Unit = {},
        onProgress: (Int) -> Unit = { },
        onStart: () -> Unit = {},
    ) = withContext(Dispatchers.Default) {
        val saveDirBy = AudioPath.getSaveDirBy(SelectConstants.FROM_VIDEO_TO_AUDIO)
        val str2 = saveDirBy +
                File.separator +
                fileName +
                CommonConstants.UNDERLINE +
                AudioPath.getSaveDate() +
                CommonConstants.DOT +
                format

        val videoToAudioCommand = VideoToAudioCommand(video!!.path , str2)

        videoToAudioCommand.setListener(object : BaseCommand.Listener {
            override fun onFailure(str: String?) {
                Log.d("FAIL_VIDEO", str.toString())
                str?.let { onFail(it) }
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

        videoToAudioCommand.execute()
    }
}