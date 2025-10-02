package com.example.base_project.select.model

import android.content.ContentUris
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import com.example.base_project.applicattion.MainApplication
import com.example.base_project.select.Result
import com.example.base_project.select.bean.Audio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

object SelectAudioModel {

    fun getAllAudio(q1: List<String>, q2: List<String>): Flow<Result<List<Audio>>> = flow {
        emit(Result.Loading)
        val listAudio = mutableListOf<Audio>()
        val musicResolver = MainApplication.instance.applicationContext.contentResolver
        val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val musicCursor = musicResolver.query(musicUri, null, null, null, null)

        if (musicCursor != null && musicCursor.moveToFirst()) {
            val titleIdx = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val idIdx = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val artistIdx = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val durationIdx = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val pathIdx = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val sizeIdx = musicCursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
            val albumAir = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ARTIST)

            do {
                try {
                    val path = musicCursor.getString(pathIdx)
                    val mimeType = getMimeType(path)
                    val extension = MimeTypeMap.getFileExtensionFromUrl(path)
                    val size = musicCursor.getLong(sizeIdx)

                    Log.d("DEBUG", "Path=$path, MimeType=$mimeType, Size=$size")

                    if ((mimeType in q1 || extension in q2) && size > 0) {
                        val audio = Audio(
                            artist = musicCursor.getString(artistIdx),
                            duration = musicCursor.getInt(durationIdx),
                            extension = extension,
                            id = musicCursor.getString(idIdx),
                            mimeType = mimeType,
                            path = path,
                            size = size,
                            title = musicCursor.getString(titleIdx),
                            albumArtist = musicCursor.getString(albumAir)
                        )
                        listAudio.add(audio)
                    }

                } catch (e: Exception) {
                    Log.e("ERROR", "Failed to read audio: ${e.message}")
                    emit(Result.Error(e))
                }
            } while (musicCursor.moveToNext())
        }

        musicCursor?.close()
        Log.d("LIST_AUDIO", "$listAudio")
        listAudio.reverse()
        emit(Result.Success(listAudio))

    }.catch {
        emit(Result.Error(it as Exception))
    }.flowOn(Dispatchers.IO)

    fun getMimeType(url: String): String {
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        return extension?.let {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(it)
        } ?: ""
    }
}