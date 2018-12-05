package com.example.lll.va.task1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import com.example.lll.va.R;

public class Task1 {
        // 任务一
    void displayImg(Activity activity){
        final ImageView iv = activity.findViewById(R.id.iv_lu);
        //1
//        iv.setImageResource(R.mipmap.wechatimg1);

        //2
//        Drawable drawable = getResources().getDrawable(R.mipmap.wechatimg1, null);
//        iv.setImageDrawable(drawable);
        //3
        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.wechatimg1);
        iv.setImageBitmap(bitmap);
    }

    void displaySurfaceView(final Activity activity){
        final SurfaceView sv = activity.findViewById(R.id.sv_lu);

        final SurfaceHolder holder = sv.getHolder();
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Canvas canvas = holder.lockCanvas();
                Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.wechatimg1);
                canvas.drawBitmap(bitmap, 0, 0, null);
                holder.unlockCanvasAndPost(canvas);
                holder.lockCanvas(new Rect(0,0,0,0));
                holder.unlockCanvasAndPost(canvas);
            }
        });

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                t.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }
}
