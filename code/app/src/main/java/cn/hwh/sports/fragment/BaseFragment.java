package cn.hwh.sports.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016/7/3.
 */
public abstract class BaseFragment extends Fragment {


    protected boolean mIsHidden = false;
    protected Unbinder unbinder;
    protected Bundle mSavedInstanceState;

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        unbinder = ButterKnife.bind(this, view);
        mSavedInstanceState = savedInstanceState;
        initParams();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mIsHidden) {
            onVisible();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!mIsHidden) {
            onInVisible();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mIsHidden = hidden;
        if (!mIsHidden) {
            onVisible();
        } else {
            onInVisible();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        causeGC();
    }



    protected void startActivity(Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        startActivity(intent);
    }

    protected void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void startActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 初始化布局
     **/
    protected abstract int getLayoutId();

    /**
     * 参数设置
     **/
    protected abstract void initParams();


    /**
     * 参数设置
     **/
    protected abstract void causeGC();

    /**
     * Fragment 可见
     */
    protected abstract void onVisible();

    /**
     * Fragment 不可见
     */
    protected abstract void onInVisible();
}
