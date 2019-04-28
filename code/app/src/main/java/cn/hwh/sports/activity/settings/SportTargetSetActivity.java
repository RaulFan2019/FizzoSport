package cn.hwh.sports.activity.settings;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.adapter.SportTargetSetAdapter;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.entity.adapter.SportTargetSetAE;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.SportTargetSetRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.utils.T;

/**
 * Created by Administrator on 2016/11/21.
 */

public class SportTargetSetActivity extends BaseActivity {
    /* contains */
    private static final int MSG_POST_ERROR = 0x00;
    private static final int MSG_POST_OK = 0x01;
    private static final int MSG_POST_SET_OK = 0x02;

    /* view */
    @BindView(R.id.rv_sport_target_set)
    RecyclerView mSportTargetRv;

    private SportTargetSetAdapter mSportAdapter;
    private DialogBuilder mDialog;
    /* data */
    private List<SportTargetSetAE> mDaysList;
    private int[] mDaysImg = {R.drawable.ic_week_target_1, R.drawable.ic_week_target_2, R.drawable.ic_week_target_3,
            R.drawable.ic_week_target_4, R.drawable.ic_week_target_5, R.drawable.ic_week_target_6, R.drawable.ic_week_target_7};

    private boolean mPostEnable;
    private Callback.Cancelable mPostCancelable;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_sport_target_set;
    }

    Handler mPostHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mPostEnable = true;
            mDialog.dismiss();
            switch (msg.what) {
                case MSG_POST_ERROR:
                    T.showShort(SportTargetSetActivity.this, msg.obj.toString());
                    break;
                case MSG_POST_OK:
                    updateView(msg.obj.toString());
                    break;
                case MSG_POST_SET_OK:
                    int pos = msg.arg1 - 1;
                    for (int i = 0; i < 7; i++) {
                        mDaysList.get(i).mIsSelected = (i == pos);
                    }
                    mSportAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @OnClick({R.id.btn_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }

    @Override
    protected void initData() {
        mPostEnable = true;
        mDaysList = new ArrayList<SportTargetSetAE>();
        mDialog = new DialogBuilder();
        getWeekTargetInfo();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void causeGC() {
        if(mPostCancelable != null && !mPostCancelable.isCancelled()){
            mPostCancelable.cancel();
        }
        if(mPostHandler!= null){
            mPostHandler.removeMessages(MSG_POST_SET_OK);
            mPostHandler.removeMessages(MSG_POST_ERROR);
            mPostHandler.removeMessages(MSG_POST_OK);
        }
        mDialog.dismiss();
    }

    /**
     * 更新页面
     *
     * @param entity
     */
    private void updateView(String entity) {
        SportTargetSetRE targetSetRE = JSON.parseObject(entity, SportTargetSetRE.class);
        for (int i = 1; i <= 7; i++) {
            SportTargetSetAE targetSetAE = new SportTargetSetAE(i + "天", mDaysImg[i - 1], false);
            if (i == targetSetRE.getExercise_days_target().getExercise_days()) {
                targetSetAE.mIsSelected = true;
            }
            mDaysList.add(targetSetAE);
        }
        mSportAdapter = new SportTargetSetAdapter(this, mDaysList);
        mSportAdapter.setOnItemListener(new SportTargetSetAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                setWeekTargetInfo(position + 1);

            }
        });
        mSportTargetRv.setLayoutManager(new LinearLayoutManager(this));
        mSportTargetRv.setAdapter(mSportAdapter);
    }

    /**
     * 获取每周目标
     */
    private void getWeekTargetInfo() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                int searchId = LocalApplication.getInstance().mLoginUser.userId;
                RequestParams params = RequestParamsBuilder.buildGetWeeklyExerciseTargetRP(SportTargetSetActivity.this, UrlConfig.URL_GET_WEEKLY_EXERCISE_TARGET, searchId);
                mPostCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (mPostHandler != null) {
                            if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                                Message msg = new Message();
                                msg.what = MSG_POST_OK;
                                msg.obj = result.result;
                                mPostHandler.sendMessage(msg);
                            } else {
                                Message msg = new Message();
                                msg.what = MSG_POST_ERROR;
                                msg.obj = result.errormsg;
                                mPostHandler.sendMessage(msg);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        if (mPostHandler != null) {
                            Message msg = new Message();
                            msg.what = MSG_POST_ERROR;
                            msg.obj = HttpExceptionHelper.getErrorMsg(ex);
                            mPostHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }
        });

        mDialog.showProgressDialog(SportTargetSetActivity.this,"加载中...",true);
        mDialog.setListener(new DialogBuilder.ProgressDialogListener() {
            @Override
            public void onCancel() {
                if(mPostCancelable!= null && !mPostCancelable.isCancelled()){
                    mPostCancelable.cancel();
                }
                finish();
            }
        });
    }

    /**
     * 设置每周锻炼目标
     *
     * @param days
     */
    private void setWeekTargetInfo(final int days) {

        if(!mPostEnable){
            return;
        }
        mPostEnable = false;

        x.task().post(new Runnable() {
            @Override
            public void run() {
                int lUpdateId = LocalApplication.getInstance().mLoginUser.userId;
                RequestParams params = new RequestParamsBuilder().buildUpdateWeeklyExerciseTargetRP(SportTargetSetActivity.this, UrlConfig.URL_UPDATE_WEEKLY_EXERCISE_TARGET, lUpdateId, days);
                mPostCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (mPostHandler != null) {
                            if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                                Message msg = new Message();
                                msg.what = MSG_POST_SET_OK;
                                msg.arg1 = days;
                                mPostHandler.sendMessage(msg);
                            } else {
                                Message msg = new Message();
                                msg.what = MSG_POST_ERROR;
                                msg.obj = result.errormsg;
                                mPostHandler.sendMessage(msg);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        if (mPostHandler != null) {
                            Message msg = new Message();
                            msg.what = MSG_POST_ERROR;
                            msg.obj = HttpExceptionHelper.getErrorMsg(ex);
                            mPostHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        mPostEnable = true;
                        mDialog.dismiss();
                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }
        });

        mDialog .showProgressDialog(SportTargetSetActivity.this,"设置中...",false);
    }
}
