package com.example.base_project.base;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseScreenActivity extends AppCompatActivity {

    public abstract void initData();


    public abstract void initUI();

    
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        init();
    }

    private void init() {
        initUI();
        initData();
    }
}
