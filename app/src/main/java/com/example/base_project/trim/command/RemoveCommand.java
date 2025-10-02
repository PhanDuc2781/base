package com.example.base_project.trim.command;


import com.example.base_project.command.BaseCommand;

import java.util.ArrayList;

public class RemoveCommand extends BaseCommand {
    private float endTime;
    private String inputPath;
    private String outputPath;
    private boolean reserve1Finish = false;
    private boolean reserve2Finish = false;
    private String reservePath1;
    private String reservePath2;
    private float startTime;
    private float totalTime;

    public RemoveCommand(float f, float f2, float f3, String str, String str2, String str3, String str4) {
        this.startTime = f;
        this.endTime = f2;
        this.totalTime = f3;
        this.inputPath = str;
        this.reservePath1 = str2;
        this.reservePath2 = str3;
        this.outputPath = str4;
    }

    @Override 
    public void execute() {
        execute(new String[]{"ffmpeg", "-ss", String.valueOf(0), "-i", this.inputPath, "-to", String.valueOf(this.startTime), "-c", "copy", this.reservePath1});
    }

    
    @Override 
    public void onExecuteSuccess() {
        if (!this.reserve1Finish) {
            this.reserve1Finish = true;
            execute(new String[]{"ffmpeg", "-ss", String.valueOf(this.endTime), "-i", this.inputPath, "-to", String.valueOf(this.totalTime - this.endTime), "-c", "copy", this.reservePath2});
        } else if (!this.reserve2Finish) {
            this.reserve2Finish = true;
            ArrayList arrayList = new ArrayList();
            arrayList.add("ffmpeg");
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(this.reservePath1);
            arrayList2.add(this.reservePath2);
            int size = arrayList2.size();
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < arrayList2.size(); i++) {
                arrayList.add("-i");
                arrayList.add(arrayList2.get(i));
                stringBuffer.append("[" + i + ":0]");
            }
            arrayList.add("-filter_complex");
            arrayList.add(stringBuffer.toString() + "concat=n=" + size + ":v=0:a=1[out]");
            arrayList.add("-map");
            arrayList.add("[out]");
            arrayList.add("-vn");
            arrayList.add("-map_metadata");
            arrayList.add("-1");
            arrayList.add(this.outputPath);
            execute((String[]) arrayList.toArray(new String[0]));
        } else {
            super.onExecuteSuccess();
        }
    }
}
