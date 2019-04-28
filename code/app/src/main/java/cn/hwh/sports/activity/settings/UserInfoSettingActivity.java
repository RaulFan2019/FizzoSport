package cn.hwh.sports.activity.settings;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hwh.pickviewlibrary.OptionsPickerView;
import cn.hwh.pickviewlibrary.TimePickerView;
import cn.hwh.sports.ActivityStackManager;
import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.activity.main.WelcomeActivity;
import cn.hwh.sports.config.AppEnum;
import cn.hwh.sports.config.FileConfig;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.UploadDBData;
import cn.hwh.sports.data.db.UserDBData;
import cn.hwh.sports.data.sp.UserSPData;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.model.MELocation;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.UserInfoRE;
import cn.hwh.sports.entity.net.UploadAvatarRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.common.CircularImage;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.utils.FileU;
import cn.hwh.sports.utils.ImageU;
import cn.hwh.sports.utils.StringU;
import cn.hwh.sports.utils.T;
import cn.hwh.sports.utils.TimeU;
import cn.hwh.sports.utils.XmlParserHandler;

/**
 * Created by Administrator on 2016/11/23.
 */

public class UserInfoSettingActivity extends BaseActivity {
    /* contains */
    private static final int REQUEST_CHANGE_IMAGE = 0x01;
    private static final int REQUEST_CUT_IMAGE = 0x02;

    private static final int MSG_POST_ERROR = 0x03;//网络错误
    private static final int MSG_POST_OK = 0x04;//更新
    private static final int MSG_GET_USER_INFO_OK = 0x05;

    public static final int EDIT_HR_TARGET_HIGH = 0x01;
    public static final int EDIT_HR_TARGET_LOW = 0x02;
    public static final int EDIT_HR_ALERT = 0x03;

    /* local view */
    @BindView(R.id.civ_user_avatar_set)
    CircularImage mAvatarCiv;//头像
    @BindView(R.id.tv_user_nickname)
    TextView mNicknameTv;//昵称文本
    @BindView(R.id.tv_user_gender)
    TextView mGenderTv;//性别文本
    @BindView(R.id.tv_user_birthday)
    TextView mBirthdayTv;//生日文本

    @BindView(R.id.tv_user_height)
    TextView mHeightTv;//身高文本
    @BindView(R.id.tv_user_weight)
    TextView mWeightTv;//体重文本

    @BindView(R.id.tv_user_rest_hr)
    TextView mRestHrTv;//静息心率文本
    @BindView(R.id.tv_user_max_hr)
    TextView mMaxHrTv;//最高心率文本
    @BindView(R.id.tv_waning_hr)
    TextView mWaningHrTv;
    @BindView(R.id.tv_bar_high)
    TextView mBarHighTv;
    @BindView(R.id.tv_bar_low)
    TextView mBarLowTv;


    OptionsPickerView mGenderPv;    //性别
    OptionsPickerView mLocationPv;  //地址
    OptionsPickerView mHeightPv;    //身高
    OptionsPickerView mWeightPv;    //体重
    TimePickerView mBirthdayPv;     //生日

    DialogBuilder mDialog;
    /* data */
    private File mAvatarF;//头像文件

    private UserDE mLoginUser;

    private Callback.Cancelable mPostCancelable;
    private Callback.Cancelable mGetUserInfoCancel;
    private boolean mPostEnable;

