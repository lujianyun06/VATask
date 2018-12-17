package com.example.lll.va.Task5pre;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.lll.va.R;

public class DrawTestView extends View{
    private static final String tag = DrawTestView.class.getName();
    int width = 0;
    int height = 0;
    boolean isFirstDraw = true;
    public DrawTestView(Context context) {
        super(context);
    }

    public DrawTestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(tag, "onmeasure width=" +width + " height=" + height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);


        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(100);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        float pts[] = {0.0f,0f,width, height};
        canvas.drawLine(pts[0], pts[1], pts[2], pts[3], paint);
//        if (isFirstDraw){
            Bitmap src = BitmapFactory.decodeResource(getResources(), R.mipmap.wechatimg1);
            Matrix matrix = new Matrix();
            matrix.setScale(1f*src.getWidth(), 1f*src.getHeight());
            matrix.setRotate(45);
            canvas.setMatrix(matrix);
            canvas.drawBitmap(src, 0, 0, null);

            isFirstDraw = false;
//        }



        Log.d(tag, "ondraw width=" +width + " height=" + height);


    }
}
