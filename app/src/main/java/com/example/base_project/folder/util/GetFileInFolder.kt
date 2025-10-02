package com.example.base_project.folder.util

import android.media.MediaMetadataRetriever
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.example.base_project.R
import com.example.base_project.applicattion.MainApplication
import com.example.base_project.select.bean.Audio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

object GetFileInFolder {
    fun getAllFileInFolder(dirName: String): Flow<List<Audio>> = flow {
        val rootDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            MainApplication.instance.applicationContext.getString(R.string.app_name_folder)
        )

        val subDir = File(rootDir, dirName)

        if (!subDir.exists() || !subDir.isDirectory) {
            emit(emptyList<Audio>())
            return@flow
        }

        val listAudio = subDir.listFiles()?.filter {
            it.isFile
        }?.map {
            fileToAudio(it)
        } ?: emptyList()

        emit(listAudio)
    }.catch {
        emit(emptyList<Audio>())
    }.flowOn(Dispatchers.IO)

    private fun fileToAudio(file: File): Audio {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.absolutePath)

        val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            ?: file.nameWithoutExtension
        val artist =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown"
        val albumArtist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)
        val durationStr =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) ?: "0"
        val duration = durationStr.toIntOrNull() ?: 0

        val extension = file.extension
        val mimeType = MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(extension.lowercase()) ?: "audio/*"

        retriever.release()

        return Audio(
            artist = artist,
            duration = duration,
            extension = extension,
            id = file.name,
            mimeType = mimeType,
            path = file.absolutePath,
            size = file.length(),
            title = title,
            albumArtist = albumArtist
        )
    }
}