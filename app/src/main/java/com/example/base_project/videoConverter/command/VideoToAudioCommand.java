/**
 * Created by nekos on 06/08/2025.
 * Author: DucPd01
 */

package com.example.base_project.videoConverter.command;

import com.example.base_project.command.BaseCommand;

public class VideoToAudioCommand extends BaseCommand {
    private String inputPath;
    private String outputPath;

    public VideoToAudioCommand(String str, String str2) {
        this.inputPath = str;
        this.outputPath = str2;
    }

    @Override
    public void execute() {
        execute(new String[]{"ffmpeg", "-i", this.inputPath, this.outputPath});
    }
}
