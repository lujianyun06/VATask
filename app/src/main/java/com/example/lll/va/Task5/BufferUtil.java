package com.example.lll.va.Task5;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class BufferUtil {


    public static BufferUtil instance = null;

    public static BufferUtil getInstance() {
        if (instance == null) {
            instance = new BufferUtil();
        }
        return instance;
    }

    private BufferUtil() {

    }

    public FloatBuffer initFloatBuffer(float[] datas) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(datas.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = byteBuffer.asFloatBuffer();
        buffer.put(datas);
        buffer.position(0);
        return buffer;
    }

    public IntBuffer initIntBuffer(int[] datas) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(datas.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        IntBuffer buffer = byteBuffer.asIntBuffer();
        buffer.put(datas);
        buffer.position(0);
        return buffer;
    }


}
