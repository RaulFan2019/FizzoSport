package cn.hwh.sports.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;

import cn.hwh.sports.config.FileConfig;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.utils.FileU;

/**
 * Created by Administrator on 2016/2/15.
 * 发送程序Crash的服务
 */
public class SendCrashLogService extends IntentService {


    private ArrayList<File> crashFiles;

    private static final String TAG = "SendCrashLogService";

    private static final int MSG_UPLOAD_NEXT = 1;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SendCrashLogService(String name) {
        super(name);
    }

    public SendCrashLogService() {
        super(TAG);
    }

    Handler uploadHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (msg.what == MSG_UPLOAD_NEXT) {
                uploadCrash();
            }
        }
    };

    @Override
    protected void onHandleIntent(Intent intent) {
        File crashPath = new File(FileConfig.CRASH_PATH);
        if (crashPath == null) {
            return;
        }
        if(crashPath.listFiles() == null){
            return;
        }
        if (crashPath.listFiles().length == 0) {
            return;
        }
        crashFiles = new ArrayList<File>();
        for (File f : crashPath.listFiles()) {
            crashFiles.add(f);
        }
        uploadCrash();
    }


    private void uploadCrash() {
//        Log.v(TAG,"crashFiles.size:" + crashFiles.size());
        if (crashFiles.size() > 0) {
            postCrashFile(crashFiles.get(0));
        }
        return;
    }

    private void postCrashFile(final File file) {
        String info = FileU.ReadTxtFile(file);
        if (info.equals("")) {
            file.delete();
            crashFiles.remove(0);
            uploadCrash();
            return;
        }
        RequestParams params = RequestParamsBuilder.buildReportCrashRP(SendCrashLogService.this,
                UrlConfig.URL_REPORT_CRASH,info, file.getName());
        x.http().post(params,new Callback.CommonCallback<BaseRE>() {
            @Override
            public void onSuccess(BaseRE reBase) {
//                Log.v(TAG,"onSuccess");
                if (reBase.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                    file.delete();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {
                if (crashFiles.size() > 0) {
                    crashFiles.remove(0);
                }
                uploadHandler.sendEmptyMessageDelayed(MSG_UPLOAD_NEXT, 1000 * 10);
            }
        });

    }
}
