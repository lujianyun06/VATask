package com.example.lll.va.task6pre;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.xml.transform.Source;

public class ImageRender implements GLSurfaceView.Renderer {
    private static final String TAG = ImageRender.class.getName();
    public Activity activity;
    private int textureId;
    private Triangle2 triangle2;
    int _pointProgram;

    public ImageRender(Activity activity) {
        this.activity = activity;
        VERTEX_SHADER = getShaderString("gl_Position.glsl");
        FRAGMENT_SHADER = getShaderString("gl_color.glsl");


    }

    int pos;

    @Override
    public void onSurfaceCreated(GL10 unusedgl, EGLConfig config) {
        GLES20.glClearColor(0, 0, 0, 0);

        int vShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);

        _pointProgram = GLES20.glCreateProgram();
//绑定程序和着色器
        GLES20.glAttachShader(_pointProgram, vShader);
        GLES20.glAttachShader(_pointProgram, fShader);
        GLES20.glLinkProgram(_pointProgram); //链接着色器单元
        triangle2 = new Triangle2();
        GLES20.glUseProgram(_pointProgram);


    }

    float vpM[] = new float[16];

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        float zNear = 0.1f;
        float zFar = 100f;
        float mM[] = {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1};
        float vM[] = new float[16];
        float pM[] = new float[16];
        float fov = 0.75f; //摄像机角度
        float size = (float) (zNear * Math.tan(fov / 2));


        Matrix.setLookAtM(vM, 0, 0, 0, 5, 0, 0, -100, 0, 1, 0);
        Matrix.frustumM(pM, 0, -size, size, -size / ratio, size / ratio, zNear, zFar);
        Matrix.setIdentityM(mM, 0);
//        Matrix.multiplyMM(vpM, 0, mM, 0, mM, 0);
        Matrix.multiplyMM(vpM, 0, pM, 0, vM, 0);
        Log.d(TAG, "mat=" + vpM);


    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//        Log.d(TAG, "_program=" + _pointProgram);
        pos = GLES20.glGetAttribLocation(_pointProgram, "vPosition");
        int color = GLES20.glGetAttribLocation(_pointProgram, "aColor");
        int matLoc = GLES20.glGetUniformLocation(_pointProgram, "mvp");
        GLES20.glUniformMatrix4fv(matLoc, 1, false, vpM, 0);


//        Log.d(TAG,"color=" + color);
//        Log.d(TAG,"pos=" + pos);
//        Log.d(TAG,"mat=" + matLoc);
        Log.d(TAG, "pos=" + pos);
        triangle2.draw(pos, color);
    }

    private int loadShader(int type, String source) {
        int shader = GLES20.glCreateShader(type); //针对特定的着色器类型予以构建
        GLES20.glShaderSource(shader, source); //着色器源码通过该函数载入当前对象中
        GLES20.glCompileShader(shader);  //通过该函数编译
        return shader;  //编译后返回
    }

    private String getShaderString(String name) {
        StringBuffer stringBuffer = new StringBuffer();
        String tmp = "";
        try {
            InputStream is = activity.getAssets().open(name);
            byte temp[] = new byte[10];
            while (-1 != is.read(temp)) {
                tmp = new String(temp);
                stringBuffer.append(tmp);
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, stringBuffer.toString());
        return stringBuffer.toString();
    }

    //
    private static String VERTEX_SHADER = "" +
            "attribute vec3 vPosition;\n" +
            "attribute vec4 aColor;\n" +
            "varying vec4 vColor;\n" +
            "uniform mat4 mvp;\n" +
            "void main()\n" +
            "{\n" +
            "    vec4 pp = vec4(vPosition, 1.0);\n" +
            "    vColor = aColor;\n" +
            "    gl_Position = mvp * pp ;\n" +
            "}"

            + "";

    private static String FRAGMENT_SHADER =
            "#ifdef GL_FRAGMENT_PRECISION_HIGH\n" +
                    "precision highp float;\n" +
                    "#else\n" +
                    "precision mediump float;\n" +
                    "#endif\n" +
                    "varying vec4 vColor;\n" +
                    "\n" +
                    "void main() {\n" +
                    "    gl_FragColor = vColor;\n" +
                    "" +
                    "}";
}
