package cn.hwh.sports.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hwh.sports.ActivityStackManager;
import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.settings.AboutUsActivity;
import cn.hwh.sports.activity.settings.AdviseUsActivity;
import cn.hwh.sports.activity.settings.BleAutoBindActivity;
import cn.hwh.sports.activity.settings.FizzoDeviceSettingActivity;
import cn.hwh.sports.activity.settings.UserInfoSettingActivity;
import cn.hwh.sports.activity.settings.WorkOutSetActivity;
import cn.hwh.sports.data.sp.UserSPData;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.fragment.BaseFragment;
import cn.hwh.sports.ui.common.CircularImage;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.utils.ImageU;
import cn.hwh.sports.utils.T;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by Raul.Fan on 2016/11/11.
 */

public class MainUserFragment extends BaseFragment {


    /* view */
    @BindView(R.id.civ_user_avatar)
    CircularImage mUserAvatarCiv;       //用户头像
    @BindView(R.id.tv_user_nickname)
    TextView mUserNickNameTv;           //用户昵称
    @BindView(R.id.tv_user_join_time)
    TextView mUserJoinTimeTv;           //用户加入时间
    @BindView(R.id.tv_add_device_tip)
    TextView mAddDeviceTipTv;           //添加设备提示
    @BindView(R.id.iv_device_img)
    ImageView mDeviceIconIv;            //设备图片
    @BindView(R.id.ll_device_info)
    LinearLayout mDeviceInfo;           //设备部分信息（用于控制显示or隐藏）
    @BindView(R.id.tv_device_name)
    TextView mDeviceNameTv;            //设备名称
    @BindView(R.id.tv_device_syn_time)
    TextView mDeviceSynTimeTv;          //设备上次同步时间
    @BindView(R.id.iv_device_arrow)
    View mDeviceArrowV;

    /* data */
    private UserDE mLoginUser;

    private DialogBuilder mDialogBuilder;

    /* 构造函数 */
    public static MainUserFragment newInstance() {
        MainUserFragment fragment = new MainUserFragment();
        return fragment;
    }

    @OnClick({R.id.rl_user_info_set, R.id.rl_user_device_set,
            R.id.rl_user_sport_record_set, R.id.rl_about_us, R.id.rl_advice})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_user_info_set:
                startActivity(UserInfoSettingActivity.class);
                break;
            case R.id.rl_user_device_set:
                onDeviceRlClick();
                break;
            //锻炼记录设置
            case R.id.rl_user_sport_record_set:
                startActivity(WorkOutSetActivity.class);
                break;
            //关于我们
            case R.id.rl_about_us:
                startActivity(AboutUsActivity.class);
                break;
            //意见反馈
            case R.id.rl_advice:
                startActivity(AdviseUsActivity.class);
                break;
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_user;
    }


    @Override
    protected void initParams() {
        mDialogBuilder = new DialogBuilder();
    }

    @Override
    protected void causeGC() {

    }

    @Override
    protected void onVisible() {
        initData();
        initView();
    }

    @Override
    protected void onInVisible() {

    }

    private void initData() {
        mLoginUser = LocalApplication.getInstance().mLoginUser;
    }

    private void initView() {
        //用户头像
        ImageU.loadUserImage(mLoginUser.avatar, mUserAvatarCiv);
        mUserNickNameTv.setText(mLoginUser.nickname);
        if (mLoginUser.registerDate != null) {
            mUserJoinTimeTv.setText("加入时间 " + TimeU.formatDateStrShow(mLoginUser.registerDate,
                    TimeU.FORMAT_TYPE_1, TimeU.FORMAT_TYPE_3));
        }
        if (mLoginUser.bleMac != null && !mLoginUser.bleMac.isEmpty()) {
            mAddDeviceTipTv.setVisibility(View.GONE);
            mDeviceInfo.setVisibility(View.VISIBLE);
            mDeviceIconIv.setVisibility(View.VISIBLE);
            mDeviceNameTv.setText(mLoginUser.bleName);
            String syncTime = UserSPData.getLastSyncTime(getActivity());
            if (!syncTime.equals("")) {
                mDeviceSynTimeTv.setVisibility(View.VISIBLE);
                mDeviceSynTimeTv.setText("已于 " + TimeU.syncTime(UserSPData.getLastSyncTime(getActivity())) + " 同步");
            } else {
                mDeviceSynTimeTv.setVisibility(View.GONE);
            }
        } else {
            mDeviceArrowV.setVisibility(View.GONE);
            mAddDeviceTipTv.setVisibility(View.VISIBLE);
            mDeviceIconIv.setVisibility(View.GONE);
            mDeviceInfo.setVisibility(View.GONE);
            mDeviceSynTimeTv.setVisibility(View.GONE);
        }
    }

    //FE:1C:5E:B9:95:50

    /**
     * 点击设备设置
     */
    private void onDeviceRlClick() {
        //若设备为空，进入扫描绑定页面
        if (mLoginUser.bleMac != null && !mLoginUser.bleMac.isEmpty()) {
            startActivity(FizzoDeviceSettingActivity.class);
        } else {
            startActivity(BleAutoBindActivity.class);
        }
    }
}
