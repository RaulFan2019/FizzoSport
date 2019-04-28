package cn.hwh.sports.activity.test;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.fizzo.baseutilslib.toast.Toasty;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.utils.Log;

/**
 * Created by Raul.Fan on 2017/3/31.
 */

public class WxTestActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "WxTestActivity";

    @BindView(R.id.btn_wx_login)
    Button mWxLoginBtn;
    @BindView(R.id.tv_wx_text)
    TextView mWxInfoTv;

    @Override
    @OnClick({R.id.btn_wx_login, R.id.btn_wx_share_f, R.id.btn_wx_share_pyq})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_wx_login:
                startWxLogin();
                break;
            case R.id.btn_wx_share_f:
                showWxShare(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.btn_wx_share_pyq:
                showWxShare(SHARE_MEDIA.WEIXIN_CIRCLE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        UMShareAPI.get(this).onSaveInstanceState(outState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_wx;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void doMyCreate() {
        boolean isauth = UMShareAPI.get(this).isAuthorize(this, SHARE_MEDIA.WEIXIN);
        //是否授权
        if (isauth) {
            mWxLoginBtn.setText("微信登录（已授权）");
        } else {
            mWxLoginBtn.setText("微信登录");
        }
    }

    @Override
    protected void causeGC() {
        UMShareAPI.get(this).release();
    }

    /**
     * 显示微信分享弹框
     *
     * @param share_media
     */
    private void showWxShare(final SHARE_MEDIA share_media) {
        String[] wxItems = {"纯文本", "纯图片（本地）", "纯图片（网络）", "链接（标题，内容）", "声音（标题，内容）", "视频（标题，内容）"};
        AlertDialog.Builder wxDialog = new AlertDialog.Builder(this);
        wxDialog.setTitle("选择分享类型");
        wxDialog.setItems(wxItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        shareText(share_media,"欢迎使用Fizzo! 这里你会看到直观的数据展示以及更好的运动建议 ");
                        break;
                    case 1:
                        shareImg(share_media);
                        break;
                    case 2:
                        Toasty.normal(WxTestActivity.this,"方法同 纯图片（本地）").show();
                        break;
                    case 3:
                        shareUrl(share_media);
                        break;
                    case 4:
                        Toasty.normal(WxTestActivity.this,"暂无").show();
                        break;
                    case 5:
                        Toasty.normal(WxTestActivity.this,"暂无").show();
                        break;
                }
            }
        }).show();
    }

    /**
     * 分享文字
     * @param share_media
     */
    private void shareText(SHARE_MEDIA share_media , String shareStr){
        new ShareAction(this).setPlatform(share_media)
                .withText(shareStr)
                .setCallback(umShareListener)
                .share();
    }

    /**
     * 分享图片
     * @param share_media
     */
    private void shareImg(SHARE_MEDIA share_media){

        //开始设置微信分享主图片
//        UMImage image = new UMImage(WxTestActivity.this, "imageurl");//网络图片
//        UMImage image = new UMImage(WxTestActivity.this, file);//本地文件
        UMImage image = new UMImage(WxTestActivity.this, R.mipmap.ic_launcher);//资源文件
//        UMImage image = new UMImage(WxTestActivity.this, bitmap);//bitmap文件
//        UMImage image = new UMImage(WxTestActivity.this, byte[]);//字节流
        //设置图片压缩 当分享的图片很大时
 //       image.compressStyle = UMImage.CompressStyle.SCALE;//大小压缩，默认为大小压缩，适合普通很大的图
//        image.compressStyle = UMImage.CompressStyle.QUALITY;//质量压缩，适合长图的分享压缩格式设置：


        //设置微信分享缩略图
        UMImage thumb =  new UMImage(this, R.mipmap.ic_launcher);
        image.setThumb(thumb);
        new ShareAction(this).setPlatform(share_media)
                .withMedia(image)
                .setCallback(umShareListener)
                .share();
    }

    /**
     * 分享网页
     * @param share_media
     */
    private void shareUrl(SHARE_MEDIA share_media){

        UMImage thumb =  new UMImage(this, R.mipmap.ic_launcher);
        //注意链接需要加http:// 等前缀
        UMWeb web = new UMWeb("http://www.fizzo.cn");
        web.setTitle("欢迎进入Fizzo");//标题
        web.setThumb(thumb);  //缩略图
        web.setDescription("这是Fizzo的官方网站，欢迎浏览～");//描述

        new ShareAction(this).setPlatform(share_media)
                .withMedia(web)
                .setCallback(umShareListener)
                .share();
    }

    /**
     * 开始微信授权
     */
    private void startWxLogin() {

        boolean isauth = UMShareAPI.get(this).isAuthorize(this, SHARE_MEDIA.WEIXIN);
        //是否授权
        if (isauth) {
            //若已授权

            UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.WEIXIN, umAuthListener);//获取用户资料
//            UMShareAPI.get(this).deleteOauth(this, SHARE_MEDIA.WEIXIN, umAuthListener);//取消授权
        } else {
            //若未授权

            UMShareAPI.get(this).doOauthVerify(this, SHARE_MEDIA.WEIXIN, umAuthListener);
        }

    }

    /**
     * 友盟登录授权回调
     */
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //授权开始的回调
            Toasty.normal(WxTestActivity.this, "开始授权～").show();
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {

            switch (action) {
                case UMAuthListener.ACTION_AUTHORIZE:
                    Toasty.normal(WxTestActivity.this, "授权成功！").show();
                    mWxLoginBtn.setText("微信登录（已授权）");
                    //获取用户资料
                    UMShareAPI.get(WxTestActivity.this).getPlatformInfo(WxTestActivity.this, SHARE_MEDIA.WEIXIN, umAuthListener);
                    break;
                case UMAuthListener.ACTION_GET_PROFILE:
                    String temp = "";
                    for (String key : data.keySet()) {
                        temp = temp + key + " : " + data.get(key) + "\n";
                    }
                    mWxInfoTv.setText(temp);

                    break;
                case UMAuthListener.ACTION_DELETE:
                    Toasty.normal(WxTestActivity.this, "授权已删除！").show();
                    mWxLoginBtn.setText("微信登录");
                    break;
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toasty.error(WxTestActivity.this, "授权失败！" + " msg:" + t.getMessage()).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toasty.error(WxTestActivity.this, "授权取消！").show();
        }
    };


    /**
     * 友盟分享回调
     */
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //分享开始的回调
        }
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toasty.normal(WxTestActivity.this, platform + " 分享成功").show();

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toasty.error(WxTestActivity.this, platform + " 分享失败").show();
            if(t!=null){
                Log.d("throw","throw:"+t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toasty.error(WxTestActivity.this, platform + " 分享取消").show();
        }
    };
}
