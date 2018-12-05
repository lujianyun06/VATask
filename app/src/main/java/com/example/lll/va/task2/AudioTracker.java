package com.example.lll.va.task2;

import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.lll.va.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AudioTracker {
    private String tag = "AudioTracker";
    //采用频率
    private final static int AUDIO_SAMPLE_RATE = 16000;
    //声道 单声道
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_OUT_MONO;
    //编码
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioTrack audioTrack;
    private Activity activity;
    private int buffersize;
    private byte[] data;
    private boolean isPlay = false;
    private boolean isFirstPlay = true; //用firstPlay和isPlay组合，可以完成暂停和继续播放的功能

    public static void startTask2byAudioTrack(Activity activity) {
        final AudioTracker audioTracker = new AudioTracker();
        audioTracker.activity = activity;
        Button btnPlay = activity.findViewById(R.id.btn_play_audio);
        Button btnStop = activity.findViewById(R.id.btn_stop_audio);
        Button btnPause = activity.findViewById(R.id.btn_pause_audio);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioTracker.startPlay();
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioTracker.stopPlay();
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioTracker.pause();
            }
        });

    }

    //AudioAttributes取代了流类型的概念（例如参见AudioManager.STREAM_MUSIC或AudioManager.STREAM_ALARM），
    // 用于定义音频播放的行为。 通过允许应用程序定义，属性允许应用程序指定比在流类型中传达的信息更多的信息：


    public void initAudioTrack() {
        if (isFirstPlay) {
            buffersize = AudioTrack.getMinBufferSize(AUDIO_SAMPLE_RATE,
                    AUDIO_CHANNEL, AUDIO_ENCODING);  //audioTracker能接受的最小的buffer大小
            audioTrack = new AudioTrack(AudioAttributes.CONTENT_TYPE_MUSIC, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL
                    , AudioFormat.ENCODING_PCM_16BIT, buffersize, AudioTrack.MODE_STREAM);
            data = new byte[buffersize];
        }

    }

    public void startPlay() {
        initAudioTrack();
        audioTrack.play();
        isPlay = true;
        playThread.start();

    }

    public void pause() {
        audioTrack.stop();
        isPlay = false;
        Log.d(tag, "pasue ");
    }

    public void stopPlay() {
        audioTrack.stop();
        isPlay = false;
        isFirstPlay = true;
        audioTrack.release();
        Log.d(tag, "stop ");
        is = null;
    }

    FileInputStream is = null;
    String fileName = Environment.getExternalStorageDirectory() + "/test.wav";
    Thread playThread = new Thread(new Runnable() {
        @Override
        public void run() {
            File file = new File(fileName);

            try {
                if (isFirstPlay) { //如果是首次播放（停止后再播放也算首次）则初始化流
                    is = new FileInputStream(file);
                    isFirstPlay = false;
                }
                Log.d(tag, "is = " + is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            writeData();
        }
    });

    private void writeData() {
        if (is == null) return;
        while (isPlay) {
            try {
                int read = is.read(data);
                Log.d(tag, "read = " + read);
                if (read != 0 && read != -1) {
                    audioTrack.write(data, 0, read);
                } else {
                    stopPlay();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
