package cn.hwh.sports.service;

import android.app.IntentService;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import cn.hwh.sports.config.MyBuildConfig;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.sp.AppSPData;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.UpdateRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.utils.DeviceU;
import cn.hwh.sports.utils.Log;

/**
 * Created by Raul.Fan on 2017/4/22.
 */

public class CheckAppVersionIntentService extends IntentService {


    /* contains */
    private static final String TAG = "CheckAppVersionIntentService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public CheckAppVersionIntentService(String name) {
        super(name);
    }

    public CheckAppVersionIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        checkUpdate();
    }

    /**
     * 检查更新
     */
    private void checkUpdate() {
        RequestParams params = new RequestParams(UrlConfig.URL_CHECK_UPDATE);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String re) {
                try {
                    String saveUpdateInfo = AppSPData.getAppUpdateInfo(CheckAppVersionIntentService.this);
                    if (saveUpdateInfo.equals(re)) {
                        return;
                    }
                    UpdateRE mUpdateEntity = JSON.parseObject(re, UpdateRE.class);
                    //需要升级
                    if (mUpdateEntity != null
                            && MyBuildConfig.VersionCode < mUpdateEntity.versionCode) {
                        AppSPData.setAppUpdateInfo(getApplicationContext(), re);
                        AppSPData.setAppUpdateNeedTip(getApplicationContext(), true);
                    } else {
                        AppSPData.setAppUpdateInfo(getApplicationContext(), "");
                    }

                } catch (JSONException exception) {
                    Log.e(TAG,exception.toString());
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
//                Log.v(TAG,"onError:" + HttpExceptionHelper.getErrorMsg(throwable));
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {
            }
        });
    }
}
