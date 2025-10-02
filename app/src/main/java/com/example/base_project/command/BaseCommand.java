/**
 * Created by nekos on 05/08/2025.
 * Author: DucPd01
 */

package com.example.base_project.command;

import android.util.Log;

import com.example.base_project.R;
import com.example.base_project.applicattion.MainApplication;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;

public abstract class BaseCommand {
    protected Listener listener;

    public interface Listener {
        void onFailure(String str);

        void onProgress(int progress);

        void onStart();

        void onSuccess();
    }

    public abstract void execute();


    public void execute(String[] strArr) {
        try {
            onExecuteStart();
            RxFFmpegInvoke.getInstance().runCommandRxJava(strArr).subscribe(new RxFFmpegSubscriber() {


                @Override
                public void onFinish() {
                    BaseCommand.this.onExecuteSuccess();
                }

                @Override
                public void onProgress(int i, long j) {
                    BaseCommand.this.onExecuteProgress(i, j);
                }

                @Override
                public void onCancel() {
                    BaseCommand.this.onExecuteFailure("CANCEL");
                }

                @Override
                public void onError(String str) {
                    Log.d("ERROR_FFMPEG", str);
                    BaseCommand.this.onExecuteFailure(str);
                }
            });
        } catch (Exception e) {
            /*CommonException.crash(e);*/
        }
    }


    public void onExecuteStart() {
        Listener listener2 = this.listener;
        if (listener2 != null) {
            listener2.onStart();
        }
    }


    public void onExecuteProgress(int i, long j) {
        if (this.listener != null) {
            if (i < 0) {
                i = 0;
            }
            if (i > 100) {
                i = 100;
            }
            double d = (double) j;
            Double.isNaN(d);
            this.listener.onProgress(i);
        }
    }


    public void onExecuteSuccess() {
        if (!MainApplication.Companion.getInstance().storage.getFirst_process_finish()) {
            MainApplication.Companion.getInstance().storage.setFirst_process_finish(true);
        }
        Listener listener2 = this.listener;
        if (listener2 != null) {
            listener2.onSuccess();
        }
    }


    public void onExecuteFailure(String str) {
        Listener listener2 = this.listener;
        if (listener2 != null) {
            listener2.onFailure(str);
        }
    }

    public void setListener(Listener listener2) {
        this.listener = listener2;
    }
}
