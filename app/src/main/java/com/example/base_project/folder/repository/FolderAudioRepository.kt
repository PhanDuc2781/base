package com.example.base_project.folder.repository

import com.example.base_project.folder.util.GetFileInFolder
import com.example.base_project.select.bean.Audio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

interface FolderAudioRepository {
    fun getAudiosInFolder(fileDir: String): Flow<List<Audio>>
    fun deleteAudio(audio: Audio): Flow<Boolean>
}

class FolderAudioRepositoryImpl @Inject constructor() : FolderAudioRepository {
    override fun getAudiosInFolder(fileDir: String): Flow<List<Audio>> = flow {
        val audios = GetFileInFolder.getAllFileInFolder(fileDir).first()
        emit(audios)
    }

    override fun deleteAudio(audio: Audio): Flow<Boolean> {
        val fileToDelete = File(audio.path)
        return if (fileToDelete.exists()) {
            flow {
                emit(fileToDelete.delete())
            }
        } else {
            flow {
                emit(false)
            }
        }
    }
}