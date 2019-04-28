package cn.hwh.sports.activity.login;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.byl.imageselector.utils.FileUtils;
import com.nostra13.universalimageloader.utils.ImageSizeUtils;

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
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.activity.settings.MultiImageSelectorActivity;
import cn.hwh.sports.config.AppEnum;
import cn.hwh.sports.config.FileConfig;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.UserDBData;
import cn.hwh.sports.data.sp.UserSPData;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.model.MELocation;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.UploadAvatarRE;
import cn.hwh.sports.entity.net.UserInfoRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.common.CircularImage;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.utils.FileU;
import cn.hwh.sports.utils.ImageU;
import cn.hwh.sports.utils.T;
import cn.hwh.sports.utils.TimeU;
import cn.hwh.sports.utils.XmlParserHandler;

/**
 * Created by Administrator on 2016/11/15.
 */

public class RegisterUserInfoActivity extends BaseActivity {

    /* contains */
    private static final String TAG = "RegisterUserInfoActivity";

    private static final int REQUEST_CHANGE_IMAGE = 0x01;
    private static final int REQUEST_CUT_IMAGE = 0x02;

    private static final int MSG_POST_ERROR = 0x00;
    private static final int MSG_POST_OK = 0x01;

    /**
     * local view
     **/
    @BindView(R.id.civ_avatar)
    CircularImage mAvatarCiv;   //头像
    @BindView(R.id.iv_bg_nickname)
    ImageView mNickNameBgIv;    //昵称
    @BindView(R.id.et_nickname)
    EditText mNickNameEt;       //昵称输入框
    @BindView(R.id.tv_error_nickname)
    TextView mErrorNickNameTv;//昵称错误提示
    @BindView(R.id.tv_gender)
    TextView mGenderTv;         //性别
    @BindView(R.id.tv_location)
    TextView mLocationTv;       //地址
    @BindView(R.id.tv_birthday)
    TextView mBirthdayTv;       //生日
    @BindView(R.id.tv_height)
    TextView mHeightTv;         //身高
    @BindView(R.id.tv_weight)
    TextView mWeightTv;         //体重

    private OptionsPickerView mGenderPv;    //性别
    private OptionsPickerView mLocationPv;  //地址
    private OptionsPickerView mHeightPv;    //身高
    private OptionsPickerView mWeightPv;    //体重
    private TimePickerView mBirthdayPv;     //生日

    private DialogBuilder mDialog;

    /**
     * local data
     **/
    private String mPhoneNum;
    private String mPsw;
    private String mProvince = "上海";
    private String mCity = "浦东新区";
    private String mNickName = "";
    private String mBirthday = "1991-01-01";
    private int mHeight = 170;
    private float mWeight = 50;
    private int mGender = AppEnum.UserGender.MAN;
    private File mAvatarF;//头像文件
    private String mAvatar = "";

