package com.example.base_project.trim;

import android.app.Activity;

import com.example.base_project.select.bean.Audio;


public interface ITrimView {
    void doFinish();

    Activity getActivity();

    float getEndTime();

    float getStartTime();

    void initAudio(Audio audio);

    boolean isFinishing();

    boolean isWaveformInitialized();

    void setTitle(String str);

    void stopPlay();
}
