package com.example.lll.va.Task5pre;

import android.app.Activity;

import com.example.lll.va.R;

public class Task5min {
    public Activity mActivity;

    public static void main(Activity activity){
        Task5min task5min = new Task5min(activity);
    }

    public Task5min(Activity activity){
        this.mActivity = activity;
        initView();
    }

    private void initView(){
        mActivity.setContentView(R.layout.activity_task5min);
    }
}
