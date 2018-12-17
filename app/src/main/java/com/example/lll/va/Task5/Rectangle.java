package com.example.lll.va.Task5;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Rectangle {
    private static final String tag = Rectangle.class.getName();
    private IntBuffer rectangleleBuffer;
    int one = 0x10000;  //定义的一个单位长度
    private int[] triangleVertices = new int[]{
            0, one, 0,
            -one, 0, 0,
            0,-one,0,
            one,0,0,
    };


    private byte[] indexs ={
            0,1,2,2,3,0
    };

    public Rectangle() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(triangleVertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());  //绘制的顺序
        rectangleleBuffer = byteBuffer.asIntBuffer();
        rectangleleBuffer.put(triangleVertices);
        rectangleleBuffer.position(0);
    }

    public void drawSelf(GL10 gl) {
        gl.glVertexPointer(3, GL10.GL_FIXED, 0, rectangleleBuffer);
        Random random = new Random();
        int r = random.nextInt() * 255;
        int g = random.nextInt() * 255;
        int b = random.nextInt() * 255;
        gl.glColor4x(r, g, b, 255);

        //三角形带方式绘制矩形，这样的话必须是另一种顺序,当前这种顺序不行
//        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
//        gl.glRotatef(180f,1.0f,0.0f,0.0f); //旋转轴是设置了1的，旋转中心是几何中心？

        //三角形扇方式绘制矩形
//        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);

        //索引方式绘制矩形
        ByteBuffer indices = ByteBuffer.wrap(indexs);
        gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_BYTE, indices);
    }
}
