package com.example.lll.va.Task7;

import android.app.Activity;
import android.graphics.Paint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class EncodeManager {
    private static final String tag = EncodeManager.class.getName();
    MediaExtractor mExtractor = null;
    FileOutputStream fos = null;
    Activity mActivity = null;
    int inputSize = 0;
    int outputSize = 0;
    MediaFormat outFormat = null;
    private String desMIMEType = MediaFormat.MIMETYPE_AUDIO_AAC;
    private int SAMPLE_RATE = 44100;
    private int CHANNEL_COUNT = 2;
    private String inputFileName = "/sdcard/test_video-raw.wav";
    private String outputFileName = "/sdcard/task7.aac";

    private MediaCodec mECodec = null;
    int samplingFreq[] = {
            96000, 88200, 64000, 48000, 44100, 32000, 24000, 22050,
            16000, 12000, 11025, 8000
    };

    public void encodeFile() {
        int channelCount = 2;
        outFormat = MediaFormat.createAudioFormat(desMIMEType, SAMPLE_RATE, CHANNEL_COUNT);
        outFormat.setInteger(MediaFormat.KEY_BIT_RATE, 96000);
        outFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        outFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 100 * 1024);
        int aacObjLC = MediaCodecInfo.CodecProfileLevel.AACObjectLC;


        // Search the Sampling Frequencies9
        int sampleIndex = -1;
        for (int i = 0; i < samplingFreq.length; ++i) {
            if (samplingFreq[i] == SAMPLE_RATE) {
                Log.d("TAG", "kSamplingFreq " + samplingFreq[i] + " i : " + i);
                sampleIndex = i;
            }
        }
        //添加csd-0，如果是视频，则csd-0和csd-1都要有
        ByteBuffer csd = ByteBuffer.allocate(2);
        csd.put((byte) ((aacObjLC << 3) | (sampleIndex >> 1)));
        csd.position(1);
        csd.put((byte) ((byte) ((sampleIndex << 7) & 0x80) | (channelCount << 3)));
        csd.flip();
        outFormat.setByteBuffer("csd-0", csd); // add csd-0
        System.out.println(Arrays.toString(csd.array()) + "===++");


        mExtractor = null;
        mExtractor = new MediaExtractor();
        try {
            mExtractor.setDataSource(inputFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int trackNum = mExtractor.getTrackCount();
        for (int i = 0; i < trackNum; i++) {
            MediaFormat format = mExtractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio/")) {
                mExtractor.selectTrack(i);
                Log.d(tag, "extractor mime=" + mime);
            }
        }

        try {
            //使用createEncoderByType怎么都无法创建，报错Unable to instantiate a encoder for type 'audio/ac3' with err 0xfffffffe.
            //看官方文档，发现推荐的方法是把要编解码的format传给MediaCodecList去找到一个name，用这个name去创建
            //createDecoder / EncoderByType（String）为特定MIME类型创建首选编解码器。但是，这不能用于注入功能，
            // 并且可能会创建无法处理特定所需媒体格式的编解码器。
            MediaCodecList list = new MediaCodecList(MediaCodecList.ALL_CODECS);
            MediaCodecInfo[] infos = list.getCodecInfos();
            for (int i = 0; i < infos.length; i++) {
                String name = infos[i].getName();
                Log.d(tag, "i=" + i + " name=" + name);
            }

            String encodeName = list.findEncoderForFormat(outFormat);
            Log.d(tag, "encodeName=" + encodeName);
            mECodec = MediaCodec.createByCodecName(encodeName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initEncodeOutputFile();
        mECodec.setCallback(mEncodeCallback);
        mECodec.configure(outFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

        mECodec.start();

    }

    //每一帧前面都要加上ADTS头，可以看做是每一个AAC帧的帧头
    private void addADTStoPacket(byte[] packet, int packetLen) {
        int profile = 2;  //AAC LC
        //39=MediaCodecInfo.CodecProfileLevel.AACObjectELD;
        int freqIdx = 4;  //44.1KHz
        int chanCfg = 2;  //CPE

//        byte[] packet1 = new byte[packetLen];
        // fill in ADTS data
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
//
//
//        //把这些清晰的放出来，但最好还是用上面的方式
//        //syncword
//        packet[0] = (byte) 0xFF;
//        packet[1] |= 0xF << 4;
//        //id
//        packet[1] |= 0x1 << 3;
//        //layer
//        packet[1] |= 0x00 << 1;
//        //protection_abscent
//        packet[1] |= 0x1;
//        //profile
//        packet[2] |= (profile -1) << 6;
//        //sampling_frequency_index
//        packet[2] |= freqIdx << 2;
//        //private bit
//        packet[2] |= 0x0 << 1;
//        //channel_config
//        packet[2] |= chanCfg >> 2;  //高1位
//        packet[3] |= chanCfg << 6;   //低2位
//        //copy and home;
//        packet[3] |= 0x00 << 4;
//        //cib and cis
//        packet[3] |= 0x00 << 2;
//        //frame_length,单位是字节。不用管int是几个字节，把它当13位就行了,一帧的长度一般不可能超过13位能表达的最大值(8KB),
//        packet[3] |= packetLen >> 11;
//        packet[4] = (byte) (packetLen >> 3);
//        int x = packetLen << 5;
//        packet[5] |= packetLen << 5;
//
//        packet[5] |= 0x7FF >> 6;
//        packet[6] |= 0x7FF << 2;
//
//        for (int i = 0; i < 7; i++) {
//            Log.d(tag, "packet " + i + "=" + packet[i]);
//        }
    }


    private void stopAndrealseCodec() {
        if (mECodec != null) {
            mECodec.release();
            mECodec = null;
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


    private void initEncodeOutputFile() {
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

    private MediaCodec.Callback mEncodeCallback = new MediaCodec.Callback() {
        @Override
        public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {

            ByteBuffer inputBuffer = codec.getInputBuffer(index);
            Log.d(tag, "\ninputBuffer.limit = " + inputBuffer.limit() + " index=" + index);
            int size = mExtractor.readSampleData(inputBuffer, 0);

            inputSize += size;
            int flag = mExtractor.getSampleFlags();
            long presentation = mExtractor.getSampleTime();
            int offset = 0;
            //最后一个帧的话，size为0，把flag置为BUFFER_FLAG_END_OF_STREAM来表示结束
            if (size != -1 && size != 0) {
                codec.queueInputBuffer(index, offset, size, presentation, flag);
                mExtractor.advance();
            } else {
                flag = MediaCodec.BUFFER_FLAG_END_OF_STREAM;
                codec.queueInputBuffer(index, offset, 0, 0, flag);

            }
            Log.d(tag, "\ninputSize= " + inputSize);
        }

        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
            if (fos == null) try {
                throw new Exception("fos == null!");
            } catch (Exception e) {
                e.printStackTrace();
            }
            int outIndex = index;
            MediaFormat format = codec.getOutputFormat(outIndex);
            Log.d(tag, "out format=" + format.getString(MediaFormat.KEY_MIME));

            ByteBuffer outputBuffer = codec.getOutputBuffer(outIndex);
            Log.d(tag, "outputBuffer.limit = " + outputBuffer.limit() + "outIndex = " + outIndex);


//        byte[] data = new byte[100 * 1024];
//        outputBuffer.get(data,0, outputBuffer.limit());
            int size = outputBuffer.limit();
            outputSize += size;
            byte[] packedData = new byte[size + 7];
            addADTStoPacket(packedData, packedData.length);
            outputBuffer.get(packedData, 7, size);
            try {
                fos.write(packedData);
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(tag, "flag=" + info.flags);
            Log.d(tag, "outputSize= " + outputSize);
            //用完后释放这个buffer，使其可以接着被使用，如果一直不释放，如果文件太大则会导致缓冲区不够用
            outputBuffer.clear();
            codec.releaseOutputBuffer(outIndex, false);
            if (info.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                Log.d(tag, "BUFFER_FLAG_END_OF_STREAM");
            }
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
