package com.example.base_project.split;


import com.example.base_project.command.BaseCommand;

public class SplitCommand extends BaseCommand {
    private float endTime;
    private String inputPath;
    private String outputPath1;
    private String outputPath2;
    private boolean split1Finish = false;
    private float splitTime;

    public SplitCommand(float f, float f2, String str, String str2, String str3) {
        this.splitTime = f;
        this.endTime = f2;
        this.inputPath = str;
        this.outputPath1 = str2;
        this.outputPath2 = str3;
    }

    @Override 
    public void execute() {
        execute(new String[]{"ffmpeg", "-ss", String.valueOf(0), "-i", this.inputPath, "-to", String.valueOf(this.splitTime), "-vn", "-map_metadata", "-1", "-c", "copy", this.outputPath1});
    }


    @Override 
    public void onExecuteSuccess() {
        if (!this.split1Finish) {
            this.split1Finish = true;
            execute(new String[]{"ffmpeg", "-ss", String.valueOf(this.splitTime), "-i", this.inputPath, "-to", String.valueOf(this.endTime - this.splitTime), "-vn", "-map_metadata", "-1", "-c", "copy", this.outputPath2});
            return;
        }
        super.onExecuteSuccess();
    }
}
