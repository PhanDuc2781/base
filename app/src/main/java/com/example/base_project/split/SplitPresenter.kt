package com.example.base_project.split

import android.util.Log
import com.example.base_project.base.BaseActivity
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
class SplitPresenter @Inject constructor() {
    suspend fun split(
        audio: Audio,
        fileName1: String,
        fileName2: String,
        splitTime: Float,
        onStart: () -> Unit,
        onFail: (String) -> Unit,
        onSuccess: () -> Unit,
        onProgress: (Int) -> Unit,
    ) = withContext(Dispatchers.Default) {
        if (splitTime > 0.0f) {
            val duration2 = (audio.duration / 1000).toFloat()
            Log.d("DURATION_SPLIT" , "$duration2 - $splitTime")
            if (splitTime < duration2) {
                val saveDirBy = AudioPath.getSaveDirBy(SelectConstants.FROM_SPLIT_AUDIO)
                val savePath1 =
                    saveDirBy + File.separator + fileName1 + CommonConstants.UNDERLINE + 1 + CommonConstants.UNDERLINE + AudioPath.getSaveDate() + CommonConstants.DOT + audio.extension.lowercase()
                val savePath2 =
                    saveDirBy + File.separator + fileName2 + CommonConstants.UNDERLINE + 2 + CommonConstants.UNDERLINE + AudioPath.getSaveDate() + CommonConstants.DOT + audio.extension.lowercase()

                val splitCommand =
                    SplitCommand(splitTime, duration2, audio.path, savePath1, savePath2)
                splitCommand.setListener(object : BaseCommand.Listener {
                    override fun onFailure(str: String?) {
                        Log.d("SPLIT_STATUS", str ?: "ERROR")
                        str?.let { onFail.invoke(it) }
                    }

                    override fun onProgress(progress: Int) {
                        onProgress.invoke(progress)
                    }

                    override fun onStart() {
                        onStart.invoke()
                    }

                    override fun onSuccess() {
                        Log.d("SPLIT_STATUS", "SUCCESS")
                        onSuccess.invoke()
                    }

                })

                splitCommand.execute()
            }
        }
    }
}