package com.example.base_project.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.ParcelFileDescriptor
import android.util.Log
import com.example.base_project.select.bean.Audio
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class SoundManager @Inject constructor(@ApplicationContext val context: Context) {
    var mediaPlayer: MediaPlayer? = null

    fun setVolume(volume: Float) {
        val vol = volume.coerceIn(0f, 1f)
        mediaPlayer?.setVolume(vol, vol)
    }

    fun playSound(audioModel: Audio, volume: Float = 1f , speed: Float = 1f , onCompletion: () -> Unit = {}) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
            } else {
                resetMediaPlayer()
            }

            val file = File(audioModel.path)
            if (!file.exists()) {
                Log.e("MediaPlayer", "File does not exist: ${audioModel.path}")
                return
            }

            val descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            descriptor?.use {
                mediaPlayer?.apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(it.fileDescriptor)
                    prepareAsync()
                    setOnPreparedListener { mp ->
                        mp.setVolume(volume.coerceIn(0f, 1f), volume.coerceIn(0f, 1f))
                        mp.start()
                    }
                    setOnCompletionListener {
                        onCompletion.invoke()
                    }
                    setOnPreparedListener {
                        it.playbackParams = it.playbackParams.setSpeed(speed)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing sound: ${audioModel.path}", e)
        }
    }

    fun pauseSound() {
        mediaPlayer?.pause()
    }

    fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun resetMediaPlayer() {
        try {
            mediaPlayer?.reset()
        } catch (e: IllegalStateException) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer()
        }
    }
}
