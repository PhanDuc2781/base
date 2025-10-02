/**
 * Created by nekos on 06/08/2025.
 * Author: DucPd01
 */

package com.example.base_project.common.util;

import android.os.Environment;

import com.example.base_project.R;
import com.example.base_project.applicattion.MainApplication;
import com.example.base_project.select.util.SelectConstants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AudioPath {
    public static String getAudioDir(int i) {
        switch (i) {
            case SelectConstants.FROM_All_AUDIO:
                return CommonConstants.ALL_DIR;
            case SelectConstants.FROM_TRIM_AUDIO:
                return CommonConstants.TRIM_DIR;
            case SelectConstants.FROM_MERGE_AUDIO:
                return CommonConstants.MERGE_DIR;
            case SelectConstants.FROM_AUDIO_CONVERTER:
                return CommonConstants.CONVERT_DIR;
            case SelectConstants.FROM_VIDEO_TO_AUDIO:
                return CommonConstants.VIDEO_DIR;
            case SelectConstants.FROM_MIX_AUDIO:
                return CommonConstants.MIX_DIR;
            case SelectConstants.FROM_COMPRESS_AUDIO:
                return CommonConstants.COMPRESS_DIR;
            case SelectConstants.FROM_TAG_EDITOR:
                return CommonConstants.TAG_DIR;
            case SelectConstants.FROM_SPLIT_AUDIO:
                return CommonConstants.SPLIT_DIR;
            case SelectConstants.FROM_REVERSE_AUDIO:
                return CommonConstants.REVERSE_DIR;
            case SelectConstants.FROM_SPEED_EDITOR:
                return CommonConstants.SPEED_DIR;
            case SelectConstants.FROM_REMOVE_PART:
                return CommonConstants.REMOVE_DIR;
            case SelectConstants.FROM_MUTE_PART:
                return CommonConstants.MUTE_DIR;
            case SelectConstants.FROM_VOLUME_BOOSTER:
                return CommonConstants.VOLUME_DIR;
            default:
                return CommonConstants.DEFAULT_DIR;
        }
    }

    public static String getSaveDirBy(int i) {
        File file;

        file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if ((file == null || !file.exists()) && (((file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)) == null || !file.exists()) && ((file = MainApplication.Companion.getInstance().getApplicationContext().getFilesDir()) == null || !file.exists()))) {
            return "";
        }

        File file2 = new File(file, MainApplication.Companion.getInstance().getApplicationContext().getString(R.string.app_name_folder));
        if (!file2.exists()) {
            file2.mkdirs();
        }
        if (!file2.exists()) {
            return "";
        }
        File file3 = new File(file2, getAudioDir(i));
        if (!file3.exists()) {
            file3.mkdirs();
        }
        if (!file3.exists()) {
            return "";
        }
        return file3.getAbsolutePath();
    }

    public static String getSaveDate() {
        return new SimpleDateFormat(CommonConstants.SAVE_DATE_PATTERN).format(new Date());
    }
}