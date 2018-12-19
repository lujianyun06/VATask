package com.example.lll.va.task6;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ImageRender implements GLSurfaceView.Renderer {
    private static final String TAG = ImageRender.class.getName();
    public Activity activity;
    private int textureId;
    private Triangle2 triangle2;
    int _pointProgram;
    Image image;

    public ImageRender(Activity activity) {
        this.activity = activity;

        boolean isUseFileShader = true;

        if (isUseFileShader) {
            VERTEX_SHADER = getShaderString("gl_Position.glsl");
            FRAGMENT_SHADER = getShaderString("gl_color.glsl");
        }


    }

    int vertexPos;

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
//        triangle2 = new Triangle2();
        GLES20.glUseProgram(_pointProgram);

        image = new Image(activity, _pointProgram);

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


        Matrix.setLookAtM(vM, 0, 0, 0, 0, 0, 0, -10, 0, 1, 0);
        Matrix.frustumM(pM, 0, -ratio, ratio, -1, 1, zNear, zFar);
//        Matrix.setIdentityM(mM, 0);
//        Matrix.multiplyMM(vpM, 0, vM, 0, mM, 0);
        Matrix.multiplyMM(vpM, 0, pM, 0, vM, 0);
//        Log.d(TAG, "mat=" + vpM);


    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//        Log.d(TAG, "_program=" + _pointProgram);
        vertexPos = GLES20.glGetAttribLocation(_pointProgram, "av_Position");
        int texture = GLES20.glGetAttribLocation(_pointProgram, "af_Position");
        int mat = GLES20.glGetUniformLocation(_pointProgram, "mat");
        GLES20.glUniformMatrix4fv(mat, 1, false, vpM, 0);
        Log.d(TAG, "vertexPos=" + texture);


        image.draw(vertexPos, texture);

//        triangle2.draw(pos, color);
    }

    private int loadShader(int type, String source) {
        int shader = GLES20.glCreateShader(type); //针对特定的着色器类型予以构建
        GLES20.glShaderSource(shader, source); //着色器源码通过该函数载入当前对象中
        GLES20.glCompileShader(shader);  //通过该函数编译
        return shader;  //编译后返回
    }

    //从asset中获取shader，在正儿八经的glsl文件里编辑，好处显而易见
    private String getShaderString(String name) {
        StringBuffer stringBuffer = new StringBuffer();
        String tmp = "";
        try {
            InputStream is = activity.getAssets().open(name);
            byte temp[] = new byte[10];
            int len = 0;
            while ((len = is.read(temp)) > 0) {
                //read不会清洗上一次读过得的数据，如果这次只读了3个，只会覆盖上一次的前三个字节，后7个还会存留，所以用本次读了的个数来限定
                tmp = new String(temp, 0, len, Charset.defaultCharset());
                stringBuffer.append(tmp);
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, stringBuffer.toString());
        Log.d(TAG, "\n****************end shader");
        return stringBuffer.toString();
    }

    //
    private static String VERTEX_SHADER = "" +
            "attribute vec4 av_Position;//顶点位置\n" +
            "attribute vec2 af_Position;//纹理位置\n" +
            "attribute vec4 a_color;\n" +
            "varying vec2 v_texPo;//纹理位置  与fragment_shader交互\n" +
            "varying vec4 f_color;\n" +
            "uniform mat4 mat;\n" +
            "void main() {\n" +
            "    v_texPo = af_Position;\n" +
            "    f_color = a_color;\n" +
            "    gl_Position = mat * av_Position;\n" +
            "}\n";

    private static String FRAGMENT_SHADER =
            "precision mediump float;//精度 为float\n" +
                    "varying vec2 v_texPo;//纹理位置  接收于vertex_shader\n" +
                    "varying vec4 f_color;\n" +
                    "\n" +
                    "uniform sampler2D sTexture;//纹理\n" +
                    "\n" +
                    "void main() {\n" +
                    "    vec4 tex2D;\n" +
                    "    tex2D =texture2D(sTexture, v_texPo);\n" +
                    "//    vec4 ff = vec4(0.5,0.0,0.0,0.0);\n" +
                    "    gl_FragColor = tex2D + f_color;\n" +
                    "}\n";
}
