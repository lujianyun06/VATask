package com.example.lll.va.task6;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Triangle2 {
    private float vertex[] = {
            1.0f, 1.0f, -0.2f,
            0, -1f, -0.2f,
            -1.0f, 1.0f, -0.2f,
            0.0F, 2.0F, -0.2f
    };

    private float color[] = {
            255f, 255f, 0,
    };

    private short[] index = {
            0, 1, 2,
            2, 3, 0

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


//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 3);


        int buffers[] = new int[1];
        GLES20.glGenBuffers(1, buffers, 0);

//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
//        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertex.length * 4, vertexBuffer, GLES20.GL_STATIC_DRAW);


//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
//        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, index.length * 4, indexBuffer, GLES20.GL_STATIC_DRAW);

        GLES20.glVertexAttribPointer(vertexPos, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(vertexPos);

//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);


        GLES20.glVertexAttribPointer(colorPos, 3, GLES20.GL_FLOAT, false, 0, colorBuffer);
        GLES20.glEnableVertexAttribArray(colorPos);

        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 6, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

//        GLES20.glDisableVertexAttribArray(vertexPos);


    }

}
