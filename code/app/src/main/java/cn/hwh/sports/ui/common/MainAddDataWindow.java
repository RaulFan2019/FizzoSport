package cn.hwh.sports.ui.common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import cn.hwh.sports.R;
import cn.hwh.sports.utils.DeviceU;

/**
 * Created by Raul.Fan on 2016/11/15.
 * 主页弹出显示添加数据的window页面
 */

public class MainAddDataWindow extends PopupWindow {

    private View mWindowV;
    private LinearLayout mRunLl;
    private LinearLayout mSportLl;
    private LinearLayout mWeightLl;
    private LinearLayout mSleepLl;
    private Activity mContext;

    private MainWindowItemClickListener mListener;

    public interface MainWindowItemClickListener {
        void onRecordRunClick();

        void onRecordSportClick();

        void onRecordWeightClick();

        void onRecordSleepClick();
    }

    public MainAddDataWindow(Activity context, final MainWindowItemClickListener listener) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mWindowV = inflater.inflate(R.layout.fragment_main_add_data, null);
//        mRunLl = (LinearLayout) mWindowV.findViewById(R.id.ll_record_run);
        mSportLl = (LinearLayout) mWindowV.findViewById(R.id.ll_record_sport);
        mWeightLl = (LinearLayout) mWindowV.findViewById(R.id.ll_record_weight);
        mSleepLl = (LinearLayout) mWindowV.findViewById(R.id.ll_record_sleep);

        mRunLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRecordRunClick();
            }
        });
        mSportLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRecordSportClick();
            }
        });
        mWeightLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRecordWeightClick();
            }
        });
        mSleepLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRecordSleepClick();
            }
        });
        initWindow();
    }


    /**
     * 初始化窗口
     */
    private void initWindow() {
        Rect outRect = new Rect();
        mContext.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        this.setOutsideTouchable(false);
        //设置SelectPicPopupWindow的View
        this.setContentView(mWindowV);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight((int) (outRect.height() - DeviceU.dpToPixel(49)));
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(false);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.anim_popup);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x80000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }
}
