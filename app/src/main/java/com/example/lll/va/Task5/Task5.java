package com.example.lll.va.Task5;

import android.app.Activity;
import android.opengl.GLSurfaceView;

public class Task5 {
    public Activity mActivity;

    public static void main(Activity activity){
        Task5 task5 = new Task5(activity);
    }

    public Task5(Activity activity){
        this.mActivity = activity;
        initView();
    }

    private void initView(){
        GLSurfaceView glSurfaceView = new GLSurfaceView(mActivity);
        glSurfaceView.setRenderer(new SVRenderer());
        mActivity.setContentView(glSurfaceView);
    }
}
