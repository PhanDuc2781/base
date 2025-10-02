package com.example.base_project.compress.command;

import com.example.base_project.command.BaseCommand;

public class CompressCommand extends BaseCommand {
    private int bitRate;
    private int channel;
    private String inputPath;
    private String outputPath;
    private int sampleRate;

    public CompressCommand(int i, int i2, int i3, String str, String str2) {
        this.channel = i;
        this.bitRate = i2;
        this.sampleRate = i3;
        this.inputPath = str;
        this.outputPath = str2;
    }

    @Override
    public void execute() {
        execute(new String[]{"ffmpeg", "-i", this.inputPath, "-ac", String.valueOf(this.channel), "-ab", String.valueOf(this.bitRate), "-ar", String.valueOf(this.sampleRate), "-vn", "-map_metadata", "-1", this.outputPath});
    }
}
