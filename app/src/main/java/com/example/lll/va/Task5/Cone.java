package com.example.lll.va.Task5;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

//锥体
public class Cone {
    private static String tag = Cone.class.getName();
    private FloatBuffer verticesBuffer;
    private IntBuffer colorBuffer;

    public static int LINE_MODE = 0;
    public static int SOLID_MODE = 1;

    private int mode=0;

    int one = 0x10000;
    private int colors[] = new int[]{
            one, one, 0, one,
            one, 0, one, one,
            0, one, one, 0,
            one, 0, 0, one,
            0, 0, one, 0,
            0, one, 0, 0,
            one, 0, 0, one,
            0, 0, one, 0,
            0, one, 0, 0,
            one, 0, 0, one,
            0, 0, one, 0,
            0, one, 0, 0,

    };

    public Cone(float size, int mode) {
        this.mode = mode;
        float v = size / 2;
        Log.d(tag, "v=" + v);
        //4+3*4 = 16个点 共 48个坐标
        float coneLineVertices[] = new float[]{
                -v, v, 0,
                v, v, 0,

                v, v, 0,
                v, -v, 0,

                v, -v, 0,
                -v, -v, 0,

                -v, -v, 0,
                -v, v, 0,

                -v, v, 0,
                0, 0, -2 * v,

                v, v, 0,
                0, 0, -2 * v,

                v, -v, 0,
                0, 0, -2 * v,

                -v, -v, 0,
                0, 0, -2 * v,
        };

        float coneSolidVertices[] = new float[]{
                0, 0, -2 * v,
                -v, -v, 0,
                -v, v, 0,
                v, -v, 0,
                v, v, 0,

        };

        if (mode == LINE_MODE)
            verticesBuffer = BufferUtil.getInstance().initFloatBuffer(coneLineVertices);
        else
            verticesBuffer = BufferUtil.getInstance().initFloatBuffer(coneSolidVertices);

        colorBuffer = BufferUtil.getInstance().initIntBuffer(colors);
    }

    public void drawSelf(GL10 gl){
        if (mode == LINE_MODE)
            drawLineCone(gl);
        else
            drawSolidCone(gl);
    }

    //画线组成的
    public void drawLineCone(GL10 gl) {
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        gl.glColorPointer(4, GL10.GL_FIXED, 0, colorBuffer);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticesBuffer);
        //画底面

//        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 3);
        for (int i = 0; i < 8; i++) {
            gl.glDrawArrays(GL10.GL_LINES, i * 2, 2);
        }
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    }

    //画面组成的
    public void drawSolidCone(GL10 gl) {
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        gl.glColorPointer(4, GL10.GL_FIXED, 0, colorBuffer);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticesBuffer);

        //画底面
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP,1,4);

        //画4个侧面
        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 5);

        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    }


}
