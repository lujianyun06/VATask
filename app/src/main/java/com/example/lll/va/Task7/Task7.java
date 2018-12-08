package com.example.lll.va.Task7;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.lll.va.task2.PcmToWavUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.Format;

public class Task7 {
    private static final String tag = Task7.class.getName();
    Activity mActivity = null;



    public static void main(Activity activity) {
        Task7 task7 = new Task7(activity);
        DecodeManager decodeManager = new DecodeManager();
        EncodeManager encodeManager = new EncodeManager();

        encodeManager.encodeFile();
//        decodeManager.decodeFile();


    }

    private Task7(Activity activity) {
        mActivity = activity;
    }



}
