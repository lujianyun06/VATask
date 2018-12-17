package com.example.lll.va.task6pre;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Triangle2 {
    private float vertex[] = {
            1.0f, 1.0f, -0.2f,
            0, -1f, -0.2f,
            -1.0f, 1.0f, -0.2f
    };

    private float color[] = {
            255f, 255f, 0,
    };

    private short[] index = {
            0, 1, 2
    };

    FloatBuffer vertexBuffer;
    FloatBuffer colorBuffer;
    ShortBuffer indexBuffer;

    public Triangle2() {
        vertexBuffer = ByteBuffer.allocateDirect(vertex.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertex);
        vertexBuffer.position(0);

        indexBuffer = ByteBuffer.allocateDirect(vertex.length * 4)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(index);
        indexBuffer.position(0);

        colorBuffer = ByteBuffer.allocateDirect(vertex.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(color);
        colorBuffer.position(0);
    }

    public void draw(int vertexPos, int colorPos) {


        GLES20.glVertexAttribPointer(vertexPos, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(vertexPos);

        GLES20.glVertexAttribPointer(colorPos, 3, GLES20.GL_FLOAT, false, 0, colorBuffer);
        GLES20.glEnableVertexAttribArray(colorPos);
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 3);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 3, GLES20.GL_UNSIGNED_SHORT, indexBuffer);


    }

}
