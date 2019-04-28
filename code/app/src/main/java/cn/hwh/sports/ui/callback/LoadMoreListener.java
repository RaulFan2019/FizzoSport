package cn.hwh.sports.ui.callback;

/**
 * Created by Raul.Fan on 2016/12/12.
 * 加载更多的回调事件
 */
public interface LoadMoreListener {

    //正在加载
    public void onLoading();

    //加载失败
    public void onError(String errorMsg);

    //加载成功，下次是否能加载
    public void onSuccess(boolean needLoadMore);

    public void onFinish();
}
