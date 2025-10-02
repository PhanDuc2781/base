package com.example.base_project.volume.command;


import com.example.base_project.command.BaseCommand;

public class VolumeCommand extends BaseCommand {
    private int db;
    private String inputPath;
    private String outputPath;

    public VolumeCommand(int i, String str, String str2) {
        this.db = i;
        this.inputPath = str;
        this.outputPath = str2;
    }

    @Override
    public void execute() {
        execute(new String[]{"ffmpeg", "-i", this.inputPath, "-filter:a", "volume=" + this.db + "dB", "-vn", "-map_metadata", "-1", this.outputPath});
    }
}
