package cn.hwh.sports.activity.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import cn.fizzo.baseutilslib.toast.Toasty;
import cn.hwh.pickviewlibrary.OptionsPickerView;
import cn.hwh.pickviewlibrary.TimePickerView;
import cn.hwh.sports.ActivityStackManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.activity.main.MainActivity;
import cn.hwh.sports.activity.main.MainActivityV2;
import cn.hwh.sports.activity.settings.MultiImageSelectorActivity;
import cn.hwh.sports.activity.settings.UserInfoSettingActivity;
import cn.hwh.sports.config.AppEnum;
import cn.hwh.sports.config.FileConfig;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.UserDBData;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.UploadAvatarRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.common.CircularImage;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.utils.FileU;
import cn.hwh.sports.utils.ImageU;
import cn.hwh.sports.utils.T;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by Raul.Fan on 2017/4/14.
 */

public class LoginPerfectUserActivity extends BaseActivity {

    /* contains */
    private static final int REQUEST_CHANGE_IMAGE = 0x01;
    private static final int REQUEST_CUT_IMAGE = 0x02;

    private static final int MSG_UPLOAD_PHOTO_ERROR = 0x00;
    private static final int MSG_UPLOAD_PHOTO_OK = 0x01;
    private static final int MSG_UPDATE_USER_INFO_ERROR = 0x02;
    private static final int MSG_UPDATE_USER_INFO_OK = 0x03;

    /* local view */
    @BindView(R.id.civ_avatar)
    CircularImage mAvatarCiv;//头像
    @BindView(R.id.et_nickname)
    TextView mNicknameEt;//昵称输入框
    @BindView(R.id.et_gender)
    TextView mGenderEt;//性别输入框
    @BindView(R.id.et_weight)
    TextView mWeightEt;//体重输入框
    @BindView(R.id.et_height)
    TextView mHeightEt;//身高输入框
    @BindView(R.id.et_birthday)
    TextView mBirthdayEt;//生日输入框

    private OptionsPickerView mGenderPv;    //性别
    private OptionsPickerView mHeightPv;    //身高
    private OptionsPickerView mWeightPv;    //体重
    private TimePickerView mBirthdayPv;     //生日

    private DialogBuilder mDialogBuilder;//对话框

    /* local data */
    private UserDE mUserDe;
    private File mAvatarF;//头像文件

