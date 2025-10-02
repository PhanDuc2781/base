/**
 * Created by nekos on 06/08/2025.
 * Author: DucPd01
 */

package com.example.base_project.merge.command;

import com.example.base_project.command.BaseCommand;

import java.util.ArrayList;
import java.util.List;

public class MergeCommand  extends BaseCommand {
    private List<String> inputPathList;
    private String outputPath;

    public MergeCommand(List<String> list, String str) {
        this.inputPathList = list;
        this.outputPath = str;
    }
    @Override
    public void execute() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("ffmpeg");
        int size = this.inputPathList.size();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < this.inputPathList.size(); i++) {
            arrayList.add("-i");
            arrayList.add(this.inputPathList.get(i));
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
    }
}
