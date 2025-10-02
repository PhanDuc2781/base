/**
 * Created by nekos on 11/08/2025.
 * Author: DucPd01
 */

package com.example.base_project.speed.command;

import com.example.base_project.command.BaseCommand;

public class SpeedCommand extends BaseCommand {
    private String inputPath;
    private String outputPath;
    private float speed;

    public SpeedCommand(float f, String str, String str2) {
        this.speed = f;
        this.inputPath = str;
        this.outputPath = str2;
    }

    @Override
    public void execute() {
        execute(new String[]{"ffmpeg", "-i", this.inputPath, "-filter:a", "atempo=" + this.speed, "-vn", "-map_metadata", "-1", this.outputPath});
    }
}
