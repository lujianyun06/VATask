package com.example.lll.va.task6pre;

import android.app.Activity;
import android.opengl.GLSurfaceView;

public class Task6pre {
    public Activity mActivity;

    public static void main(Activity activity){
        Task6pre task5 = new Task6pre(activity);
    }

    public Task6pre(Activity activity){
        this.mActivity = activity;
        initView();
    }

    private void initView(){
        GLSurfaceView glSurfaceView = new GLSurfaceView(mActivity);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glSurfaceView.setRenderer(new ImageRender(mActivity));
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        mActivity.setContentView(glSurfaceView);
    }
}
