package com.example.base_project.wave_form.async

import com.example.base_project.select.bean.Audio


interface SongLoaderListener {
    fun onLoadSongSuccess(listSong: ArrayList<Audio>)
}