package cn.hwh.sports.utils;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.hwh.sports.R;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.data.sp.SportSpData;
import cn.hwh.sports.service.VoiceService;

/**
 * Created by Administrator on 2016/6/21.
 */
public class TTsU {

    static int[] rawNum = {R.raw.n_0, R.raw.n_1, R.raw.n_2, R.raw.n_3, R.raw.n_4, R.raw.n_5, R.raw.n_6, R.raw.n_7, R.raw.n_8, R.raw.n_9};
    static int[] rawUnit = {R.raw.n_10, R.raw.n_100, R.raw.n_1000, R.raw.n_10000};
    static int[] rawComeont = {R.raw.comeon1, R.raw.comeon2};
    static int[] zones = {R.raw.zone_0, R.raw.zone_1, R.raw.zone_2, R.raw.zone_3, R.raw.zone_4, R.raw.zone_5};

    /**
     * 开始跑步
     */
//    public static void playStartRun(final Context context, final int count) {
//        if (SportSpData.getTtsEnable(context)) {
//            ArrayList<Integer> resList = new ArrayList<>();
//            if (count == 1) {
//                resList.add(R.raw.start3);
//            } else {
//                resList.add(R.raw.start1);
//                resList.addAll(NumToVoiceList(count));
//                resList.add(R.raw.start2);
//            }
//            play(context, resList, true);
//        }
//    }

    /**
     * 继续跑步
     */
    public static void playContinueRun(final Context context) {
        if (SportSpData.getTtsEnable(context)) {
            ArrayList<Integer> resList = new ArrayList<>();
            resList.add(R.raw.resume);
            play(context, resList, true);
        }
    }

    /**
     * 暂停跑步
     */
    public static void playPauseRun(final Context context) {
        if (SportSpData.getTtsEnable(context)) {
            ArrayList<Integer> resList = new ArrayList<>();
            resList.add(R.raw.pause);
            play(context, resList, true);
        }
    }

    /**
     * 跑步结束
     *
     * @param context
     */
    public static void playFinishRun(final Context context) {
        if (SportSpData.getTtsEnable(context)) {
            ArrayList<Integer> resList = new ArrayList<>();
            resList.add(R.raw.end);
            play(context, resList, true);
        }
    }

    /**
     * 接近报警心率
     * @param context
     */
    public static void playCloseToWarningHr(final Context context){
        if (SportSpData.getTtsEnable(context)) {
            ArrayList<Integer> resList = new ArrayList<>();
            resList.add(R.raw.close_to_warning_hr);
            play(context, resList, true);
        }
    }

    /**
     * 超过报警心率
     * @param context
     */
    public static void playOutWarningHr(final Context context){
        if (SportSpData.getTtsEnable(context)) {
            ArrayList<Integer> resList = new ArrayList<>();
            resList.add(R.raw.out_warning_hr);
            play(context, resList, true);
        }
    }

    /**
     * 室内运动每分钟播报
     *
     * @param type
     * @param hr
     * @param cadence
     * @param zone
     */
    public static void playMinuteIndoorVoice(final Context context, final int type, final int hr,
                                             final int cadence, final int zone) {
        if (SportSpData.getTtsEnable(context)) {
            ArrayList<Integer> resList = new ArrayList<>();
            resList.add(R.raw.tip);
            resList.add(R.raw.curr_hr);
            resList.addAll(NumToVoiceList(hr));
            //若是室内跑步
            if (type == SportEnum.EffortType.RUNNING_INDOOR && cadence != 0) {
                resList.add(R.raw.curr_cadence);
                resList.addAll(NumToVoiceList(cadence));
            }
            resList.add(R.raw.at);
            resList.add(zones[zone]);
            play(context, resList, false);
        }
    }

    /**
     * 室内运动每分钟播报
     *
     * @param hr
     * @param cadence
     * @param zone
     */
    public static void playMinuteOutdoorVoice(final Context context,final int hr,
                                              final int cadence, final int zone) {
        if (SportSpData.getTtsEnable(context)) {
            ArrayList<Integer> resList = new ArrayList<>();
            resList.add(R.raw.tip);
            resList.add(R.raw.curr_hr);
            resList.addAll(NumToVoiceList(hr));
            if (cadence != 0){
                resList.add(R.raw.curr_cadence);
                resList.addAll(NumToVoiceList(cadence));
            }
            resList.add(R.raw.at);
            resList.add(zones[zone]);
            play(context, resList, false);
        }
    }

