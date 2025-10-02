package com.example.base_project.select.util

object SelectUtils {
    fun getAllAudioMimeTypeList(): List<String> {
        return listOf(
            SelectConstants.MIME_TYPE_AUDIO_MP3,
            SelectConstants.MIME_TYPE_AUDIO_AAC,
            SelectConstants.MIME_TYPE_AUDIO_M4A,
            SelectConstants.MIME_TYPE_AUDIO_WAV,
            SelectConstants.MIME_TYPE_AUDIO_AMR,
            SelectConstants.MIME_TYPE_AUDIO_3GPP
        )
    }

    fun getAllAudioExtensionList(): List<String> {
        return listOf(
            SelectConstants.EXTENSION_AUDIO_MP3,
            SelectConstants.EXTENSION_AUDIO_AAC,
            SelectConstants.EXTENSION_AUDIO_M4A,
            SelectConstants.EXTENSION_AUDIO_WAV,
            SelectConstants.EXTENSION_AUDIO_AMR,
            SelectConstants.EXTENSION_VIDEO_3GP
        )
    }

    fun getMP3AACM4AWAVMimeTypeList(): List<String> {
        return listOf(
            SelectConstants.MIME_TYPE_AUDIO_MP3,
            SelectConstants.MIME_TYPE_AUDIO_AAC,
            SelectConstants.MIME_TYPE_AUDIO_M4A,
            SelectConstants.MIME_TYPE_AUDIO_WAV
        )
    }

    fun getMP3AACM4AWAVExtensionList(): List<String> {
        return listOf(
            SelectConstants.EXTENSION_AUDIO_MP3,
            SelectConstants.EXTENSION_AUDIO_AAC,
            SelectConstants.EXTENSION_AUDIO_M4A,
            SelectConstants.EXTENSION_AUDIO_WAV
        )
    }

    fun getMP3MimeTypeList(): List<String> {
        return listOf(
            SelectConstants.MIME_TYPE_AUDIO_MP3
        )
    }

    fun getMP3ExtensionList(): List<String> {
        return listOf(
            SelectConstants.EXTENSION_AUDIO_MP3
        )
    }
}