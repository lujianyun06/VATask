package com.example.lll.va.task4;

import android.app.Activity;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.example.lll.va.task3.Task3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.Format;
import java.util.ArrayList;

public class Task4 {
    public static final String tag = Task4.class.getName();
//        static String videoFileName = Environment.getExternalStorageDirectory() + "/love.3gp";
    static String videoFileName = Task3.videoFileName;
    static String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    static String outFileName = Environment.getExternalStorageDirectory() + "/task4_new.mp4";
    long audioDuration = 0;
    long videoDuration = 0;
    private Activity mActivity;
    private int videoTrackIndex = 0;
    private int audioTrackIndex = 0;
    private int audioTrackIndex2 = 0;

    public static void main(Activity activity) {
        Task4 task4 = new Task4(activity);
        task4.run();
    }

    public void run() {
        extractVideoAndMux();

    }

    public Task4(Activity activity) {
        this.mActivity = activity;
    }


    private void extractVideoAndMux() {
        MediaMuxer vMediaMuxer = null;
        try {
            vMediaMuxer = new MediaMuxer(outFileName, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MediaExtractor vextractor = new MediaExtractor();
        MediaExtractor aextractor = new MediaExtractor();
        MediaFormat audioFormat, videoFormat;

        videoFormat = extractVideo(vMediaMuxer, vextractor);
        audioFormat = extractAudio(vMediaMuxer, aextractor);

        //能获得的文件最大值，单纯的音频文件好像没这个值
//        int audioMaxInputSize = audioFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
        int videoMaxInputSize = videoFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);

        //持续时间,都是以μs为单位的
        audioDuration = audioFormat.getLong(MediaFormat.KEY_DURATION);
        videoDuration = videoFormat.getLong(MediaFormat.KEY_DURATION);
        Log.d(tag, "videoTime=" + videoDuration + " audioTime=" + audioDuration);

        vMediaMuxer.setOrientationHint(90);
        vMediaMuxer.start();

        mux(vMediaMuxer, vextractor, aextractor, videoDuration);

        vextractor.release();
        vextractor = null;

        // 释放MediaMuxer
//        vMediaMuxer.stop();
        vMediaMuxer.release();

    }

    private MediaFormat extractVideo(MediaMuxer vMediaMuxer, MediaExtractor vextractor) {
        MediaFormat format = null;
        try {
            vextractor.setDataSource(videoFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int numTracks = vextractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            format = vextractor.getTrackFormat(i);

            String mime = format.getString(MediaFormat.KEY_MIME);
            boolean isVideoTrack = mime.startsWith("video/");

            Log.d(tag, "mime=" + mime + " i=" + i);
            if (isVideoTrack) {
                vextractor.selectTrack(i);
                //新视频的视频轨编号
                videoTrackIndex = vMediaMuxer.addTrack(format);
                Log.d(tag, "audio track=" + videoTrackIndex);
                break;

            }
        }
        return format;

    }

    private MediaFormat extractAudio(MediaMuxer vMediaMuxer, MediaExtractor aextractor) {
        MediaFormat format = null;

        try {
            String audioSrcName = sdcardPath + "/rise.aac";
//            String audioSrcName = Task3.videoFileName;
            aextractor.setDataSource(audioSrcName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int numTracks = aextractor.getTrackCount();
        Log.d(tag, "numtracks=" + numTracks);
        for (int i = 0; i < numTracks; i++) {
            format = aextractor.getTrackFormat(i);

            String mime = format.getString(MediaFormat.KEY_MIME);
            boolean isAudioTrack = mime.startsWith("audio/");

            Log.d(tag, "mime=" + mime + " i=" + i);
            if (isAudioTrack) {
                Log.d(tag, "format=" + format);

                aextractor.selectTrack(i);
                //新视频的音频轨编号
                audioTrackIndex = vMediaMuxer.addTrack(format);
                Log.d(tag, "audio track=" + audioTrackIndex);
                break;

            }
        }
        Log.d(tag, "format=" + format);
        return format;
    }


    private void mux(MediaMuxer vMediaMuxer, MediaExtractor vextractor, MediaExtractor aextractor,
                     long duration) {

        if (vextractor == null || vMediaMuxer == null) try {
            throw new Exception("MediaExtractor and MediaMuxer should not be null!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ByteBuffer videoBuffer = ByteBuffer.allocate(1000 * 1024);
        MediaCodec.BufferInfo videoInfo = new MediaCodec.BufferInfo();

        while (true) {

            int size = vextractor.readSampleData(videoBuffer, 0);
//            Log.d(tag, "video size = " + size);
            if (size == 0 || size == -1) break;

            videoInfo.presentationTimeUs = vextractor.getSampleTime();
            videoInfo.offset = 0;
            videoInfo.flags = vextractor.getSampleFlags();
            videoInfo.size = size;
            Log.d(tag, "video time=" + videoInfo.presentationTimeUs + " size=" + videoInfo.size + " flag=" + videoInfo.flags
                    + " videoTrackIndex=" + videoTrackIndex);
            vMediaMuxer.writeSampleData(videoTrackIndex, videoBuffer, videoInfo);

            vextractor.advance();
        }


        if (aextractor == null) return;

        ByteBuffer audioBuffer = ByteBuffer.allocate(1000 * 1024);
        long savedTime = 0;
        MediaCodec.BufferInfo audioInfo = new MediaCodec.BufferInfo();
        long halfPos = audioDuration / 2;
        while (true) {
            long presentationTimeUs = aextractor.getSampleTime();

            //从音频的一半处开始混合，相当于把音频的前一半给扔掉了
            if (presentationTimeUs < halfPos ){
                aextractor.advance(); //快进到下一帧
                continue;
            }
            //当前时间-音频时长的一半 就是合成的时间，因为是从一半开始合成的，合成大于视频时长时就把后面的丢弃
            if (duration < presentationTimeUs - halfPos) {
                break;
            }

            int size = aextractor.readSampleData(audioBuffer, 0);
//            Log.d(tag, "audio size = " + size);
            if (size == 0 || size == -1) break;
            //这是这一帧在新合成的视频中的时间戳，一定要搞对时间关系
            audioInfo.presentationTimeUs = presentationTimeUs - halfPos;
            audioInfo.offset = 0;
            audioInfo.flags = aextractor.getSampleFlags();
            audioInfo.size = size;
            Log.d(tag, "audio time=" + audioInfo.presentationTimeUs + " size=" + audioInfo.size + " flag=" + audioInfo.flags
                    + " audioTrackIndex=" + audioTrackIndex);
            vMediaMuxer.writeSampleData(audioTrackIndex, audioBuffer, audioInfo);

            aextractor.advance();
        }

    }
}

