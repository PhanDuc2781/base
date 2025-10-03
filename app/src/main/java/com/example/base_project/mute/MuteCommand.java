/**
 * Created by nekos on 03/10/2025.
 * Author: DucPd01
 */

package com.example.base_project.mute;

import com.example.base_project.command.BaseCommand;

public class MuteCommand extends BaseCommand{
    private float endTime;
    private String inputPath;
    private String outputPath;
    private float startTime;

    public MuteCommand(float f, float f2, String str, String str2) {
        this.startTime = f;
        this.endTime = f2;
        this.inputPath = str;
        this.outputPath = str2;
    }

    @Override
    public void execute() {
        execute(new String[]{"ffmpeg", "-i", this.inputPath, "-af", "volume=enable='between(t, " + this.startTime + ", " + this.endTime + ")':volume=0", "-vn", "-map_metadata", "-1", this.outputPath});
    }
}
