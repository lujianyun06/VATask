package com.example.lll.va.Task5;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Triangle {
    private static String tag = Triangle.class.getName();
    private IntBuffer triangleBuffer;
    int one = 0x10000;  //定义的一个单位长度
    private int[] triangleVertices = new int[]{
            0,one,0, //0,one,0,
            -one,-one,0, //0,one,0,
            one,-one,one, //0,one,0,
    };

    private IntBuffer colorBuffer;
    private int[] colors = new int[]{
            0,one,0,one/2,
            1,1,0,1,
            1,one,one,one/2

    };

    private short[] shorts = new short[]{
        0,1,2
    };


    public Triangle(){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(triangleVertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        triangleBuffer = byteBuffer.asIntBuffer();
        triangleBuffer.put(triangleVertices);
        triangleBuffer.position(0);
        Log.d(tag, "one=" + one);

        ByteBuffer colorByteBuffer = ByteBuffer.allocateDirect(colors.length * 4);
        colorByteBuffer.order(ByteOrder.nativeOrder());
        colorBuffer = colorByteBuffer.asIntBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);
        Log.d(tag, "one=" + one);

    }

    public void drawSelf(GL10 gl){
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        gl.glColorPointer(4,GL10.GL_FIXED,0,colorBuffer);
        gl.glVertexPointer(3,GL10.GL_FIXED, Integer.SIZE/8 * 3, triangleBuffer);

        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 3);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    }
}