    private Callback.Cancelable mPostCancelable;
    private boolean mPostEnable;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_register_user_info;
    }

    Handler mPostHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mPostEnable = true;
            switch (msg.what) {
                case MSG_POST_OK:
                    mDialog.dismiss();
                    preLogin(msg.obj.toString());

                    break;
                case MSG_POST_ERROR:
                    mDialog.dismiss();
                    T.showShort(RegisterUserInfoActivity.this, msg.obj.toString());
                    break;
            }
        }
    };

    @OnClick({R.id.btn_back, R.id.btn_register_commit, R.id.rl_register_user_info, R.id.civ_avatar,
            R.id.iv_bg_gender, R.id.iv_bg_location, R.id.iv_bg_birthday, R.id.iv_bg_height, R.id.iv_bg_weight})
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
        switch (v.getId()) {
            //返回
            case R.id.btn_back:
                finish();
                break;
            //注册提交
            case R.id.btn_register_commit:
                onCompleteClick();
                break;
            //头像
            case R.id.civ_avatar:
                changePhoto();
                break;
            //性别
            case R.id.iv_bg_gender:
                showGanderPv();
                break;
            //地址
            case R.id.iv_bg_location:
                showLocationPick();
                break;
            //生日
            case R.id.iv_bg_birthday:
                changeBirthday();
                break;
            //身高
            case R.id.iv_bg_height:
                changeHeight();
                break;
            //体重
            case R.id.iv_bg_weight:
                changeWeight();
                break;
            case R.id.rl_register_user_info:
                //点击页面回收输入法，已写成switch全局响应
                break;
        }
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
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void initData() {
        mCheckNewData = false;
        mPostEnable = true;
        mPhoneNum = getIntent().getStringExtra("phone");
        mPsw = getIntent().getStringExtra("password");
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        //性别
        mGenderPv = new OptionsPickerView(RegisterUserInfoActivity.this);
        mGenderTv.setText(R.string.value_man);
        //地址
        mLocationPv = new OptionsPickerView(RegisterUserInfoActivity.this);
        mLocationTv.setText(mProvince + " " + mCity);
        //生日
        Calendar calendar = Calendar.getInstance();
        int currYear = calendar.get(Calendar.YEAR);
        mBirthdayPv = new TimePickerView(RegisterUserInfoActivity.this, TimePickerView.Type.YEAR_MONTH_DAY, 1950, currYear);
        mBirthdayPv.setTitle("选择生日");
        mBirthdayTv.setText(mBirthday);
        //身高
        mHeightPv = new OptionsPickerView(RegisterUserInfoActivity.this);
        mHeightTv.setText(mHeight + "");
        //体重
        mWeightPv = new OptionsPickerView(RegisterUserInfoActivity.this);
        mWeightTv.setText(mWeight + "");

        mNickNameEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                TransitionDrawable transitionDrawable = (TransitionDrawable) mNickNameBgIv.getDrawable();
                if (hasFocus) {
                    transitionDrawable.startTransition(200);
                } else {
                    transitionDrawable.reverseTransition(200);
                }
            }
        });

        mDialog = new DialogBuilder();
    }

    @Override
    protected void doMyCreate() {
        ActivityStackManager.getAppManager().addTempActivity(this);
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
        mDialog.dismiss();
        ActivityStackManager.getAppManager().finishTempActivity(this);
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
        int options1Pos = mGender - 1;

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
                mGender = options1 + 1;
                mGenderTv.setText(options1Items.get(options1));
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
        final String province = mProvince;
        final String city = mCity;
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
                mProvince = options1Items.get(options1).getName();
                mCity = options2Items.get(options1).get(option2);
                mLocationTv.setText(mProvince + " " + mCity);
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
        } finally {

        }
        return handler;
    }

    //设置出生年月
    private void changeBirthday() {
        //设置时间选择器的默认参数
        mBirthdayPv.setTime(TimeU.formatStrToDate(mBirthday, TimeU.FORMAT_TYPE_3));
        //设置选择器是否可以循环滚动
        mBirthdayPv.setCyclic(false);
        //设置选择时间回调
        mBirthdayPv.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                //开始向服务器发送更新请求
                mBirthday = TimeU.formatDateToStr(date, TimeU.FORMAT_TYPE_3);
                mBirthdayTv.setText(mBirthday);
            }
        });
        mBirthdayPv.setCancelable(true);
        mBirthdayPv.show();
    }

    /**
     * 设置身高
     */
    private void changeHeight() {
        int defaultHeight = mHeight;
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
                mHeight = options1Items.get(options1);
                mHeightTv.setText(mHeight + "厘米");
            }
        });
        mHeightPv.show();
    }


    /**
     * 设置体重
     */
    private void changeWeight() {
        int defaultWeight = (int) mWeight;
        int pointWeight = (int) ((mWeight - defaultWeight) * 10);
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
                mWeight = options1Items.get(options1) + (float) options2Items.get(options1).get(option2) / 10;
                mWeightTv.setText(mWeight + "公斤");
            }
        });
        mWeightPv.show();
    }


    /**
     * 点击完成按钮
     */
    private void onCompleteClick() {
        if (mAvatarF == null) {
            T.showShort(RegisterUserInfoActivity.this, "请选择一张头像");
            return;
        }
        mNickName = mNickNameEt.getText().toString();
        if (mNickName.isEmpty()) {
            mErrorNickNameTv.setText("请输入昵称");
            return;
        }

        if (!mPostEnable) {
            return;
        }
        mPostEnable = false;

        mDialog.showProgressDialog(RegisterUserInfoActivity.this, "请稍后...", true);
        mDialog.setListener(new DialogBuilder.ProgressDialogListener() {
            @Override
            public void onCancel() {
                if (mPostCancelable != null) {
                    mPostCancelable.cancel();
                }
            }
        });

        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildUploadAvatarRP(RegisterUserInfoActivity.this, UrlConfig.URL_UPLOAD_AVATAR, mAvatarF);
                mPostCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (mPostHandler != null) {
                            if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                                UploadAvatarRE entity = JSON.parseObject(result.result, UploadAvatarRE.class);
                                mAvatar = entity.url;
                                postRegisterInfo();
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
     * 开始注册
     */
    private void postRegisterInfo() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildRegisterRP(RegisterUserInfoActivity.this, UrlConfig.URL_REGISTER, mPhoneNum,
                        mPsw, mNickName, mAvatar, "devicePushId", mWeight, mHeight,
                        mBirthday, mProvince, mCity, mGender);
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
     * 开始登录
     *
     * @param userInfo
     */
    private void preLogin(String userInfo) {
        UserInfoRE entity = JSON.parseObject(userInfo, UserInfoRE.class);
        UserSPData.setUserId(getApplicationContext(), entity.id);
        UserSPData.setUserName(getApplicationContext(), entity.name);
        UserSPData.setHasLogin(getApplicationContext(), true);
        UserSPData.setUserAvatar(getApplicationContext(), entity.avatar);
        UserSPData.setUserNickName(getApplicationContext(), entity.nickname);
        UserSPData.setUserRole(getApplicationContext(), entity.roles.get(entity.roles.size() - 1).role);
        UserSPData.setUserPwd(getApplicationContext(),mPsw);
        UserDE user = new UserDE(entity.id, entity.name, entity.sessionid, entity.weight,
                entity.height, entity.nickname, entity.gender, entity.birthdate, entity.avatar,
                entity.locationprovince, entity.locationcity, entity.maxhr, entity.resthr, entity.hrdevice.macaddr,
                entity.hrdevice.name, entity.vo2max, entity.registerdate, entity.roles.get(entity.roles.size() - 1).role
                , entity.daily_target.stepcount, entity.daily_target.length, entity.daily_target.effort_point,
                entity.daily_target.calorie, entity.daily_target.exercise_minutes, entity.daily_target.sleep_minutes
                , entity.targethrlow, entity.targethrhigh, entity.alerthr,entity.characteristic_target.weight,
                entity.characteristic_target.fatrate,entity.finishedworkout,entity.monthexerciseddays);
        UserDBData.save(user);
        LocalApplication.getInstance().mLoginUser = user;
        Intent intent = new Intent(getApplicationContext(), RegisterJoinStoreActivity.class);
        startActivity(intent);

        ActivityStackManager.getAppManager().finishAllTempActivity();
    }
}
