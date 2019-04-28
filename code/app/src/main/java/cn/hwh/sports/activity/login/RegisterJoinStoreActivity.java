package cn.hwh.sports.activity.login;

import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.activity.main.MainActivity;
import cn.hwh.sports.activity.settings.BleAutoBindActivity;
import cn.hwh.sports.adapter.SearchStoreAdapter;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.GetSearchStoreRE;
import cn.hwh.sports.entity.net.StoreRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.utils.T;

/**
 * Created by Administrator on 2016/11/16.
 */

public class RegisterJoinStoreActivity extends BaseActivity {

    /* contains */
    private static final int MSG_POST_ERROR = 0x00;
    private static final int MSG_POST_OK = 0x01;
    private static final int MSG_SEARCH = 0x02;
    private static final int MSG_SEARCH_OK = 0x03;
    private static final int MSG_SEARCH_ERROR = 0x04;

    /* view */
    @BindView(R.id.et_search)
    EditText mSearchEt;
    @BindView(R.id.tv_search_key)
    TextView mSearchKeyTv;
    @BindView(R.id.lv_store)
    ListView mStoreLv;
    @BindView(R.id.ll_none_store)
    LinearLayout mNoneLl;

    private DialogBuilder mDialog;

    /* network */
    private boolean mPostEnable = true;
    private Callback.Cancelable mPostCancelable;

    /* local data */
    private String mSearchKey = "";
    private SearchStoreAdapter mAdapter;

    List<GetSearchStoreRE.StoresEntity> mStoreEntities = new ArrayList<>();//搜索的门店结果

    @Override
    protected int getLayoutId() {
        return R.layout.activity_join_store;
    }

    Handler mPostHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mPostEnable = true;
            switch (msg.what) {
                //请求加入成功
                case MSG_POST_OK:
                    mDialog.dismiss();
                    StoreRE store = JSON.parseObject(msg.obj.toString(), StoreRE.class);
                    T.showShort(RegisterJoinStoreActivity.this, "您已经成功加入<" + store.storename + ">");
                    startActivity(MainActivity.class);
                    startActivity(BleAutoBindActivity.class);
                    finish();
                    break;
                //请求加入失败
                case MSG_POST_ERROR:
                    mDialog.dismiss();
                    T.showShort(RegisterJoinStoreActivity.this, msg.obj.toString());
                    break;
                //开始搜索
                case MSG_SEARCH:
                    toDoSearch();
                    break;
                //搜索成功
                case MSG_SEARCH_OK:
                    GetSearchStoreRE result = JSON.parseObject((String) msg.obj, GetSearchStoreRE.class);
                    mStoreEntities.clear();
                    mStoreEntities.addAll(result.stores);
                    mAdapter.notifyDataSetChanged();
                    if (mSearchKey.equals("")) {
                        mSearchKeyTv.setText("推荐门店");
                    } else {
                        mSearchKeyTv.setText("搜索结果");
                    }
                    if (mStoreEntities.size() > 0) {
                        mNoneLl.setVisibility(View.INVISIBLE);
                        mStoreLv.setVisibility(View.VISIBLE);
                    } else {
                        mNoneLl.setVisibility(View.VISIBLE);
                        mStoreLv.setVisibility(View.INVISIBLE);
                    }
                    break;
                //搜索失败
                case MSG_SEARCH_ERROR:
                    //do nothing
                    break;
            }
        }
    };

    @OnClick({R.id.btn_jump})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_jump:
                startActivity(MainActivity.class);
                startActivity(BleAutoBindActivity.class);
                break;
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void initData() {
        mPostEnable = true;
        mCheckNewData = false;
        mDialog = new DialogBuilder();
        mAdapter = new SearchStoreAdapter(RegisterJoinStoreActivity.this, mStoreEntities);
        mStoreLv.setAdapter(mAdapter);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        //输入框操作
        mSearchEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String newText = mSearchEt.getText().toString();
                // 若输入的内容为空
                if (newText == null || newText.equals("")) {
                    mSearchKey = "";
                    // 若输入的内容不为空
                } else {
                    if (!mPostEnable) {
                        return;
                    }
                    mSearchKey = newText;
                }
                mPostHandler.removeMessages(MSG_SEARCH);
                mPostHandler.sendEmptyMessageDelayed(MSG_SEARCH, 500);
            }
        });
        //列表点击监听
        mStoreLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GetSearchStoreRE.StoresEntity store = mStoreEntities.get(i);
                showSelectStoreDialog(store);
            }
        });

    }

    @Override
    protected void doMyCreate() {
        toDoSearch();
    }

    @Override
    protected void causeGC() {
        if (mPostCancelable != null && !mPostCancelable.isCancelled()) {
            mPostCancelable.cancel();
        }
        if (mPostHandler != null) {
            mPostHandler.removeMessages(MSG_POST_ERROR);
            mPostHandler.removeMessages(MSG_POST_OK);
            mPostHandler.removeMessages(MSG_SEARCH_ERROR);
            mPostHandler.removeMessages(MSG_SEARCH_OK);
            mPostHandler.removeMessages(MSG_SEARCH);
        }
    }

    /***
     * 点击完成按钮
     */
    private void onSelectStoreClick(final int storeId) {
        if (!mPostEnable) {
            return;
        }
        mPostEnable = false;

        mDialog.showProgressDialog(RegisterJoinStoreActivity.this, "正在加入...", true);
        mDialog.setListener(new DialogBuilder.ProgressDialogListener() {
            @Override
            public void onCancel() {
                if (mPostCancelable != null && !mPostCancelable.isCancelled()) {
                    mPostCancelable.cancel();
                }
            }
        });
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildLoginStoreRP(RegisterJoinStoreActivity.this, UrlConfig.URL_LOGIN_STORE
                        , storeId);
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
     * 开始搜索
     */
    private void toDoSearch() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams requestParams = RequestParamsBuilder.BuildSearchStoreParams(RegisterJoinStoreActivity.this,
                        UrlConfig.URL_QUERY_STORE, mSearchKey);
                mPostCancelable = x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            Message msg = new Message();
                            msg.what = MSG_SEARCH_OK;
                            msg.obj = result.result;
                            mPostHandler.sendMessage(msg);
                        } else {
                            mPostHandler.sendEmptyMessage(MSG_SEARCH_ERROR);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        mPostHandler.sendEmptyMessage(MSG_SEARCH_ERROR);
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
     * 显示选择的门店对话框
     *
     * @param store
     */
    private void showSelectStoreDialog(final GetSearchStoreRE.StoresEntity store) {
        mDialog.showSelectStoreDialog(RegisterJoinStoreActivity.this, store);
        mDialog.setListener(new DialogBuilder.SelectStoreDialogListener() {
            @Override
            public void onConfirmBtnClick() {
                onSelectStoreClick(store.storeid);
            }
        });
    }
}
