package cn.hwh.sports.activity.student;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.activity.main.WelcomeActivity;
import cn.hwh.sports.adapter.TeachSelectStuAdapter;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.UserDBData;
import cn.hwh.sports.data.sp.UserSPData;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.MoverListRE;
import cn.hwh.sports.entity.net.UserInfoRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.ui.common.MyLoadingView;
import cn.hwh.sports.utils.T;

/**
 * Created by Raul.Fan on 2016/11/30.
 * 私教选择学生列表
 */

public class TeachSelectStudentListActivity extends BaseActivity {

    /* contains */
    private static final int MSG_UPDATE_VIEW = 0;// 更新listView
    private static final int MSG_GET_USER_OK = 1;
    private static final int MSG_GET_USER_ERROR = 2;//
    private static final int MSG_GET_USER_LIST_ERROR = 3;

    /* view */
    @BindView(R.id.et_search)
    EditText mSearchEt;
    @BindView(R.id.lv_stu)
    ListView mStuLv;
    @BindView(R.id.v_loading)
    MyLoadingView mLoadingView;

    private DialogBuilder mDialogBuilder;
    private Callback.Cancelable mCancelable;

    private TeachSelectStuAdapter mAdapter;
    private boolean mPostAble = true;//是否可以发送请求
    private List<MoverListRE.MoversEntity> mMoversEntities = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_teach_select_stu;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 若是更新列表请求
            if (msg.what == MSG_UPDATE_VIEW) {
                mAdapter.notifyDataSetChanged();
                if (mMoversEntities.size() == 0) {
                    T.showShort(TeachSelectStudentListActivity.this, "未搜索到学员");
                }
                mLoadingView.loadFinish();
                mPostAble = true;
            } else if (msg.what == MSG_GET_USER_OK) {
                if (mDialogBuilder.mProgressDialog != null) {
                    mDialogBuilder.mProgressDialog.dismiss();
                }
                saveMoverInfo((String) msg.obj);
            } else if (msg.what == MSG_GET_USER_ERROR) {
                T.showShort(TeachSelectStudentListActivity.this, (String) msg.obj);
                if (mDialogBuilder.mProgressDialog != null) {
                    mDialogBuilder.mProgressDialog.dismiss();
                }
            }else if (msg.what == MSG_GET_USER_LIST_ERROR){
                mPostAble = true;
                mLoadingView.LoadError((String) msg.obj);
                T.showShort(TeachSelectStudentListActivity.this, (String) msg.obj);
            }
        }
    };

    @OnClick(R.id.btn_back)
    public void onClick(View view) {
        if (view.getId() == R.id.btn_back) {
            finish();
        }
    }

    @Override
    protected void initData() {
        mDialogBuilder = new DialogBuilder();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        //键盘不自动弹出
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mAdapter = new TeachSelectStuAdapter(TeachSelectStudentListActivity.this, mMoversEntities);
        mStuLv.setAdapter(mAdapter);
        //列表点击时间
        mStuLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int ownerId = mMoversEntities.get(i).id;
//                UserDE mover = UserDBData.getUserById(mMoversEntities.get(i).id);
                getUserInfoById(ownerId);
//                if (mover == null) {
//
//                } else {
//                    startConnectMoverDevice(mover);
//                }
            }
        });
        //输入框的监听事件
        mSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mPostAble) {
                    SearchFriends(mSearchEt.getText().toString(), false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void doMyCreate() {
        SearchFriends("", false);
    }

    @Override
    protected void causeGC() {
        mMoversEntities.clear();
        if (mHandler != null) {
            mHandler.removeMessages(MSG_UPDATE_VIEW);
        }
        if (mCancelable != null) {
            mCancelable.cancel();
        }
    }

    /**
     * 根据用户输入搜索好友
     *
     * @param searchStr
     */
    private void SearchFriends(final String searchStr, final boolean hasTip) {
        mPostAble = false;
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.BuildSearchMoverParams(TeachSelectStudentListActivity.this,
                        UrlConfig.URL_LIST_MOVER,
                        LocalApplication.getInstance().getLoginUser(TeachSelectStudentListActivity.this).userId, searchStr);
                mCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        mMoversEntities.clear();
                        if (BaseResponseParser.ERROR_CODE_NONE == result.errorcode) {
                            MoverListRE re = JSON.parseObject(result.result, MoverListRE.class);
                            if (re.movers != null && re.movers.size() > 0) {
                                mMoversEntities.addAll(re.movers);
                            }
                            mHandler.sendEmptyMessage(MSG_UPDATE_VIEW);
                        }else {
                            Message message = new Message();
                            message.what = MSG_GET_USER_LIST_ERROR;
                            message.obj = result.errormsg;
                            mHandler.sendMessage(message);
                        }

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message message = new Message();
                        message.what = MSG_GET_USER_LIST_ERROR;
                        message.obj = HttpExceptionHelper.getErrorMsg(ex);
                        mHandler.sendMessage(message);
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
    }

    /**
     * 获取用户信息
     */
    private void getUserInfoById(final int userId) {
        mDialogBuilder.showProgressDialog(TeachSelectStudentListActivity.this, "正在获取学员信息", true);
        mDialogBuilder.setListener(new DialogBuilder.ProgressDialogListener() {
            @Override
            public void onCancel() {
                if (mCancelable != null){
                    mCancelable.cancel();
                }
                T.showShort(TeachSelectStudentListActivity.this,"取消加载");
            }
        });
        x.task().post(new Runnable() {
            @Override
            public void run() {
                //开始请求服务器
                RequestParams requestParams = RequestParamsBuilder.buildGetUserInfoRP(TeachSelectStudentListActivity.this,
                        UrlConfig.URL_GET_USER_INFO, userId);
                mCancelable = x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            //正确返回数据
                            Message mPostMsg = new Message();
                            mPostMsg.what = MSG_GET_USER_OK;
                            mPostMsg.obj = result.result;
                            mHandler.sendMessage(mPostMsg);
                        } else {
                            Message mPostMsg = new Message();
                            mPostMsg.what = MSG_GET_USER_ERROR;
                            mPostMsg.obj = result.errormsg;
                            mHandler.sendMessage(mPostMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        //请求失败、网络等问题
                        Message mPostMsg = new Message();
                        mPostMsg.what = MSG_GET_USER_ERROR;
                        mPostMsg.obj = "获取学员信息失败，请稍后再试";
                        mHandler.sendMessage(mPostMsg);
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
    }

    private void saveMoverInfo(final String userInfo) {
        UserInfoRE entity = JSON.parseObject(userInfo, UserInfoRE.class);
        UserDE lUserDe = new UserDE(entity.id, entity.name, entity.sessionid, entity.weight,
                entity.height, entity.nickname, entity.gender, entity.birthdate, entity.avatar,
                entity.locationprovince, entity.locationcity, entity.maxhr, entity.resthr, entity.hrdevice.macaddr,
                entity.hrdevice.name, entity.vo2max, entity.registerdate, entity.roles.get(entity.roles.size() - 1).role
                , entity.daily_target.stepcount, entity.daily_target.length,entity.daily_target.effort_point,
                entity.daily_target.calorie, entity.daily_target.exercise_minutes, entity.daily_target.sleep_minutes
                ,entity.targethrlow,entity.targethrhigh,entity.alerthr,entity.characteristic_target.weight,
                entity.characteristic_target.fatrate,entity.finishedworkout,entity.monthexerciseddays);
        UserDBData.saveOrUpdate(lUserDe);
        startConnectMoverDevice(lUserDe);
    }

    /**
     * 启动连接学员设备界面
     *
     * @param lUserDe
     */
    private void startConnectMoverDevice(final UserDE lUserDe) {
        if (lUserDe.bleMac.equals("")) {
            T.showShort(TeachSelectStudentListActivity.this, "学员尚未绑定设备");
            return;
        }
        if (BleManager.getBleManager().mBleConnectE != null) {
            BleManager.getBleManager().mBleConnectE.disConnect();
        }
        Bundle bundle = new Bundle();
        bundle.putInt("userId", lUserDe.userId);
        startActivity(TeachConnectMoverDeviceActivity.class, bundle);
    }
}
