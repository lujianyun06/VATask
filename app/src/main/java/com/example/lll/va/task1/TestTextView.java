package com.example.lll.va.task1;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class TestTextView extends View {
    private String tag = "TestTextView";
    public TestTextView(Context context) {
        super(context);
    }

    public TestTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Log.d(tag, "helllllll");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(tag, "w =" + w + " h =" + h);

        //当
//        setMeasuredDimension(w, h);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
