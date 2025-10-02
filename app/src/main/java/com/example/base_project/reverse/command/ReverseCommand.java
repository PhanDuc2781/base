package com.example.base_project.reverse.command;

import com.example.base_project.command.BaseCommand;

public class ReverseCommand extends BaseCommand {
    private String inputPath;
    private String outputPath;

    public ReverseCommand(String str, String str2) {
        this.inputPath = str;
        this.outputPath = str2;
    }

    @Override
    public void execute() {
        execute(new String[]{"ffmpeg", "-i", this.inputPath, "-af", "areverse", "-vn", "-map_metadata", "-1", this.outputPath});
    }
}
