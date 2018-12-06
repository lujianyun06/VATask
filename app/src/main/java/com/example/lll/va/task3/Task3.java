package com.example.lll.va.task3;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import com.example.lll.va.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Task3 implements View.OnClickListener,
        TextureView.SurfaceTextureListener,
        SurfaceHolder.Callback {
    private final static String tag = Task3.class.getName();
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private CameraUtil mCameraUtil;
    private Context mContext;
    private int height;
    private int width;
    private SurfaceView surfaceView;
    private TextureView textureView;
    private static Task3 task3;
    private Activity mActivity;
    private boolean isSurfaceMode;  //切换surfaceview和textureview的演示

    public static String videoFileName = Environment.getExternalStorageDirectory() + "/test_video.3gp";

    public static void main(Activity activity) {
        task3 = new Task3(activity);
        task3.initView();

    }

    public Task3(Activity activity) {
        mActivity = activity;
        mContext = activity;
    }

    private void initView() {
        mActivity.setContentView(R.layout.activity_task3);

        surfaceView = mActivity.findViewById(R.id.sv_t3);
        textureView = mActivity.findViewById(R.id.texture_view_1);

        //为了知道surface的生命周期，一般只有surface建好后才开始下一步动作
        //surfaceView必须给holder添加callback，TextureView必须给自己添加listener
        if (surfaceView.getVisibility() == View.VISIBLE) {
            isSurfaceMode = true;
            surfaceView.getHolder().addCallback(this);
            mHolder = surfaceView.getHolder();
        } else {
            isSurfaceMode = false;
            textureView.setSurfaceTextureListener(this);
        }

        Button btnTakePic = mActivity.findViewById(R.id.btn_take_pic);
        Button btnStartVideo = mActivity.findViewById(R.id.btn_start_video);
        Button btnStopVideo = mActivity.findViewById(R.id.btn_stop_video);
        btnTakePic.setOnClickListener(this);
        btnStartVideo.setOnClickListener(this);
        btnStopVideo.setOnClickListener(this);
    }

    private void getWidthAndHeight() {
        width = isSurfaceMode ? surfaceView.getWidth() : textureView.getWidth();
        height = isSurfaceMode ? surfaceView.getHeight() : textureView.getHeight();
    }

    public void initCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        Log.d(tag, "is surfaceview = " + isSurfaceMode);
        mCameraUtil = CameraUtil.getInstance();
        mCamera = mCameraUtil.openCamera(0);
        Bundle paramBundle = new Bundle();  //感觉干脆用bundle传参好了
        getWidthAndHeight();
        paramBundle.putInt("width", width);
        paramBundle.putInt("height", height);
        mCameraUtil.setCameraParamter(mContext, mCamera, paramBundle);

        //根据不同的view，选择不同的预览载体
        if (isSurfaceMode) {
            try {

                mCamera.setPreviewDisplay(surfaceView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                mCamera.setPreviewTexture(textureView.getSurfaceTexture());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //预览监听
        mCamera.setPreviewCallback(mCameraCallback);
//        mCamera.setPreviewCallbackWithBuffer(mCameraCallback);
        //开始预览
        mCamera.startPreview();

    }


    /**************************  录视频部分   *******************************/
    MediaRecorder mediaRecorder;

    public void startVideoRecord() {

        mediaRecorder = new MediaRecorder();// 创建mediarecorder对象
        // 设置录制视频源为设置好参数的Camera(相机)
        mediaRecorder.setOnInfoListener(mediaCallbackListener);
        mediaRecorder.setOnErrorListener(mediaCallbackListener);
        mCamera.unlock();

        mediaRecorder.setCamera(mCamera);

        if (isSurfaceMode)//如果给camera设置了setPreviewDisplay，则这句可以不加
            mediaRecorder.setPreviewDisplay(mHolder.getSurface());


        Log.d(tag, "camera + " + mCamera);

        
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
        mediaRecorder
                .setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        // 设置录制的视频编码h264
        //音频编码为AAC
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOrientationHint(90);
        // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
        mediaRecorder.setVideoSize(1600, 1200);
        mediaRecorder.setVideoEncodingBitRate(1024 * 1024 * 5);// 设置编码位率,图像模糊的设置了这个图像就清晰了

        // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
        mediaRecorder.setVideoFrameRate(24);
//        mediaRecorder.setPreviewDisplay(mHolder.getSurface());
        // 设置视频文件输出的路径
        mediaRecorder.setOutputFile(Task3.videoFileName);
        try {
            // 准备录制
            mediaRecorder.prepare();
            // 开始录制
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void stopVideoRecord() {
        if (mediaRecorder == null) return;
        mediaRecorder.stop();
        mediaRecorder.release();

        try {
            mCamera.lock();
            mCamera.reconnect();

            //这或许是camera的bug，预览的时候不能同时进行拍摄，如果拍摄结束后还需要预览
            //要再setPreviewCallback和startPreview，貌似在camera2中解决了
            mCamera.setPreviewCallback(mCameraCallback);
            mCamera.startPreview();
//            mCamera.lock();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private CameraCallback mCameraCallback = new CameraCallback();
    class CameraCallback implements Camera.PreviewCallback{

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            int len = data.length;
            //当相机的预览分辨率为1600 * 1200，预览图像的编码格式为 NV21 即 12bit/pixel 时
            //每一帧图像的大小应该为 1600 * 1200 * 12 / 8 = 2880000 Bytes
            Log.d(tag, "onPreviewFrame data.data len=" + len);

        }
    }


    MediaCallbackListener mediaCallbackListener;

    /************************  SurfaceTextureListener  **********************************/
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (!isSurfaceMode)
            initCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    /************************  SurfaceCallback  **********************************/
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (isSurfaceMode)
            initCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    /**********************************************************/

    class MediaCallbackListener implements MediaRecorder.OnInfoListener, MediaRecorder.OnErrorListener {

        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Log.d(tag, "error");
        }

        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Log.d(tag, "info");
        }
    }


    public void onClick(View v) {
//        if (mCamera == null) return;
        if (v.getId() == R.id.btn_take_pic) //拍照
            takePicture();
        else if (v.getId() == R.id.btn_start_video)
            startVideoRecord();
        else if (v.getId() == R.id.btn_stop_video)
            stopVideoRecord();
    }


    /**************************  拍照部分   *******************************/

    private void takePicture() {
        mCamera.takePicture(null, null, mPictureCallback);
    }


    String fileName = Environment.getExternalStorageDirectory() + "/t3.jpg";
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            SavePicAsyncTask saveBitmapTask = new SavePicAsyncTask();
            saveBitmapTask.execute(data);
            Thread t = new Thread();


            if (mCamera != null) {
                mCamera.startPreview();
            }
        }
    };

    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };

    //这是设置了rotation时调用保存图片的方法
    public void savePic(byte[] data) {
        try {
            File file = new File(fileName);
            FileOutputStream os = new FileOutputStream(file);
            os.write(data);
            os.flush();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mCamera != null) {
            mCamera.startPreview();
        }
    }

    //使用bitmap保存时，因为compress是耗时方法，放在onPictureTaken中会体验极差，所以放个后台任务来压缩保存图片
    private  class SavePicAsyncTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            byte[] data = (byte[]) objects[0];
            Bitmap bm0 = BitmapFactory.decodeByteArray(data, 0, data.length);

            //由于camera.
            Matrix m = new Matrix();
            m.setRotate(90, (float) bm0.getWidth() / 2, (float) bm0.getHeight() / 2); //后面两个是旋转轴心坐标
            Bitmap bitmap = Bitmap.createBitmap(bm0, 0, 0, bm0.getWidth(), bm0.getHeight(), m, false);

            File file = new File(fileName);
            try {
                FileOutputStream os = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, os);
                os.flush();
                os.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    };
}
