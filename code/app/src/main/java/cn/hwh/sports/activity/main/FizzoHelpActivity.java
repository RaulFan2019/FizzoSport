package cn.hwh.sports.activity.main;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.adapter.FizzoHelpListAdapter;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.GetArticleListRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.common.MyLoadingView;
import cn.hwh.sports.utils.Log;
import cn.hwh.sports.utils.T;

/**
 * Created by Raul.Fan on 2017/3/9.
 * Fizzo 帮助页面
 */
public class FizzoHelpActivity extends BaseActivity {


    private static final String TAG = "FizzoHelpActivity";

    private static final int MSG_POST_ERROR = 0x01;
    private static final int MSG_POST_SUCCESS = 0x02;


    @BindView(R.id.list)
    ListView mList;
    @BindView(R.id.v_loading)
    MyLoadingView mLoadingView;

    private FizzoHelpListAdapter mAdapter;

    private ArrayList<GetArticleListRE> mData = new ArrayList<>();


    @Override
    protected int getLayoutId() {
        return R.layout.activity_fizzo_help;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_POST_ERROR) {
                mLoadingView.LoadError((String) msg.obj);
                T.showShort(FizzoHelpActivity.this, (String) msg.obj);
            } else if (msg.what == MSG_POST_SUCCESS) {
                mLoadingView.loadFinish();
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @OnClick(R.id.btn_back)
    public void onClick() {
        finish();
    }


    @Override
    protected void initData() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mAdapter = new FizzoHelpListAdapter(FizzoHelpActivity.this, mData);
        mList.setAdapter(mAdapter);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String url = mData.get(i).url;
                Bundle bundle = new Bundle();
                bundle.putString("url",url);
                startActivity(ArticleWebViewActivity.class,bundle);
            }
        });
    }

    @Override
    protected void doMyCreate() {
        postGetArticleList();
    }

    @Override
    protected void causeGC() {
        mData.clear();
        if (mHandler != null) {
            mHandler.removeMessages(MSG_POST_ERROR);
            mHandler.removeMessages(MSG_POST_SUCCESS);
        }
    }


    /**
     * 获取文章列表
     */
    private void postGetArticleList() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                String phoneType = android.os.Build.MODEL;
                String uiVer = Build.FINGERPRINT;

//                T.showShort(FizzoHelpActivity.this,"phoneType:" + phoneType + ".uiVer:" + uiVer);
//                Log.v(TAG, "phoneType:" + phoneType + ".uiVer:" + uiVer);
                RequestParams requestParams = RequestParamsBuilder.buildGetArticleList(FizzoHelpActivity.this,
                        UrlConfig.URL_GET_ARTICLE_LIST, phoneType, uiVer);
                x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            ArrayList<GetArticleListRE> tmp
                                    = (ArrayList<GetArticleListRE>) JSONArray.parseArray(result.result, GetArticleListRE.class);
                            mData.addAll(tmp);
                            mHandler.sendEmptyMessage(MSG_POST_SUCCESS);
                        } else {
                            Message msg = mHandler.obtainMessage(MSG_POST_ERROR);
                            msg.obj = result.errormsg;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = mHandler.obtainMessage(MSG_POST_ERROR);
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

