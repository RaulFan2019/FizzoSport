package cn.hwh.sports.ui.window;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import cn.hwh.sports.R;

/**
 * Created by Raul.Fan on 2017/1/3.
 */

public class ShareWindow extends PopupWindow {

    private Activity mContext;

    private View mWindowV;
    private LinearLayout mWeChatCLl;
    private LinearLayout mWeChatMomentLl;
    private Button mCancelBtn;

    private ShareWindowItemClickListener mListener;

    public interface ShareWindowItemClickListener {
        void onShareWeChatCClick();

        void onShareWeChatMomentClick();

        void onCancelBtnClick();
    }

    public ShareWindow(Activity context, final ShareWindowItemClickListener listener) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mWindowV = inflater.inflate(R.layout.dlg_share, null);
        mWeChatCLl = (LinearLayout) mWindowV.findViewById(R.id.btn_wechart_c);
        mWeChatMomentLl = (LinearLayout) mWindowV.findViewById(R.id.btn_wechart_moment);
        mCancelBtn = (Button) mWindowV.findViewById(R.id.btn_cancel);

        mWeChatCLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onShareWeChatCClick();
            }
        });
        mWeChatMomentLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onShareWeChatMomentClick();
            }
        });
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCancelBtnClick();
            }
        });
        initWindow();
    }

    /**
     * 初始化窗口
     */
    private void initWindow() {
//        Rect outRect = new Rect();
//        mContext.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        this.setOutsideTouchable(false);
        //设置SelectPicPopupWindow的View
        this.setContentView(mWindowV);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.anim_popup);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xffffff);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }
}
