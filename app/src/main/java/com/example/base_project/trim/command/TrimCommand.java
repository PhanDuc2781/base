/**
 * Created by nekos on 12/08/2025.
 * Author: DucPd01
 */

package com.example.base_project.trim.command;

import com.example.base_project.command.BaseCommand;

public class TrimCommand extends BaseCommand {
    private String duration;
    private String inputPath;
    private String outputPath;
    private String startTime;

    public TrimCommand(float f, float f2, String str, String str2) {
        this.startTime = String.valueOf(f);
        this.duration = String.valueOf(f2);
        this.inputPath = str;
        this.outputPath = str2;
    }

    @Override
    public void execute() {
        execute(new String[]{"ffmpeg", "-ss", this.startTime, "-i", this.inputPath, "-to", this.duration, "-vn", "-map_metadata", "-1", "-c", "copy", this.outputPath});
    }
}