package com.example.base_project.audio_converter.command;


import com.example.base_project.command.BaseCommand;

public class ConvertCommand extends BaseCommand {
    private String inputPath;
    private String outputPath;

    public ConvertCommand(String str, String str2) {
        this.inputPath = str;
        this.outputPath = str2;
    }

    @Override 
    public void execute() {
        execute(new String[]{"ffmpeg", "-i", this.inputPath, "-vn", "-map_metadata", "-1", this.outputPath});
    }
}
