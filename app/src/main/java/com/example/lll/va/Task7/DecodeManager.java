package com.example.lll.va.Task7;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
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

public class DecodeManager {
    private static final String tag = DecodeManager.class.getName();
    MediaExtractor mExtractor = null;
    FileOutputStream fos = null;
    Activity mActivity = null;
    MediaCodec mDCodec = null;
    int inputSize = 0;
    int outputSize = 0;
    public String inputFileName = "/sdcard/rise.mp3";  //mime一定要和格式匹配
    public String outputFileName = "/sdcard/test_video-raw";
    public String srcMIMTType = MediaFormat.MIMETYPE_AUDIO_MPEG;
    MediaFormat mFormat = null;

    //解码输出的是pcm文件
    private void initDecodeOutputFile() {
        File outputFile = new File(outputFileName);
        if (!outputFile.exists()) {
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fos = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Log.d(tag, "fos=" + fos);
    }

    /******************************* Decode *****************************/
    public void decodeFile() {
        initDecodeOutputFile();
        try {
            mDCodec = MediaCodec.createDecoderByType(srcMIMTType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mExtractor = new MediaExtractor();
        try {
            mExtractor.setDataSource(inputFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int trackNum = mExtractor.getTrackCount();
        for (int i = 0; i < trackNum; i++) {
            MediaFormat tmpformat = mExtractor.getTrackFormat(i);
            String mime = tmpformat.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio/")) {
                mFormat = tmpformat;
                Log.d(tag, "mime" + mime);
                Log.d(tag, "sampleRate=" + mFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE));
                Log.d(tag, "channelCount" + mFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT));
                mFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 100 * 1024);
                mExtractor.selectTrack(i);
            }
        }
        mDCodec.configure(mFormat, null, null, 0);
        mDCodec.setCallback(mDecodeCallback);
        mDCodec.start();
    }


    private void writeDecodeData2Stream(MediaCodec codec, int index, MediaCodec.BufferInfo info) {
        if (fos == null) try {
            throw new Exception("fos == null!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        MediaFormat format = codec.getOutputFormat(index);
        Log.d(tag, "out format=" + format.getString(MediaFormat.KEY_MIME));

        ByteBuffer outputBuffer = codec.getOutputBuffer(index);
        Log.d(tag, "outputBuffer.limit = " + outputBuffer.limit() + "index = " + index);
        //如果直接定义 data =  new byte[100 * 1024]; 然后调用outputBuffer.get(data),
        // 则会使用data的长度去操作buffer，但buffer没这么大，导致BufferUnderflowException
        //所以下面两种方式选一种

        byte[] data = new byte[outputBuffer.limit()];
        outputBuffer.get(data);

//        byte[] data = new byte[100 * 1024];
//        outputBuffer.get(data,0, outputBuffer.limit());
//
        outputSize += data.length;
        try {
            fos.write(data);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(tag, "flag=" + info.flags);
        Log.d(tag, "outputSize= " + outputSize);
        //用完后释放这个buffer，使其可以接着被使用，如果一直不释放，如果文件太大则会导致缓冲区不够用
        codec.releaseOutputBuffer(index, false);
        if (info.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
            Log.d(tag, "BUFFER_FLAG_END_OF_STREAM");
            stopAndrealseCodec();
            exPCM2WAV();
        }
    }

    private void stopAndrealseCodec() {
        if (mDCodec != null) {
            mDCodec.release();
            mDCodec = null;
            mExtractor.release();
            mExtractor = null;
            Log.d(tag, "outputBuffer BUFFER_FLAG_END_OF_STREAM");
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d(tag, "stopAndrealseCodec");
            Log.d(tag, "inputSize=" + inputSize);
            Log.d(tag, "outputSize=" + outputSize);
        }
    }

    private void exPCM2WAV(){
        //为什么从视频里(只试了mp4和3gp)剥离出来的要 原本的sampleRate/2才是正常速度？
        int sampleRate = mFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        PcmToWavUtil util = new PcmToWavUtil(sampleRate, mFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT),
                AudioFormat.ENCODING_PCM_16BIT);
        Log.d(tag, "sampleRate=" + sampleRate);
        util.pcmToWav(outputFileName, outputFileName + ".wav");
    }

    private MediaCodec.Callback mDecodeCallback = new MediaCodec.Callback() {
        @Override
        public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
            ByteBuffer inputBuffer = codec.getInputBuffer(index);

            Log.d(tag, "\ninputBuffer.limit = " + inputBuffer.limit() + " index=" + index);
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
//            while (true){
            int size = mExtractor.readSampleData(inputBuffer, 0);
            int flag = mExtractor.getSampleFlags();
            long presentation = mExtractor.getSampleTime();
            int offset = 0;
            Log.d(tag, "size= " + size + " flag=" + flag + " presentation=" + presentation);
            if (size != -1) {
                inputSize += size;
                codec.queueInputBuffer(index, offset, size, presentation, flag);
                mExtractor.advance();
            } else { //当文件达到末尾时，用flag
//                return;
                size = 0;
                presentation = 0;
                flag = MediaCodec.BUFFER_FLAG_END_OF_STREAM;
                codec.queueInputBuffer(index, offset, size, presentation, flag);
            }
        }

        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
            writeDecodeData2Stream(codec, index, info);

        }

        @Override
        public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
            Log.d(tag, "onError" + e.toString());
        }

        @Override
        public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {

        }
    };
}
