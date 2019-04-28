package cn.hwh.sports.activity.settings;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.utils.T;

/**
 * Created by Raul.Fan on 2017/1/12.
 */

public class AdviseUsActivity extends BaseActivity {


    private static final int MSG_ADVISE_ERROR = 0x01;
    private static final int MSG_ADVISE_OK = 0x02;

    @BindView(R.id.et_advise)
    EditText mAdviseEt;

    /* network */
    private Callback.Cancelable mCancelable;

    private DialogBuilder mDialogBuilder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings_advise;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ADVISE_ERROR:
                    mDialogBuilder.mProgressDialog.dismiss();
                    T.showShort(AdviseUsActivity.this, (String) msg.obj);
                    break;
                case MSG_ADVISE_OK:
                    mDialogBuilder.mProgressDialog.dismiss();
                    T.showShort(AdviseUsActivity.this, "提交成功,谢谢您的建议");
                    finish();
                    break;
            }
        }
    };

    @OnClick({R.id.btn_back, R.id.btn_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_commit:
                postCommit();
                break;
        }
    }

    @Override
    protected void initData() {
        mDialogBuilder = new DialogBuilder();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void causeGC() {
        if (mHandler != null) {
            mHandler.removeMessages(MSG_ADVISE_OK);
            mHandler.removeMessages(MSG_ADVISE_ERROR);
        }
        if (mCancelable != null) {
            mCancelable.cancel();
        }
    }

    private void postCommit() {
        final String commitInfo = mAdviseEt.getText().toString();
        if (commitInfo.isEmpty()) {
            T.showShort(AdviseUsActivity.this, "建议信息不能为空");
            return;
        }

        mDialogBuilder.showProgressDialog(AdviseUsActivity.this, "正在提交..", false);
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildPublishAdvise(AdviseUsActivity.this, UrlConfig.URL_PUBLISH_ADVISE, commitInfo);
                mCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mHandler.sendEmptyMessage(MSG_ADVISE_OK);
                        } else {
                            Message msg = new Message();
                            msg.what = MSG_ADVISE_ERROR;
                            msg.obj = result.errormsg;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.what = MSG_ADVISE_ERROR;
                        msg.obj = HttpExceptionHelper.getErrorMsg(ex);
                        mHandler.sendMessage(msg);
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

}
