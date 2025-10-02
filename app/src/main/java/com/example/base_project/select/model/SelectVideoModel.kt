package com.example.base_project.select.model

import android.util.Log
import android.webkit.MimeTypeMap
import com.example.base_project.applicattion.MainApplication
import com.example.base_project.select.Result
import com.example.base_project.select.bean.Video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

object SelectVideoModel {
    fun queryVideoList(): Flow<Result<List<Video>>> = flow {
        emit(Result.Loading)

        val arrayList = mutableListOf<Video>()
        val contentResolver = MainApplication.instance.contentResolver
        val videoUri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val videoCursor = contentResolver.query(videoUri, null, null, null, null)
        videoCursor?.let {
            while (it.moveToNext()) {
                val title =
                    it.getColumnIndex(android.provider.MediaStore.Video.Media.TITLE)
                val id =
                    it.getColumnIndex(android.provider.MediaStore.Video.Media._ID)
                val duration =
                    it.getColumnIndex(android.provider.MediaStore.Video.Media.DURATION)
                val size =
                    it.getColumnIndex(android.provider.MediaStore.Video.Media.SIZE)
                val path =
                    it.getColumnIndex(android.provider.MediaStore.Video.Media.DATA)

                val video = Video(
                    title = it.getString(title),
                    id = it.getString(id),
                    duration = it.getInt(duration),
                    extension = MimeTypeMap.getFileExtensionFromUrl(it.getString(path)),
                    path = it.getString(path),
                    size = it.getLong(size)
                )

                arrayList.add(video)
            }
        }
        videoCursor?.close()
        arrayList.reverse()

        Log.d("VIDEO_LIST", "$arrayList")
        emit(Result.Success(arrayList))
    }.catch {
        emit(Result.Error(it as Exception))
    }.flowOn(Dispatchers.IO)
}