    private DialogBuilder mDialogBuilder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_info_setting;
    }

    Handler mPostHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mPostEnable = true;
            mDialog.dismiss();
            switch (msg.what) {
                case MSG_POST_ERROR:
                    T.showShort(UserInfoSettingActivity.this, msg.obj.toString());
                    break;
                case MSG_POST_OK:
                    UserDBData.save(mLoginUser);
                    refreshView();
                    T.showShort(UserInfoSettingActivity.this, "已设置");
                    break;
                case MSG_GET_USER_INFO_OK:
                    UserInfoRE user = JSON.parseObject(msg.obj.toString(), UserInfoRE.class);
                    updateUserInfo(user);
                    break;
            }
        }
    };

    @OnClick({R.id.btn_back, R.id.ll_set_photo, R.id.rl_user_nickname_set, R.id.rl_user_gender_set,
             R.id.rl_user_birthday_set, R.id.rl_user_height_set,
            R.id.rl_user_weight_set, R.id.rl_user_rest_hr_set, R.id.rl_user_max_hr_set,
            R.id.rl_max_hr, R.id.rl_bar_high, R.id.rl_bar_low, R.id.btn_logout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            //头像设置
            case R.id.ll_set_photo:
                changePhoto();
                break;
            //设置昵称
            case R.id.rl_user_nickname_set:
                showSetNickNameDialog();
                break;
            //设置性别
            case R.id.rl_user_gender_set:
                showGanderPv();
                break;
            //设置生日
            case R.id.rl_user_birthday_set:
                changeBirthday();
                break;
            //设置身高
            case R.id.rl_user_height_set:
                changeHeight();
                break;
            //设置体重
            case R.id.rl_user_weight_set:
                changeWeight();
                break;
            case R.id.rl_user_rest_hr_set:
                showSetRestHrDialog();
                break;
            case R.id.rl_user_max_hr_set:
                showSetMaxHrDialog();
                break;
            case R.id.rl_max_hr:
                showEditHrDialog(EDIT_HR_ALERT);
                break;
            case R.id.rl_bar_high:
                showEditHrDialog(EDIT_HR_TARGET_HIGH);
                break;
            case R.id.rl_bar_low:
                showEditHrDialog(EDIT_HR_TARGET_LOW);
                break;
            //登出
            case R.id.btn_logout:
                showLogoutDialog();
                break;
        }
    }

    @Override
    protected void initData() {
        mLoginUser = LocalApplication.getInstance().getLoginUser(UserInfoSettingActivity.this);
        mDialogBuilder = new DialogBuilder();
        mPostEnable = true;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        //性别
        mGenderPv = new OptionsPickerView(UserInfoSettingActivity.this);
        mGenderTv.setText(R.string.value_man);
        //地址
        mLocationPv = new OptionsPickerView(UserInfoSettingActivity.this);
        //生日
        Calendar calendar = Calendar.getInstance();
        int currYear = calendar.get(Calendar.YEAR);
        mBirthdayPv = new TimePickerView(UserInfoSettingActivity.this, TimePickerView.Type.YEAR_MONTH_DAY, 1950, currYear);
        mBirthdayPv.setTitle("选择生日");
        //身高
        mHeightPv = new OptionsPickerView(UserInfoSettingActivity.this);
        //体重
        mWeightPv = new OptionsPickerView(UserInfoSettingActivity.this);
        mDialog = new DialogBuilder();
        refreshView();
    }


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
                    postUploadAvatar();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void causeGC() {
        if (mPostCancelable != null && !mPostCancelable.isCancelled()) {
            mPostCancelable.cancel();
        }
        if (mPostHandler != null) {
            mPostHandler.removeMessages(MSG_POST_ERROR);
            mPostHandler.removeMessages(MSG_POST_OK);
        }
        if (mGetUserInfoCancel != null) {
            mGetUserInfoCancel.cancel();
        }
        mDialog.dismiss();
    }

    /**
     * 更新页面
     */
    private void refreshView() {
        ImageU.loadUserImage(mLoginUser.avatar, mAvatarCiv);
        mNicknameTv.setText(mLoginUser.nickname);
        if (mLoginUser.gender == AppEnum.UserGender.WOMEN) {
            mGenderTv.setText("女");
        } else {
            mGenderTv.setText("男");
        }
        mBirthdayTv.setText(mLoginUser.birthday);
        mHeightTv.setText(mLoginUser.height + "cm");
        mWeightTv.setText(mLoginUser.weight + "kg");
        updateUserHrView();
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
     * 静息心率弹框
     */
    private void showSetRestHrDialog() {
        mDialog.showEditDialog(this, "静息心率", "保存", InputType.TYPE_CLASS_NUMBER, String.valueOf(mLoginUser.restHr));
        mDialog.setListener(new DialogBuilder.EditDialogListener() {
            @Override
            public void onConfirmBtnClick(String mInputText) {
                if (mInputText != null && !mInputText.isEmpty()) {
                    mLoginUser.restHr = Integer.valueOf(mInputText);
                    postUpdateUserInfo(null, 0, null, null, 0, 0, null, null, 0, mLoginUser.restHr);
                } else {
                    T.showShort(UserInfoSettingActivity.this, "昵称不可为空");
                }
            }
        });
    }

    /**
     * 最大心率弹框
     */
    private void showSetMaxHrDialog() {
        mDialog.showEditDialog(this, "最大心率", "保存", InputType.TYPE_CLASS_NUMBER, String.valueOf(mLoginUser.maxHr));
        mDialog.setListener(new DialogBuilder.EditDialogListener() {
            @Override
            public void onConfirmBtnClick(String mInputText) {
                if (mInputText != null && !mInputText.isEmpty()) {
                    mLoginUser.maxHr = Integer.valueOf(mInputText);
                    postUpdateUserInfo(null, 0, null, null, 0, 0, null, null, mLoginUser.maxHr, 0);
                } else {
                    T.showShort(UserInfoSettingActivity.this, "昵称不可为空");
                }
            }
        });
    }

    /**
     * 昵称弹框
     */
    private void showSetNickNameDialog() {
        mDialog.showEditDialog(this, "昵称", "保存", InputType.TYPE_CLASS_TEXT, mLoginUser.nickname);
        mDialog.setListener(new DialogBuilder.EditDialogListener() {
            @Override
            public void onConfirmBtnClick(String mInputText) {
                if (mInputText != null && !mInputText.isEmpty()) {
                    mLoginUser.nickname = mInputText;
                    postUpdateUserInfo(null, 0, null, null, 0, 0, null, mLoginUser.nickname, 0, 0);
                } else {
                    T.showShort(UserInfoSettingActivity.this, "昵称不可为空");
                }
            }
        });
    }

    /**
     * 显示性别选择器
     */
    private void showGanderPv() {
        final ArrayList<String> options1Items = new ArrayList<>();
        options1Items.add("男");
        options1Items.add("女");
        int options1Pos = mLoginUser.gender > 0 ? mLoginUser.gender - 1 : 0;

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
                mLoginUser.gender = options1 + 1;
                postUpdateUserInfo(null, mLoginUser.gender, null, null, 0, 0, null, null, 0, 0);
            }
        });
        mGenderPv.show();
    }

    /**
     * 显示地区选择器
     */
    private void showLocationPick() {
        //联动选择器1 省
        final ArrayList<MELocation> options1Items = new ArrayList<MELocation>();
        //联动选择器2 市
        final ArrayList<ArrayList<String>> options2Items = new ArrayList<ArrayList<String>>();
        //选择器默认位置值
        int options1Pos = 0;
        int options2Pos = 0;
        //获取默认数据
        final String province = mLoginUser.province;
        final String city = mLoginUser.city;
        //初始化省数据
        if (initLocData() != null) {
            options1Items.addAll(initLocData().getProvinceDataList());
        }
        //初始化市数据
        for (int i = 0; i < options1Items.size(); i++) {
            MELocation pro = options1Items.get(i);
            //插入第二项值
            options2Items.add(pro.getCityList());
            //获取省当前地址位置
            if (pro.getName().equals(province)) {
                options1Pos = i;
                //获取市
                for (int n = 0; n < pro.getCityList().size(); n++) {
                    if (pro.getCityList().get(n).equals(city)) {
                        options2Pos = n;
                        break;
                    }
                }
            }
        }

        mLocationPv.setCancelable(true);
        //初始化选择器数据
        mLocationPv.setPicker(options1Items, options2Items, true);
        //设置选择器是否无限滚动
        mLocationPv.setCyclic(false);
        //设置选择器标题
        mLocationPv.setTitle("选择地址");
        //设置选择器默认值
        mLocationPv.setSelectOptions(options1Pos, options2Pos);
        //选择器回调
        mLocationPv.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                mLoginUser.province = options1Items.get(options1).getName();
                mLoginUser.city = options2Items.get(options1).get(option2);
                postUpdateUserInfo(null, 0, mLoginUser.province, mLoginUser.city, 0, 0, null, null, 0, 0);
            }
        });

        mLocationPv.show();
    }

    /**
     * 初始化XML位置数据
     */
    private XmlParserHandler initLocData() {
        XmlParserHandler handler = new XmlParserHandler();
        AssetManager asset = getResources().getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            parser.parse(input, handler);
            input.close();

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return handler;
    }

    //设置出生年月
    private void changeBirthday() {
        //设置时间选择器的默认参数
        mBirthdayPv.setTime(TimeU.formatStrToDate(mLoginUser.birthday, TimeU.FORMAT_TYPE_3));
        //设置选择器是否可以循环滚动
        mBirthdayPv.setCyclic(false);
        //设置选择时间回调
        mBirthdayPv.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                //开始向服务器发送更新请求
                mLoginUser.birthday = TimeU.formatDateToStr(date, TimeU.FORMAT_TYPE_3);
                postUpdateUserInfo(null, 0, null, null, 0, 0, mLoginUser.birthday, null, 0, 0);
            }
        });
        mBirthdayPv.setCancelable(true);
        mBirthdayPv.show();
    }

    /**
     * 设置身高
     */
    private void changeHeight() {
        int lDefaultHeight = mLoginUser.height;
        final ArrayList<Integer> options1Items = new ArrayList<>();
        //选择器默认位置值
        int options1Pos = 0;
        //插值
        for (int i = 100; i <= 300; i++) {
            options1Items.add(i);
            //若值相同则为默认值
            if (i == lDefaultHeight) {
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
                mLoginUser.height = options1Items.get(options1);
                postUpdateUserInfo(null, 0, null, null, mLoginUser.height, 0, null, null, 0, 0);
            }
        });
        mHeightPv.show();
    }


    /**
     * 设置体重
     */
    private void changeWeight() {
        float lWeight = mLoginUser.weight;
        int lDefaultWeight = (int) lWeight;
        final int pointWeight = (int) ((lWeight - lDefaultWeight) * 10);
        final ArrayList<Integer> options1Items = new ArrayList<Integer>();
        final ArrayList<ArrayList<Integer>> options2Items = new ArrayList<ArrayList<Integer>>();
        //选择器默认位置值
        int options1Pos = 0;
        int options2Pos = 0;
        //插值
        for (int i = 35; i <= 300; i++) {
            options1Items.add(i);
            //若值相同则为默认值
            if (i == lDefaultWeight) {
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
                mLoginUser.weight = options1Items.get(options1) + (float) options2Items.get(options1).get(option2) / 10;
                postUpdateUserInfo(null, 0, null, null, 0, mLoginUser.weight, null, null, 0, 0);
            }
        });
        mWeightPv.show();
    }


    /**
     * 上传图片
     */
    private void postUploadAvatar() {
        mDialog.showProgressDialog(this, "正在上图片...", false);

        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildUploadAvatarRP(UserInfoSettingActivity.this, UrlConfig.URL_UPLOAD_AVATAR, mAvatarF);
                mPostCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (mPostHandler != null) {
                            if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                                UploadAvatarRE entity = JSON.parseObject(result.result, UploadAvatarRE.class);
                                mLoginUser.avatar = entity.url;
                                postUpdateUserInfo(mLoginUser.avatar, 0, null, null, 0, 0, null, null, 0, 0);
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
    private void postUpdateUserInfo(final String avatar, final int gender,
                                    final String province, final String city, final int height, final float weight,
                                    final String birthday, final String nickname, final int maxhr, final int resthr) {
        if (!mPostEnable) {
            return;
        }
        mPostEnable = false;

        x.task().post(new Runnable() {
            @Override
            public void run() {
                final RequestParams requestParams = RequestParamsBuilder.buildUpdateUserInfoRP(UserInfoSettingActivity.this
                        , UrlConfig.URL_UPDATE_USER_INFO, avatar, gender, province, city, height, weight, birthday, nickname, maxhr, resthr);
                mPostCancelable = x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (mPostHandler != null) {
                            if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                                Message msg = new Message();
                                msg.what = MSG_POST_OK;
                                msg.obj = result.result;
                                mPostHandler.sendMessage(msg);
                            } else {
                                Message mPostMsg = new Message();
                                mPostMsg.what = MSG_POST_ERROR;
                                mPostMsg.obj = result.errormsg;
                                mPostHandler.sendMessage(mPostMsg);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message mPostMsg = new Message();
                        mPostMsg.what = MSG_POST_ERROR;
                        mPostMsg.obj = HttpExceptionHelper.getErrorMsg(ex);
                        mPostHandler.sendMessage(mPostMsg);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        mPostEnable = true;
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
    private void getUserInfo() {
        if (!mPostEnable) {
            return;
        }
        mPostEnable = false;

        x.task().post(new Runnable() {
            @Override
            public void run() {
                //开始请求服务器
                RequestParams requestParams = RequestParamsBuilder.buildGetUserInfoRP(UserInfoSettingActivity.this,
                        UrlConfig.URL_GET_USER_INFO, UserSPData.getUserId(LocalApplication.applicationContext));
                mGetUserInfoCancel = x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {

                        if (mPostHandler != null) {

                            if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                                //正确返回数据
                                Message mPostMsg = new Message();
                                mPostMsg.what = MSG_GET_USER_INFO_OK;
                                mPostMsg.obj = result.result;
                                mPostHandler.sendMessage(mPostMsg);
                            } else {
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        if (mPostHandler != null) {
                            //请求失败、网络等问题
                            Message mPostMsg = new Message();
                            mPostMsg.what = MSG_POST_ERROR;
                            mPostMsg.obj = "请求失败";
                            mPostHandler.sendMessage(mPostMsg);
                        }
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        mPostEnable = true;
                    }

                    @Override
                    public void onFinished() {
                        mPostEnable = true;
                    }
                });
            }
        });
    }

    /**
     * 更新用户信息
     */
    private void updateUserInfo(final UserInfoRE entity) {
        entity.sessionid = mLoginUser.sessionId;
        UserDBData.CommonLogin(UserInfoSettingActivity.this,entity);
        mLoginUser = LocalApplication.getInstance().getLoginUser(UserInfoSettingActivity.this);
        refreshView();
    }

    /**
     * 更新用户心率界面
     */
    private void updateUserHrView() {
        mRestHrTv.setText(String.valueOf(mLoginUser.restHr));
        mMaxHrTv.setText(String.valueOf(mLoginUser.maxHr));
        if (mLoginUser.targetHrHigh == -1) {
            mBarHighTv.setText("未设置");
        } else {
            mBarHighTv.setText(mLoginUser.targetHrHigh + "");
        }
        if (mLoginUser.targetHrLow == -1) {
            mBarLowTv.setText("未设置");
        } else {
            mBarLowTv.setText(mLoginUser.targetHrLow + "");
        }
        if (mLoginUser.alertHr == -1) {
            mWaningHrTv.setText("未设置");
        } else {
            mWaningHrTv.setText(mLoginUser.alertHr + "");
        }
    }

    /**
     * 显示编辑对话框
     *
     * @param editType
     */
    private void showEditHrDialog(final int editType) {
//        if (BleManager.getBleManager().mBleConnectE == null
//                || !BleManager.getBleManager().mBleConnectE.mIsConnected) {
//            T.showShort(UserInfoSettingActivity.this, "请连接设备");
//        }
        String title = "";
        String editStr = "";
        int editNum;
        if (editType == EDIT_HR_ALERT) {
            title = "修改报警心率";
            editNum = mLoginUser.alertHr;
        } else if (editType == EDIT_HR_TARGET_HIGH) {
            title = "修改靶心率上限";
            editNum = mLoginUser.targetHrHigh;
        } else {
            title = "修改靶心率下限";
            editNum = mLoginUser.targetHrLow;
        }
        if (editNum == -1) {
            editStr = "";
        } else {
            editStr = editNum + "";
        }

        mDialogBuilder.showEditDialog(UserInfoSettingActivity.this, title, "修改", InputType.TYPE_CLASS_NUMBER, editStr);
        mDialogBuilder.setListener(new DialogBuilder.EditDialogListener() {
            @Override
            public void onConfirmBtnClick(String mInputText) {
                String error = StringU.checkInputHrError(UserInfoSettingActivity.this, mInputText, editType);
                if (error.equals(AppEnum.DEFAULT_CHECK_ERROR)) {
                    int tempHr = Integer.valueOf(mInputText);
                    //若输入的是最低靶心率，且大于上限
                    if (editType == EDIT_HR_TARGET_HIGH && tempHr < mLoginUser.targetHrLow) {
                        T.showShort(UserInfoSettingActivity.this, "靶心率下限不能大于靶心率上限");
                        return;
                    } else if (editType == EDIT_HR_TARGET_LOW && tempHr > mLoginUser.targetHrHigh) {
                        T.showShort(UserInfoSettingActivity.this, "靶心率下限不能大于靶心率上限");
                        return;
                    }
                    if (editType == EDIT_HR_TARGET_HIGH) {
                        mLoginUser.targetHrHigh = Integer.valueOf(mInputText);
                    } else if (editType == EDIT_HR_TARGET_LOW) {
                        mLoginUser.targetHrLow = Integer.valueOf(mInputText);
                    } else {
                        mLoginUser.alertHr = Integer.valueOf(mInputText);
                    }
                    UserDBData.update(mLoginUser);
                    LocalApplication.getInstance().mLoginUser = mLoginUser;
                    UploadDBData.updateUserHr(mLoginUser.userId,mLoginUser.targetHrLow,mLoginUser.targetHrHigh,mLoginUser.alertHr);
                    updateUserHrView();
                    if (BleManager.getBleManager().mBleConnectE != null
                            && BleManager.getBleManager().mBleConnectE.mIsConnected){
                        BleManager.getBleManager().mBleConnectE.writeUserHr();
                    }

                } else {
                    T.showShort(UserInfoSettingActivity.this, error);
                }
            }
        });
    }

    /**
     * 显示登出对话框
     */
    private void showLogoutDialog(){
        mDialogBuilder.showChoiceDialog(UserInfoSettingActivity.this,"登出","是否要退出本账号的登录？","登出");
        mDialogBuilder.setListener(new DialogBuilder.ChoiceDialogListener() {
            @Override
            public void onConfirmBtnClick() {
                logout();
            }
        });
    }


    /**
     * 登出
     */
    private void logout() {
        //断开之前的连接
        if (BleManager.getBleManager().mBleConnectE != null) {
            BleManager.getBleManager().mBleConnectE.disConnect();
            BleManager.getBleManager().mBleConnectE = null;
        }
        BleManager.getBleManager().replaceConnect("");
        BleManager.getBleManager().destroy();
        LocalApplication.getInstance().mLoginUser = null;
        UserSPData.setUserId(UserInfoSettingActivity.this,AppEnum.DEFAULT_USER_ID);
        UserSPData.setHasLogin(UserInfoSettingActivity.this, false);
        UserSPData.setUserRole(UserInfoSettingActivity.this, AppEnum.UserRoles.NO_ROLE);
        UserSPData.setUserPage(UserInfoSettingActivity.this, AppEnum.UserRoles.NO_ROLE);


        ActivityStackManager.getAppManager().finishAllActivity();
    }

}
