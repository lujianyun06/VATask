package com.example.lll.va.task1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.lll.va.R;

import java.security.MessageDigest;

public class DrawView extends View {

    String TAG = "DrawView";
    Bitmap bitmap;
    Bitmap orginBitmap;
    boolean bitmapFinished = false;


    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        orginBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.wechatimg1);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(TAG, "w =" + w + " h =" + h);
        if (!bitmapFinished) {

            bitmap = Bitmap.createScaledBitmap(orginBitmap, w/2, h/2, false);
            bitmapFinished = true;
        }
        //当
//        setMeasuredDimension(w, h);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null)
            canvas.drawBitmap(bitmap, 0, 0, null);
    }

}
