/**
 * Created by nekos on 08/08/2025.
 * Author: DucPd01
 */

package com.example.base_project.mix.command;

import com.example.base_project.command.BaseCommand;

public class MixCommand extends BaseCommand {
    private String inputPath1;
    private String inputPath2;
    private String length;
    private String outputPath;
    private float volume1;
    private float volume2;

    public MixCommand(String str, String str2, float f, float f2, String str3, String str4) {
        this.inputPath1 = str;
        this.inputPath2 = str2;
        this.volume1 = f;
        this.volume2 = f2;
        this.length = str3;
        this.outputPath = str4;
    }

    @Override
    public void execute() {
        execute(new String[]{"ffmpeg", "-i", this.inputPath1, "-i", this.inputPath2, "-filter_complex", "[0:0]volume=" + this.volume1 + "[a];[1:0]volume=" + this.volume2 + "[b];[a][b]amix=inputs=2:duration=" + this.length, "-vn", "-map_metadata", "-1", this.outputPath});
    }
}
