package cn.hwh.sports.utils;


import android.content.Context;
import android.media.AudioManager;

import cn.yunzhisheng.tts.offline.basic.ITTSControl;
import cn.yunzhisheng.tts.offline.basic.TTSFactory;

/**
 * 云之声离线语音合成
 *
 * @author Raul
 */
public class UscTTsU {
    private static final String TAG = "UscTTsUtil";

    public static final String appKey = "giduevj3zhzgyfuqsdmebeeu4lngwxibi2aizzib";
    public static final String secret = "ebbbaca287f01e82aaaa422aeb2ce45e";

    private ITTSControl mTTSPlayer;
    private Context mContext;

    /**
     * 构造
     *
     * @param context
     */
    public UscTTsU(Context context) {
        this.mContext = context;
        initTts();
    }

    /**
     * 初始化本地离线TTS
     */
    private void initTts() {

        // 初始化语音合成对象
        mTTSPlayer = TTSFactory.createTTSControl(mContext, appKey);
        // 初始化合成引擎
        mTTSPlayer.init();
        mTTSPlayer.setVoicePitch(1.1f);
        mTTSPlayer.setType(4);

        mTTSPlayer.setStreamType(AudioManager.STREAM_MUSIC);
    }

    /**
     * 播放语音
     *
     * @param voice
     */
    public void showVoice(String voice) {
        mTTSPlayer.play(voice);
    }
}
