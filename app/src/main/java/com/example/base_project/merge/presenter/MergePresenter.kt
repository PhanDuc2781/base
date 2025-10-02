package com.example.base_project.merge.presenter

import android.util.Log
import com.example.base_project.command.BaseCommand
import com.example.base_project.common.util.AudioPath
import com.example.base_project.common.util.CommonConstants
import com.example.base_project.merge.command.MergeCommand
import com.example.base_project.select.bean.Audio
import com.example.base_project.select.util.SelectConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MergePresenter @Inject constructor() {
    private val audioList: ArrayList<Audio> = ArrayList()

    fun setMergeView(audioList: ArrayList<Audio>) {
        this.audioList.addAll(audioList)
    }

    suspend fun executeMerge(
        fileName: String,
        onFail: (String) -> Unit = {},
        onSuccess: () -> Unit = {},
        onProgress: (Int) -> Unit = { },
        onStart: () -> Unit = {},
    ) = withContext(Dispatchers.Default) {
        val saveDirBy = AudioPath.getSaveDirBy(SelectConstants.FROM_MERGE_AUDIO)
        val arrayList = ArrayList<String>()
        val it = audioList.iterator()
        while (it.hasNext()) {
            arrayList.add(it.next().path)
        }

        val str2 = saveDirBy +
                File.separator +
                fileName +
                CommonConstants.UNDERLINE +
                AudioPath.getSaveDate() +
                CommonConstants.DOT +
                SelectConstants.EXTENSION_AUDIO_MP3

        val mergeCommand = MergeCommand(arrayList, str2)

        mergeCommand.setListener(object : BaseCommand.Listener {
            override fun onFailure(str: String) {
                onFail(str)
            }

            override fun onProgress(p0: Int) {
                onProgress.invoke(p0)
            }

            override fun onStart() {
                onStart()
            }

            override fun onSuccess() {
                onSuccess()
            }
        })
        mergeCommand.execute()
    }
}