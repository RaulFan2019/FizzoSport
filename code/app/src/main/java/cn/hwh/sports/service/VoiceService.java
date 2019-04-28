package cn.hwh.sports.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.hwh.sports.config.FileConfig;

/**
 * Created by Administrator on 2016/6/21.
 */
public class VoiceService extends Service {


    private MediaPlayer mediaPlayer;//播放器
    private List<Integer> mVoiceList = new ArrayList<>();
    private int mPlayIndex;
    private boolean mIsPlaying = false;

    private static final int MSG_PLAY = 0x01;

    Handler mPlayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_PLAY) {
                if (mIsPlaying) {
                    Message message = new Message();
                    message.what = MSG_PLAY;
                    message.obj = msg.obj;
                    mPlayHandler.sendMessageDelayed(message, 1000);
                } else {
                    mIsPlaying = true;
                    startMergeMp3((List<Integer>) msg.obj);
                }
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initMedia();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            List<Integer> VoiceList = ((List<Integer>) intent.getSerializableExtra("resList"));
            boolean isChongtu = intent.getBooleanExtra("isChongtu", true);
            if (!mIsPlaying || isChongtu) {
                mIsPlaying = true;
                startMergeMp3(VoiceList);
            } else {
                Message message = new Message();
                message.what = MSG_PLAY;
                message.obj = VoiceList;
                mPlayHandler.sendMessageDelayed(message, 1000);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private void initMedia() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mIsPlaying = false;
            }
        });
    }

    //播放下一个语音文件
    private void nextMediaPlay() {
        if (mPlayIndex < mVoiceList.size()) {
            mediaPlay();
        } else {
            mPlayIndex = 0;
            mVoiceList.clear();
        }
    }

    //开始播放
    private void mediaPlay() {
        mediaPlayer.reset();
        AssetFileDescriptor afd = this.getResources().openRawResourceFd(mVoiceList.get(mPlayIndex));
        mPlayIndex++;
        try {
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始合并mp3
     */
    private void startMergeMp3(final List<Integer> VoiceList) {
        List<BufferedInputStream> bisList = new ArrayList<>();
        for (int i = 0, size = VoiceList.size(); i < size; i++) {
            InputStream fileDescriptor = this.getResources().openRawResource(VoiceList.get(i));//从外部assets获取MP3文件
            BufferedInputStream bis = new BufferedInputStream(fileDescriptor, 10000);//转换缓冲流
            bisList.add(bis);
        }
        File file = new File(FileConfig.RECORD_PATH + File.separator + "marge.mp3");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos, 10000);//缓冲刘
            byte input[] = new byte[200];
            int count = 0;
            for (int i = 0, size = bisList.size(); i < size; i++) {
                BufferedInputStream bis = bisList.get(i);
                while (bis.read(input) != -1) {
                    bos.write(input);
                    count++;
                }
                bis.close();
            }
            bos.close();
            fos.close();
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(FileConfig.RECORD_PATH + File.separator + "marge.mp3");
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            mIsPlaying = false;
            e.printStackTrace();
        } catch (IOException e) {
            mIsPlaying = false;
            e.printStackTrace();
        }

    }
}
