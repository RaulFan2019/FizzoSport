package cn.hwh.sports.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.data.sp.UserSPData;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.utils.DeviceU;
import cn.hwh.sports.utils.Log;
import cn.hwh.sports.utils.T;

/**
 * This Service is Persistent Service. Do some what you want to do here.<br/>
 * <p/>
 * Created by Mars on 12/24/15.
 */
public class ListenerService extends Service {


    private static final String TAG = "ListenerService";
    Handler myHandler;
    private int timer = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG,"onCreate");
        myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    if(timer % 10 == 0){
                        //如果用户已登录
                        if(LocalApplication.getInstance().getLoginUser(ListenerService.this) != null
                                && UserSPData.hasLogin(ListenerService.this)){
                            //检查上传服务是否活着
                            if (!DeviceU.isWorked(ListenerService.this, "cn.hwh.sports.service.UploadWatcherService")) {
                                Intent uploadServiceIntent = new Intent(ListenerService.this, UploadWatcherService.class);
                                startService(uploadServiceIntent);
                            }
                            //检查是否在运动中
                            WorkoutDE workoutDE  = WorkoutDBData.getUnFinishWorkout(LocalApplication.getInstance().getLoginUser(ListenerService.this).userId);
                            if (workoutDE != null){
                                if (workoutDE.type == SportEnum.EffortType.RUNNING_OUTDOOR){
                                    if (!DeviceU.isWorked(ListenerService.this, "cn.hwh.sports.service.RunningService")) {
                                        Intent effortIntent = new Intent(ListenerService.this, FreeEffortService.class);
                                        startService(effortIntent);
                                    }
                                }else{
                                    if (!DeviceU.isWorked(ListenerService.this, "cn.hwh.sports.service.FreeEffortService")) {
                                        Intent effortIntent = new Intent(ListenerService.this, RunningService.class);
                                        startService(effortIntent);
                                    }
                                }
                            }
                        }
                    }
                    timer++;
                    myHandler.sendEmptyMessageDelayed(1, 6000);
                }
            }
        };
        myHandler.sendEmptyMessage(1);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
