package com.example.lll.va.task6pre;

import android.app.Activity;

import com.example.lll.va.Task5.BufferUtil;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Image {
    FloatBuffer verticesBuffer = null;
    FloatBuffer textureBuffer = null;

    private static final float[] vertexData = {
            0f,    0f, 0.5f, 0.5f,
            -0.5f, -0.8f,   0f,   1f,
            0.5f, -0.8f,   1f,   1f,
            0.5f,  0.8f,   1f,   0f,
            -0.5f,  0.8f,   0f,   0f,
            -0.5f, -0.8f,   0f,   1f
    };

    private Activity activity;

    public Image(Activity activity){
        verticesBuffer = BufferUtil.getInstance().initFloatBuffer(vertexData);
    }

    public void draw(GL10 gl){

    }

}
