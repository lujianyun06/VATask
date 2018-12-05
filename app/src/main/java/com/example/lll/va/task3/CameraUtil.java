package com.example.lll.va.task3;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;

import java.security.PublicKey;
import java.util.List;

public class CameraUtil {
    private static final String tag = CameraUtil.class.getName();
    private static CameraUtil util;

    public static CameraUtil getInstance() {
        if (util == null) {
            util = new CameraUtil();
        }
        return util;
    }

    int CameraNum = 0;
    boolean mIsPortrait = true;

    private CameraUtil() {
        CameraNum = Camera.getNumberOfCameras();
        Log.d(tag, "cam_num=" + CameraNum);
    }


    public Camera openCamera(int id) {
        if (CameraNum == 0) return null;
        Camera camera = Camera.open(id); //打开第一个摄像头
        return camera;
    }

    public void setCameraParamter(Context context, Camera camera, Bundle paramBundle) {
        if (camera == null) return;

        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO); //闪光灯模式
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

        int width = paramBundle.getInt("width");
        int height = paramBundle.getInt("height");
        Log.d(tag, "width = " + width + " height =" + height);
        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
        Camera.Size lastSize = getOptimalPreviewSize(context, sizeList, width, height);
        parameters.setPreviewSize(lastSize.width, lastSize.height);
        Log.d(tag, "surwidth = " + width + " surheight =" + height
                + " size width=" + lastSize.width + " size height=" + lastSize.height);
        parameters.setPreviewFormat(ImageFormat.NV21); //默认预览帧格式
        //拍照分辨率和预览分辨率
//        parameters.setPictureSize();
//        parameters.setPreviewSize(); //预览分辨率只能从上面的sizeList里面选取

        followScreenOrientation(context, camera);
        camera.setParameters(parameters);
        //有的手机这么设置会出错，则使用camera.getParameters().setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);的形式设置


    }

    public void followScreenOrientation(Context context, Camera camera) {
        final int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            camera.setDisplayOrientation(180);
            mIsPortrait = false;
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            camera.setDisplayOrientation(90);
            mIsPortrait = true;
        }
    }


    //虽然这样画面还是有一点点扁，还是不知道系统相机是如何做到画面不扁而且还像素高的
    private Camera.Size getOptimalPreviewSize(Context context, List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.2;  //camera宽高比与surface宽高比的最大误差阈值,越小越精确，但可能没有合适的分辨率，一般为0.1或0.2
        final int orientation = context.getResources().getConfiguration().orientation;
        int targetHeight = 0;
        double targetRatio = 0d;
        //由于横竖屏不一样，系统相机的尺寸默认是横屏状态下的，
        // 所以竖屏状态下，比例就是高宽比（因此此时高大于宽），而与系统中高度比较时则应该用宽(哪个小用哪个)
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            targetRatio = (double) w / h;
            targetHeight = h;
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            targetRatio = (double) h / w;
            targetHeight = w;
        }
        //获得surfaceView的宽高比
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE; //在比例合适的情况下，两种高度最低能达到的差值（肯定是越小越符合），一开始设为最大浮点数


        Log.d(tag, "targetHeight=" + targetHeight);
        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {  //遍历当前摄像头支持的分辨率，并计算宽高比
            double ratio = (double) size.width / size.height;
            Log.d(tag, "width=" + size.width + " height=" + size.height);
            Log.d(tag, "target Ratio=" + targetRatio + " ratio=" + ratio);
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue; //如果两个宽高比之差大于阈值，不符合
            if (Math.abs(size.height - targetHeight) < minDiff) { //找到了更小的差值，则替换最合适的比例
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

//         Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {          //当使用阈值找不到时，则忽略比例阈值，直接用最小的高度差的那组分辨率
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
    }
}
