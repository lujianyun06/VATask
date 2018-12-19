package com.example.lll.va.task6;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.example.lll.va.R;
import com.example.lll.va.Task5.BufferUtil;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Image {
    private static final String TAG = Image.class.getName();

    //顶点坐标
    static float vertexData[] = {   // in counterclockwise order:
            -5f, -5f, -1f, // bottom left
            5f, -5f, -1f, // bottom right
            -5f, 5f, -1f, // top left
            5f, 5f, -1f,  // top right
    };

    //纹理坐标  对应顶点坐标  与之映射
    static float textureData[] = {   // in counterclockwise order:
            0.0f, 1f, // bottom left
            1f, 1f, // bottom right
            0f, 0f, // top left
            1f, 0f,  // top right
    };

    short index[] = {
        0,2,3,3,1,0
    };

    int one = 0x10000;

    int colorsi[] = {
            one, 0, 0,
    };

    float colorsf[] = {
        0, 0, 0,0,
    };

    //每一次取点的时候取几个点
    //每一次取的总的点 大小


    //位置
    private FloatBuffer vertexBuffer;
    //纹理
    private FloatBuffer textureBuffer;
    //颜色
    private FloatBuffer colorBuffer;

    private ShortBuffer indexBuffer;

    //纹理id
    private int textureId;

    //program
    int program;


    public Image(Activity activity, int program){
        vertexBuffer = BufferUtil.getInstance().initFloatBuffer(vertexData);
        textureBuffer = BufferUtil.getInstance().initFloatBuffer(textureData);
        colorBuffer = BufferUtil.getInstance().initFloatBuffer(colorsf);
        indexBuffer = BufferUtil.getInstance().initShortBuffer(index);

        int[] textureIds = new int[1];
        //创建纹理
        GLES20.glGenTextures(1, textureIds, 0);
        if (textureIds[0] == 0) {
            return;
        }
        textureId = textureIds[0];
        Log.d(TAG, "textureId=" + textureId);
        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        int textureLoc = GLES20.glGetUniformLocation(program, "sTexture");
        GLES20.glUniform1i(textureLoc, 0);

        //环绕（超出纹理坐标范围）  （s==x t==y GL_REPEAT 重复）
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        //过滤（纹理像素映射到坐标点）  （缩小、放大：GL_LINEAR线性）
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.wechatimg1);

        if (bitmap == null) {
            return;
        }
        //设置纹理为2d图片
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        this.program = program;
    }


    public void draw(int vertexPos, int texturePos){

        int colorLoc = GLES20.glGetAttribLocation(program, "a_color");


        GLES20.glEnableVertexAttribArray(colorLoc);
        GLES20.glEnableVertexAttribArray(vertexPos);
        GLES20.glEnableVertexAttribArray(texturePos);
        //设置顶点位置值
        GLES20.glVertexAttribPointer(vertexPos, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        //设置纹理位置值
        GLES20.glVertexAttribPointer(texturePos, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        //设置颜色值
//        GLES20.glVertexAttribPointer(colorLoc, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);

        //绘制 GLES20.GL_TRIANGLE_STRIP:复用坐标
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT,indexBuffer);
        GLES20.glDisableVertexAttribArray(vertexPos);
        GLES20.glDisableVertexAttribArray(texturePos);
        GLES20.glDisableVertexAttribArray(colorLoc);
    }

}