    private Callback.Cancelable mCancelable;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_perfect_user;
    }

    @OnClick({R.id.ll_nickname, R.id.ll_gender, R.id.ll_weight, R.id.ll_height,
            R.id.ll_birthday, R.id.civ_avatar, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            //点击头像
            case R.id.civ_avatar:
                changePhoto();
                break;
            //点击昵称
            case R.id.ll_nickname:
                showEditNicknameDialog();
                break;
            //点击性别
            case R.id.ll_gender:
                showGanderPv();
                break;
            //点击体重
            case R.id.ll_weight:
                changeWeight();
                break;
            //点击身高
            case R.id.ll_height:
                changeHeight();
                break;
            //点击生日
            case R.id.ll_birthday:
                changeBirthday();
                break;
            case R.id.btn_next:
                onCompleteClick();
                break;
        }
    }

    Handler mLocalHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //上传用户照片失败
                case MSG_UPLOAD_PHOTO_ERROR:
                    if (mDialogBuilder.mProgressDialog != null){
                        mDialogBuilder.mProgressDialog.dismiss();
                    }
                    Toasty.error(LoginPerfectUserActivity.this,(String)msg.obj).show();
                    break;
                //上传用户头像成功
                case MSG_UPLOAD_PHOTO_OK:
                    postUpdateUserInfo();
                    break;
                //更新用户信息失败
                case MSG_UPDATE_USER_INFO_ERROR:
                    if (mDialogBuilder.mProgressDialog != null){
                        mDialogBuilder.mProgressDialog.dismiss();
                    }
                    Toasty.error(LoginPerfectUserActivity.this,(String)msg.obj).show();
                    break;
                //更新用户信息成功
                case MSG_UPDATE_USER_INFO_OK:
                    if (mDialogBuilder.mProgressDialog != null){
                        mDialogBuilder.mProgressDialog.dismiss();
                    }
                    UserDBData.update(mUserDe);
                    LocalApplication.getInstance().mLoginUser = mUserDe;
                    startActivity(MainActivityV2.class);
                    ActivityStackManager.getAppManager().finishAllTempActivity();
                    break;
            }
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHANGE_IMAGE) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> mSelectList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                ImageU.startPhotoCut(this, Uri.fromFile(new File(mSelectList.get(0))), REQUEST_CUT_IMAGE);
            }
        } else if (requestCode == REQUEST_CUT_IMAGE) {
            if (resultCode == RESULT_OK) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(FileConfig.cutFileUri));
                    Bitmap resultBmp = ImageU.compressBitmap(bitmap, 30);
                    mAvatarF = FileU.saveBigBitmap(resultBmp);
                    mAvatarCiv.setImageBitmap(resultBmp);
                    bitmap.recycle();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void initData() {
        mUserDe = LocalApplication.getInstance().getLoginUser(LoginPerfectUserActivity.this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        //性别
        mGenderPv = new OptionsPickerView(LoginPerfectUserActivity.this);
        if (mUserDe.gender == AppEnum.UserGender.MAN) {
            mGenderEt.setText(R.string.value_man);
        }
        //生日
        Calendar calendar = Calendar.getInstance();
        int currYear = calendar.get(Calendar.YEAR);
        mBirthdayPv = new TimePickerView(LoginPerfectUserActivity.this, TimePickerView.Type.YEAR_MONTH_DAY, 1950, currYear);
        mBirthdayPv.setTitle("选择生日");
        mBirthdayEt.setText(mUserDe.birthday);
        //身高
        mHeightPv = new OptionsPickerView(LoginPerfectUserActivity.this);
        mHeightEt.setText(mUserDe.height +  "cm");
        //体重
        mWeightPv = new OptionsPickerView(LoginPerfectUserActivity.this);
        mWeightEt.setText(mUserDe.weight + "kg");
        //昵称
        mNicknameEt.setText(mUserDe.nickname);
        mDialogBuilder = new DialogBuilder();
        //头像
        ImageU.loadUserImage(mUserDe.avatar, mAvatarCiv);
    }

    @Override
    protected void doMyCreate() {
        ActivityStackManager.getAppManager().addTempActivity(this);
    }

    @Override
    protected void causeGC() {
        ActivityStackManager.getAppManager().finishTempActivity(this);
        if (mCancelable != null) {
            mCancelable.cancel();
        }
    }


    /**
     * 选择图片
     */
    private void changePhoto() {
        Intent intent = new Intent(getApplicationContext(), MultiImageSelectorActivity.class);
        //是否
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
        startActivityForResult(intent, REQUEST_CHANGE_IMAGE);
    }

    /**
     * 显示性别选择器
     */
    private void showGanderPv() {
        final ArrayList<String> options1Items = new ArrayList<>();
        options1Items.add("男");
        options1Items.add("女");
        int options1Pos = mUserDe.gender - 1;

        mGenderPv.setCancelable(true);
        //初始化选择器数据
        mGenderPv.setPicker(options1Items);
        //设置选择器是否无限滚动
        mGenderPv.setCyclic(false);
        //设置选择器标题
        mGenderPv.setTitle("性别设置");
        mGenderPv.setLabels("");
        //设置选择器默认值
        mGenderPv.setSelectOptions(options1Pos);
        //选择器回调
        mGenderPv.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                mUserDe.gender = options1 + 1;
                mGenderEt.setText(options1Items.get(options1));
            }
        });
        mGenderPv.show();
    }

    //设置出生年月
    private void changeBirthday() {
        //设置时间选择器的默认参数
        mBirthdayPv.setTime(TimeU.formatStrToDate(mUserDe.birthday, TimeU.FORMAT_TYPE_3));
        //设置选择器是否可以循环滚动
        mBirthdayPv.setCyclic(false);
        //设置选择时间回调
        mBirthdayPv.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                //开始向服务器发送更新请求
                mUserDe.birthday = TimeU.formatDateToStr(date, TimeU.FORMAT_TYPE_3);
                mBirthdayEt.setText(mUserDe.birthday);
            }
        });
        mBirthdayPv.setCancelable(true);
        mBirthdayPv.show();
    }

    /**
     * 设置身高
     */
    private void changeHeight() {
        int defaultHeight = mUserDe.height;
        final ArrayList<Integer> options1Items = new ArrayList<>();
        //选择器默认位置值
        int options1Pos = 0;
        //插值
        for (int i = 100; i <= 300; i++) {
            options1Items.add(i);
            //若值相同则为默认值
            if (i == defaultHeight) {
                options1Pos = i - 100;
            }
        }

        mHeightPv.setCancelable(true);
        //初始化选择器数据
        mHeightPv.setPicker(options1Items);
        //设置选择器是否无限滚动
        mHeightPv.setCyclic(false);
        //设置选择器标题
        mHeightPv.setTitle("身高设置");

        mHeightPv.setLabels("厘米");
        //设置选择器默认值
        mHeightPv.setSelectOptions(options1Pos);
        //选择器回调
        mHeightPv.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                mUserDe.height = options1Items.get(options1);
                mHeightEt.setText(mUserDe.height + "cm");
            }
        });
        mHeightPv.show();
    }


    /**
     * 设置体重
     */
    private void changeWeight() {
        int defaultWeight = (int) mUserDe.weight;
        int pointWeight = (int) ((mUserDe.weight - defaultWeight) * 10);
        final ArrayList<Integer> options1Items = new ArrayList<Integer>();
        final ArrayList<ArrayList<Integer>> options2Items = new ArrayList<ArrayList<Integer>>();
        //选择器默认位置值
        int options1Pos = 0;
        int options2Pos = 0;
        //插值
        for (int i = 35; i <= 300; i++) {
            options1Items.add(i);
            //若值相同则为默认值
            if (i == defaultWeight) {
                options1Pos = i - 35;
            }
            ArrayList<Integer> items = new ArrayList<>();
            for (int j = 0; j <= 9; j++) {
                items.add(j);
            }
            options2Items.add(items);
        }

        options2Pos = pointWeight;


        mWeightPv.setCancelable(true);
        //初始化选择器数据
        mWeightPv.setPicker(options1Items, options2Items, false);
        //设置选择器是否无限滚动
        mWeightPv.setCyclic(false);
        //设置选择器标题
        mWeightPv.setTitle("体重设置");

        mWeightPv.setLabels(".", "公斤");
        //设置选择器默认值
        mWeightPv.setSelectOptions(options1Pos, options2Pos);
        //选择器回调
        mWeightPv.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                mUserDe.weight = options1Items.get(options1) + (float) options2Items.get(options1).get(option2) / 10;
                mWeightEt.setText(mUserDe.weight + "kg");
            }
        });
        mWeightPv.show();
    }

    /**
     * 点击完成按钮
     */
    private void onCompleteClick() {
        mDialogBuilder.showProgressDialog(LoginPerfectUserActivity.this, "请稍后...", false);
        //没有改变
        if (mAvatarF == null) {
            postUpdateUserInfo();
        } else {
            uploadUserPhoto();
        }
    }


    /**
     * 上传用户头像
     */
    private void uploadUserPhoto() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildUploadAvatarRP(LoginPerfectUserActivity.this,
                        UrlConfig.URL_UPLOAD_AVATAR, mAvatarF);
                mCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            UploadAvatarRE entity = JSON.parseObject(result.result, UploadAvatarRE.class);
                            mUserDe.avatar = entity.url;
                            postUpdateUserInfo();
                        } else {
                            Message msg = new Message();
                            msg.what = MSG_UPLOAD_PHOTO_ERROR;
                            msg.obj = result.errormsg;
                            mLocalHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.what = MSG_UPLOAD_PHOTO_ERROR;
                        msg.obj = HttpExceptionHelper.getErrorMsg(ex);
                        mLocalHandler.sendMessage(msg);

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
     * 更新用户信息
     */
    private void postUpdateUserInfo(){
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildUpdateUserInfoRP(LoginPerfectUserActivity.this
                        , UrlConfig.URL_UPDATE_USER_INFO, mUserDe.avatar, mUserDe.gender,
                        null, null, mUserDe.height, mUserDe.weight, mUserDe.birthday, mUserDe.nickname, 0, 0);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mLocalHandler.sendEmptyMessage(MSG_UPDATE_USER_INFO_OK);
                        } else {
                            Message msg = new Message();
                            msg.what = MSG_UPDATE_USER_INFO_ERROR;
                            msg.obj = result.errormsg;
                            mLocalHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.what = MSG_UPDATE_USER_INFO_ERROR;
                        msg.obj = HttpExceptionHelper.getErrorMsg(ex);
                        mLocalHandler.sendMessage(msg);
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
     * 显示修改用户昵称对话框
     */
    private void showEditNicknameDialog(){
        mDialogBuilder.showEditDialog(LoginPerfectUserActivity.this,"修改昵称","确定",
                InputType.TYPE_CLASS_TEXT,mUserDe.nickname);
        mDialogBuilder.setListener(new DialogBuilder.EditDialogListener() {
            @Override
            public void onConfirmBtnClick(String mInputText) {
                if (mInputText != null && !mInputText.isEmpty()) {
                    mUserDe.nickname = mInputText;
                } else {
                    T.showShort(LoginPerfectUserActivity.this, "昵称不可为空");
                }
            }
        });
    }
}