    /**
     * 每公里报
     */
    public static void playPerKm(final Context context, final int km, final int time, final int pace) {
        if (SportSpData.getTtsEnable(context)) {
            ArrayList<Integer> resList = new ArrayList<>();
            resList.add(R.raw.tip);
            resList.add(R.raw.run);
            resList.addAll(NumToVoiceList(km));
            resList.add(R.raw.km);
            resList.add(R.raw.duration);
            resList.addAll(NumToTimeVoice(time));
            resList.add(R.raw.last_km);
            resList.addAll(NumToTimeVoice(pace));
            resList.add(rawComeont[new Random().nextInt(2)]);
            play(context, resList, false);
        }
    }

//    /**
//     * 播报当前心率
//     *
//     * @param context
//     * @param heartbeat
//     */
//    public static void playRunHeartbeat(final Context context, final int heartbeat, final int mCurrCadence) {
//        if (SportSpData.getTtsEnable(context)) {
//            ArrayList<Integer> resList = new ArrayList<>();
//            resList.add(R.raw.heart_rate);
//            resList.addAll(NumToVoiceList(heartbeat));
//            if (mCurrCadence != 0) {
//                resList.add(R.raw.stridefrequency);
//                resList.addAll(NumToVoiceList(mCurrCadence));
//            }
//            play(context, resList, false);
//        }
//    }

//    /**
//     * 你有新记录
//     */
//    public static void playNewRecord(final Context context) {
//        if (SportSpData.getTtsEnable(context)) {
//            ArrayList<Integer> resList = new ArrayList<>();
//            resList.add(R.raw.new_record);
//            play(context, resList, false);
//        }
//    }

    /**
     * 播放
     *
     * @param context
     * @param resList
     */
    private static void play(final Context context, final ArrayList<Integer> resList, final boolean isChongtu) {
        Intent intent = new Intent(context, VoiceService.class);
        intent.putExtra("resList", resList);
        intent.putExtra("isChongtu", isChongtu);
        context.startService(intent);
    }


    /**
     * 将数字转换为声音数字播报文件
     *
     * @param num
     * @return
     */
    private static ArrayList<Integer> NumToVoiceList(int num) {
        ArrayList<Integer> rawRes = new ArrayList<>();
        List<NumVoice> voiceList = new ArrayList<>();

        for (int count = 0; num != 0; count++) {
            int yu = num % 10;
            if (yu != 0) {
                NumVoice numVoice = new NumVoice(count, yu);
                voiceList.add(numVoice);
            }
            num = num / 10;
        }

        int key = voiceList.size();
        for (int i = key - 1; i > -1; i--) {
            NumVoice numVoice = voiceList.get(i);
            if ((key - numVoice.key) > 1) {
                rawRes.add(rawNum[0]);
            }
            rawRes.add(rawNum[numVoice.value]);
            if (numVoice.key > 0) {
                rawRes.add(rawUnit[numVoice.key - 1]);
            }
            key = numVoice.key;
        }

        return rawRes;
    }

    public static ArrayList<Integer> NumToTimeVoice(int secNum) {
        ArrayList<Integer> rawRes = new ArrayList<>();
        int hour = secNum / 3600;
        int min = (secNum % 3600) / 60;
        int sec = secNum % 60;

        if (hour > 0) {
            rawRes.addAll(NumToVoiceList(hour));
            rawRes.add(R.raw.hour);
        }
        if (min > 0) {
            rawRes.addAll(NumToVoiceList(min));
            rawRes.add(R.raw.minute);
        }
        if (sec != 0) {
            rawRes.addAll(NumToVoiceList(sec));
            rawRes.add(R.raw.second);
        }

        return rawRes;
    }

    static class NumVoice {
        int key;
        int value;

        public NumVoice(int key, int value) {
            super();
            this.key = key;
            this.value = value;
        }
    }
}
