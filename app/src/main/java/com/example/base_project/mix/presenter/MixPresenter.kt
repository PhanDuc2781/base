package com.example.base_project.mix.presenter

import android.util.Log
import com.example.base_project.command.BaseCommand
import com.example.base_project.common.util.AudioPath
import com.example.base_project.common.util.CommonConstants
import com.example.base_project.mix.command.MixCommand
import com.example.base_project.select.bean.Audio
import com.example.base_project.select.util.SelectConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MixPresenter @Inject constructor() {

    companion object {
        const val LONGEST: String = "longest"
        const val SHORTEST: String = "shortest"
    }

    suspend fun executeMix(
        audio1: Audio,
        audio2: Audio,
        volume1: Int,
        volume2: Int,
        fileName: String,
        onStart: () -> Unit = {},
        onProgress: (Int) -> Unit = {},
        onFail: (String) -> Unit = {},
        onSuccess: () -> Unit = {},
    ) = withContext(Dispatchers.Default) {
        val saveDirBy = AudioPath.getSaveDirBy(SelectConstants.FROM_MIX_AUDIO)
        val str2 = SHORTEST
        val str3 = saveDirBy +
                File.separator +
                fileName +
                CommonConstants.UNDERLINE +
                AudioPath.getSaveDate() +
                CommonConstants.DOT +
                SelectConstants.EXTENSION_AUDIO_MP3

        val volume1 = (volume1 * 0.01f * 2f).toFloat()
        val volume2 = (volume2 * 0.01f * 2f).toFloat()

        Log.d("STR_MIX", "$str2-$str3-$volume1-$volume2")

        val mixCommand: MixCommand =
            if (SelectConstants.EXTENSION_AUDIO_MP3 != audio1.extension || SelectConstants.EXTENSION_AUDIO_MP3 != audio2.extension) {
                MixCommand(audio1.path, audio2.path, volume1, volume2, str2, str3)
            } else {
                MixCommand(audio2.path, audio1.path, volume1, volume2, str2, str3)
            }

        mixCommand.setListener(object : BaseCommand.Listener {
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

        mixCommand.execute()
    }
}