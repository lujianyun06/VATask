package com.example.lll.va.task2;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.lll.va.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioRecorder {

    private String tag = "AudioRecorder";
    //音频输入-麦克风
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    //采用频率
    //44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    //采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    private final static int AUDIO_SAMPLE_RATE = 16000;
    //声道 单声道
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    //编码
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private byte data[];
    private boolean isRecording = false;

    private AudioRecord audioRecord = null;  // 声明 AudioRecord 对象
    private int recordBufSize = 0; // 声明recoordBufffer的大小字段
    private Button btnStart;
    private Button btnStop;

    public static void startTask2byAudioRecord(Activity activity){
        final AudioRecorder audioRecorder = new AudioRecorder();
        audioRecorder.createAudioRecord();
        audioRecorder.btnStart = activity.findViewById(R.id.btn_start_record_audio);
        audioRecorder.btnStop = activity.findViewById(R.id.btn_stop_record_audio);
        audioRecorder.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioRecorder.startRecord();
            }
        });
        audioRecorder.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioRecorder.stopRecord();
            }
        });
    }

    public void createAudioRecord() {
        recordBufSize = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE,
                AUDIO_CHANNEL, AUDIO_ENCODING);  //audioRecord能接受的最小的buffer大小
        audioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, recordBufSize);
        data = new byte[recordBufSize];
    }

    public void startRecord() {
        audioRecord.startRecording();
        isRecording = true;
        thread_w.start();
    }

    private String filename = Environment.getExternalStorageDirectory() + "/test";
    Thread thread_w = new Thread(new Runnable() {
        @Override
        public void run() {

            FileOutputStream os = null;

            try {
                os = new FileOutputStream(filename);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (null != os) {
                Log.d(tag, "isRecording = " + isRecording);
                while (isRecording) {
                    int read = audioRecord.read(data, 0, recordBufSize);
                    Log.d(tag, "read size = " + read);
                    // 如果读取音频数据没有出现错误，就将数据写入到文件
                    if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                        try {
                            os.write(data);
                            Log.d(tag, "os writr = " + data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    public void stopRecord() {
        isRecording = false;
        audioRecord.stop();
        audioRecord.release();
//        thread_w = null;
        PcmToWavUtil util = new PcmToWavUtil(AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING);
        util.pcmToWav(filename, filename + ".wav");
    }

}

