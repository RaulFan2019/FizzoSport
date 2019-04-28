package cn.hwh.sports.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import cn.hwh.sports.ActivityStackManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.utils.DeviceU;

/**
 * Created by Administrator on 2016/7/18.
 */
public abstract class BaseActivity extends AppCompatActivity {


    protected boolean mCheckNewData = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedInstanceState.putParcelable("android:support:fragments", null);
        }
        super.onCreate(savedInstanceState);
        ActivityStackManager.getAppManager().addActivity(this);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initData();
        initViews(savedInstanceState);
        doMyCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //若本页面需要检查新数据，并从后台切换到前台
        if (!LocalApplication.getInstance().isActive) {
            LocalApplication.getInstance().isActive = true;

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!DeviceU.isAppOnForeground(getApplicationContext())) {
            //app 进入后台
            LocalApplication.getInstance().isActive = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        causeGC();
        ActivityStackManager.getAppManager().finishActivity(this);
    }


    protected abstract int getLayoutId();

    protected void startActivity(Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), cls);
        startActivity(intent);
    }

    protected void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), cls);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void startActivityForResult(Class<?> cls, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), cls);
        startActivityForResult(intent, requestCode);
    }

    protected void startActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), cls);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    //初始化数据
    protected abstract void initData();

    //初始化页面
    protected abstract void initViews(Bundle savedInstanceState);

    //执行初始化后的事情
    protected abstract void doMyCreate();

    //释放内存
    protected abstract void causeGC();

}
