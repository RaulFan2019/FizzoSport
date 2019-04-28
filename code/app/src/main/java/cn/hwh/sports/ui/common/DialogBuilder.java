package cn.hwh.sports.ui.common;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.Date;

import cn.hwh.sports.R;
import cn.hwh.sports.entity.net.GetSearchStoreRE;
import cn.hwh.sports.ui.datepicker.DatePicker;
import cn.hwh.sports.ui.datepicker.TimePicker;
import cn.hwh.sports.utils.ImageU;
import cn.hwh.sports.utils.LengthUtils;

/**
 * Created by Administrator on 2016/11/14.
 */

public class DialogBuilder {


    /**
     * msg Dialog
     **/
    public Dialog mMsgDialog;//加载等待对话框

    /**
     * WatchDisconnect Dialog
     **/
    public Dialog mWatchDisconnectDialog;//加载等待对话框

    /**
     * progress Dialog
     **/
    public Dialog mProgressDialog;//加载等待对话框
    private ProgressDialogListener mProgressListener;

    public interface ProgressDialogListener {
        void onCancel();
    }

    /**
     * choice Dialog
     **/
    public Dialog mChoiceDialog;//选择对话框
    private ChoiceDialogListener mChoiceListener;

    public interface ChoiceDialogListener {
        void onConfirmBtnClick();
    }

    /**
     * choice Dialog
     **/
    public Dialog mSelectStoreDialog;//选择门店对话框
    private SelectStoreDialogListener mSelectStoreListener;

    public interface SelectStoreDialogListener {
        void onConfirmBtnClick();
    }

    /**
     * Edit Dialog
     */
    public Dialog mEditDialog;//输入对话框
    private EditDialogListener mEditDialogListener;

    public interface EditDialogListener {
        void onConfirmBtnClick(String mInputText);
    }

    /**
     * Edit Dialog
     */
    public Dialog mInputLengthDialog;//输入对话框
    private InputLengthDialogListener mInputLengthDialogListener;

    public interface InputLengthDialogListener {
        void onConfirmBtnClick(String mInputText);
    }


    /**
     * date Dialog
     */
    public Dialog mDateDialog;//时间选择对话框
    private DateDialogListener mDateDialogListener;

    public interface DateDialogListener {
        void onConfirmBtnClick(String date);
    }

    /**
     * date Dialog
     */
    public Dialog mTimeDialog;//时间选择对话框
    private TimeDialogListener mTimeDialogListener;

    public interface TimeDialogListener {
        void onConfirmBtnClick(String time);
    }


    /**
     * app 必须升级对话框
     */
    public Dialog mAppMustUpdateDialog;
    private AppMustUpdateListener mAppMustUpdateListener;

    public interface AppMustUpdateListener {
        void onConfirmBtnClick();
    }

    /**
     * app 可升级对话框
     */
    public Dialog mAppNormalUpdateDialog;
    private AppNormalUpdateListener mAppNormalUpdateListener;

    public interface AppNormalUpdateListener {
        void onConfirmBtnClick();
        void onCancelBtnClick();
    }

    /**
     * app 可升级对话框
     */
    public Dialog mAppDownloadDialog;
    private ClipDrawable mClipDrawable;
    private TextView mDownloadProgressTv;


    /**
     * 分享对话框
     */
    public Dialog mShareDialog;
    private ShareDialogListener mShareDialogListener;

    public interface ShareDialogListener {
        void onShareWeChatCClick();

        void onShareWeChatMomentClick();

        void onCancelBtnClick();
    }


    /**
     * 进度一般信息对话框
     * @param context
     * @param title
     * @param msg
     */
    public void showMsgDialog(final Context context, final String title, final String msg) {
        mMsgDialog = new Dialog(context, R.style.DialogTheme);
        mMsgDialog.setContentView(R.layout.dlg_msg);
        mMsgDialog.setCancelable(true);
        mMsgDialog.setCanceledOnTouchOutside(true);

        TextView TitleTv = (TextView) mMsgDialog.findViewById(R.id.tv_title);
        TextView lMsgTv = (TextView) mMsgDialog.findViewById(R.id.tv_msg);

        lMsgTv.setText(msg);
        TitleTv.setText(title);

        mMsgDialog.show();
    }

    /**
     * 显示手环断开连接提示框
     * @param context
     */
    public void showWatchDisconnectDialog(final Context context) {
        mWatchDisconnectDialog = new Dialog(context, R.style.DialogTheme);
        mWatchDisconnectDialog.setContentView(R.layout.dlg_watch_disconnect);
        mWatchDisconnectDialog.setCancelable(true);
        mWatchDisconnectDialog.setCanceledOnTouchOutside(true);

        mWatchDisconnectDialog.show();
    }


    /**
     * 进度提示框
     *
     * @param context
     * @param msg
     * @param cancelAble
     */
    public void showProgressDialog(final Context context, final CharSequence msg, final boolean cancelAble) {
        mProgressDialog = new Dialog(context, R.style.DialogTheme);
        mProgressDialog.setContentView(R.layout.dlg_progress);
        mProgressDialog.setCancelable(cancelAble);
        mProgressDialog.setCanceledOnTouchOutside(cancelAble);

        TextView lMsgTv = (TextView) mProgressDialog.findViewById(R.id.tv_progress_msg);

        lMsgTv.setText(msg);

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (mProgressListener != null) {
                    mProgressListener.onCancel();
                }
            }
        });
        mProgressDialog.show();

    }

    public void setListener(ProgressDialogListener listener) {
        this.mProgressListener = listener;
    }

    /**
     * 选择dialog
     *
     * @param context
     * @param title
     * @param msg
     * @param confirmBtnStr
     */
    public void showChoiceDialog(final Context context, final CharSequence title, final CharSequence msg, final String confirmBtnStr) {
        mChoiceDialog = new Dialog(context, R.style.DialogTheme);
        mChoiceDialog.setContentView(R.layout.dlg_choice);
        mChoiceDialog.setCancelable(true);
        mChoiceDialog.setCanceledOnTouchOutside(true);

        TextView lTitleTv = (TextView) mChoiceDialog.findViewById(R.id.tv_dialog_title);
        TextView lContentTv = (TextView) mChoiceDialog.findViewById(R.id.tv_choice_msg);

        Button mCancelBtn = (Button) mChoiceDialog.findViewById(R.id.btn_cancel);
        Button mOKBtn = (Button) mChoiceDialog.findViewById(R.id.btn_ok);

        lTitleTv.setText(title);
        mOKBtn.setText(confirmBtnStr);

        if (msg == null){
            lContentTv.setVisibility(View.GONE);
        }else {
            lContentTv.setVisibility(View.VISIBLE);
            lContentTv.setText(msg);
        }

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mChoiceDialog != null) {
                    mChoiceDialog.dismiss();
                }
            }
        });

        mOKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mChoiceDialog != null) {
                    if (mChoiceListener != null) {
                        mChoiceListener.onConfirmBtnClick();
                    }
                    mChoiceDialog.dismiss();
                }
            }
        });
        mChoiceDialog.show();
    }

    public void setListener(ChoiceDialogListener listener) {
        this.mChoiceListener = listener;
    }


    /**
     * 选择门店的对话框
     *
     * @param context
     * @param storesEntity
     */
    public void showSelectStoreDialog(final Context context, final GetSearchStoreRE.StoresEntity storesEntity) {
        mSelectStoreDialog = new Dialog(context, R.style.DialogTheme);
        mSelectStoreDialog.setContentView(R.layout.dlg_select_store);
        mSelectStoreDialog.setCancelable(true);
        mSelectStoreDialog.setCanceledOnTouchOutside(true);

        TextView lNameTv = (TextView) mSelectStoreDialog.findViewById(R.id.tv_store_name);
        TextView lAddressTv = (TextView) mSelectStoreDialog.findViewById(R.id.tv_store_address);
        CircularImage mLogIv = (CircularImage) mSelectStoreDialog.findViewById(R.id.iv_store_logo);

        LinearLayout lBtnLeftLl = (LinearLayout) mSelectStoreDialog.findViewById(R.id.ll_left);
        LinearLayout lBtnRightLl = (LinearLayout) mSelectStoreDialog.findViewById(R.id.ll_right);

        lNameTv.setText(storesEntity.name);
        lAddressTv.setText(storesEntity.address);
        ImageU.loadUserImage(storesEntity.logo, mLogIv);

        lBtnLeftLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectStoreDialog != null) {
                    mSelectStoreDialog.dismiss();
                }
            }
        });

        lBtnRightLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectStoreDialog != null) {
                    if (mSelectStoreListener != null) {
                        mSelectStoreListener.onConfirmBtnClick();
                    }
                    mSelectStoreDialog.dismiss();
                }
            }
        });
        mSelectStoreDialog.show();
    }

    public void setListener(SelectStoreDialogListener listener) {
        this.mSelectStoreListener = listener;
    }


    /**
     * 输入框 Dialog
     *
     * @param context
     * @param title
     * @param confirmBtnStr
     * @param inputType     输入框输入类型
     */
    public void showEditDialog(final Context context, final CharSequence title, final String confirmBtnStr,
                               final int inputType, final String editStr) {
        mEditDialog = new Dialog(context, R.style.DialogTheme);
        mEditDialog.setContentView(R.layout.dlg_edit);
        mEditDialog.setCancelable(true);
        mEditDialog.setCanceledOnTouchOutside(true);

        TextView lTitleTv = (TextView) mEditDialog.findViewById(R.id.tv_edit_dialog_title);
        final EditText lInputEt = (EditText) mEditDialog.findViewById(R.id.et_dialog_input);

        LinearLayout lBtnLeftLl = (LinearLayout) mEditDialog.findViewById(R.id.ll_left);
        TextView lBtnLeftTv = (TextView) mEditDialog.findViewById(R.id.tv_left);
        LinearLayout lBtnRightLl = (LinearLayout) mEditDialog.findViewById(R.id.ll_right);
        TextView lBtnRightTv = (TextView) mEditDialog.findViewById(R.id.tv_right);

        lTitleTv.setText(title);

        lBtnLeftTv.setText("取消");
        lBtnRightTv.setText(confirmBtnStr);
        lInputEt.setInputType(inputType);
        lInputEt.setText(editStr);

        lBtnLeftLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEditDialog != null) {
                    mEditDialog.dismiss();
                }
            }
        });

        lBtnRightLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEditDialog != null) {
                    if (mEditDialogListener != null) {
                        mEditDialogListener.onConfirmBtnClick(lInputEt.getText().toString());
                    }
                    mEditDialog.dismiss();
                }
            }
        });
        mEditDialog.show();
    }

    public void setListener(EditDialogListener listener) {
        this.mEditDialogListener = listener;
    }


    /**
     * 显示输入距离的对话框
     *
     * @param context
     * @param length
     */
    public void showInputLengthDialog(final Context context, final float length) {
        mInputLengthDialog = new Dialog(context, R.style.DialogTheme);
        mInputLengthDialog.setContentView(R.layout.dlg_input_length);
        mInputLengthDialog.setCancelable(false);

        final EditText lInputEt = (EditText) mInputLengthDialog.findViewById(R.id.et_dialog_input);

        lInputEt.setText(LengthUtils.formatLength(length) + "");
//        lInputEt.setText("");

        lInputEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    lInputEt.setSelection(lInputEt.getText().length());
                }
            }
        });
        TextView mConfirmBtn = (TextView) mInputLengthDialog.findViewById(R.id.btn_confirm);
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInputLengthDialogListener != null) {
                    mInputLengthDialogListener.onConfirmBtnClick(lInputEt.getText().toString());
                }
            }
        });
        lInputEt.requestFocus();
        mInputLengthDialog.show();
    }

    public void setListener(InputLengthDialogListener listener) {
        this.mInputLengthDialogListener = listener;
    }

    /**
     * 显示日期选择Dialog
     *
     * @param context
     * @param title
     * @param confirmBtnStr
     */
    public void showDateDialog(final Context context, final CharSequence title, final String confirmBtnStr) {
        mDateDialog = new Dialog(context, R.style.DialogTheme);
        mDateDialog.setContentView(R.layout.dlg_date);
        mDateDialog.setCancelable(true);
        mDateDialog.setCanceledOnTouchOutside(true);

        TextView lTitleTv = (TextView) mDateDialog.findViewById(R.id.tv_date_dialog_title);
        final DatePicker mDateDp = (DatePicker) mDateDialog.findViewById(R.id.dp_date);

        LinearLayout lBtnLeftLl = (LinearLayout) mDateDialog.findViewById(R.id.ll_left);
        TextView lBtnLeftTv = (TextView) mDateDialog.findViewById(R.id.tv_left);
        LinearLayout lBtnRightLl = (LinearLayout) mDateDialog.findViewById(R.id.ll_right);
        TextView lBtnRightTv = (TextView) mDateDialog.findViewById(R.id.tv_right);
        //标题
        lTitleTv.setText(title);
        //按钮
        lBtnLeftTv.setText("取消");
        lBtnRightTv.setText(confirmBtnStr);
        //时间组件
        mDateDp.setTextColor(Color.BLACK);
        mDateDp.setTextSize(45);
        mDateDp.setRowNumber(5);
        mDateDp.setFlagTextColor(Color.WHITE);
        mDateDp.setDate(new Date());

        lBtnLeftLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDateDialog != null) {
                    mDateDialog.dismiss();
                }
            }
        });
        lBtnRightLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDateDialog != null) {
                    if (mDateDialogListener != null) {
                        String year = mDateDp.getYear() + "";
                        String month = mDateDp.getMonth() > 9 ? mDateDp.getMonth() + "" : "0" + mDateDp.getMonth();
                        String day = mDateDp.getDayOfMonth() > 9 ? mDateDp.getDayOfMonth() + "" : "0" + mDateDp.getDayOfMonth();
                        String dateStr = year + "-" + month + "-" + day;
                        mDateDialogListener.onConfirmBtnClick(dateStr);
                    }
                    mDateDialog.dismiss();
                }
            }
        });

        mDateDialog.show();
    }

    public void setListener(DateDialogListener listener) {
        this.mDateDialogListener = listener;
    }

    /**
     * 显示时间 dialog
     *
     * @param context
     * @param title
     * @param confirmBtnStr
     */
    public void showTimeDialog(final Context context, final CharSequence title, final String confirmBtnStr) {
        mTimeDialog = new Dialog(context, R.style.DialogTheme);
        mTimeDialog.setContentView(R.layout.dlg_time);
        mTimeDialog.setCancelable(true);
        mTimeDialog.setCanceledOnTouchOutside(true);

        TextView lTitleTv = (TextView) mTimeDialog.findViewById(R.id.tv_time_dialog_title);
        final TimePicker mTimeTp = (TimePicker) mTimeDialog.findViewById(R.id.tp_time);

        LinearLayout lBtnLeftLl = (LinearLayout) mTimeDialog.findViewById(R.id.ll_left);
        TextView lBtnLeftTv = (TextView) mTimeDialog.findViewById(R.id.tv_left);
        LinearLayout lBtnRightLl = (LinearLayout) mTimeDialog.findViewById(R.id.ll_right);
        TextView lBtnRightTv = (TextView) mTimeDialog.findViewById(R.id.tv_right);
        //标题
        lTitleTv.setText(title);
        //按钮
        lBtnLeftTv.setText("取消");
        lBtnRightTv.setText(confirmBtnStr);
        //时间组件
        mTimeTp.setTextColor(Color.BLACK);
        mTimeTp.setTextSize(45);
        mTimeTp.setRowNumber(5);
        mTimeTp.setFlagTextColor(Color.WHITE);
        mTimeTp.setTime(new Date());

        lBtnLeftLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimeDialog != null) {
                    mTimeDialog.dismiss();
                }
            }
        });
        lBtnRightLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimeDialog != null) {
                    if (mTimeDialogListener != null) {
                        String hour = mTimeTp.getHour() > 9 ? mTimeTp.getHour() + "" : "0" + mTimeTp.getHour();
                        String min = mTimeTp.getMin() > 9 ? mTimeTp.getMin() + "" : "0" + mTimeTp.getMin();
                        String dateStr = hour + ":" + min;
                        mTimeDialogListener.onConfirmBtnClick(dateStr);
                    }
                    mTimeDialog.dismiss();
                }
            }
        });

        mTimeDialog.show();
    }

    public void setListener(TimeDialogListener listener) {
        this.mTimeDialogListener = listener;
    }


    /**
     * App必须升级的dialog
     *
     * @param context
     */
    public void showAppMustUpdateDialog(final Context context) {
        mAppMustUpdateDialog = new Dialog(context, R.style.DialogTheme);
        mAppMustUpdateDialog.setContentView(R.layout.dlg_app_update_must);
        mAppMustUpdateDialog.setCancelable(false);
        mAppMustUpdateDialog.setCanceledOnTouchOutside(false);

        Button mUpdateBtn = (Button) mAppMustUpdateDialog.findViewById(R.id.btn_update);


        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAppMustUpdateDialog != null) {
                    if (mAppMustUpdateListener != null) {
                        mAppMustUpdateListener.onConfirmBtnClick();
                    }
                    mAppMustUpdateDialog.dismiss();
                }
            }
        });
        mAppMustUpdateDialog.show();
    }

    public void setListener(AppMustUpdateListener listener) {
        this.mAppMustUpdateListener = listener;
    }


    /**
     * 选择dialog
     *
     * @param context
     */
    public void showAppNormalUpdateDialog(final Context context) {
        mAppNormalUpdateDialog = new Dialog(context, R.style.DialogTheme);
        mAppNormalUpdateDialog.setContentView(R.layout.dlg_app_update_normal);
        mAppNormalUpdateDialog.setCancelable(false);
        mAppNormalUpdateDialog.setCanceledOnTouchOutside(false);

        Button mUpdateBtn = (Button) mAppNormalUpdateDialog.findViewById(R.id.btn_update);
        Button mCancelBtn = (Button) mAppNormalUpdateDialog.findViewById(R.id.btn_cancel);

        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAppNormalUpdateDialog != null) {
                    if (mAppNormalUpdateListener != null) {
                        mAppNormalUpdateListener.onConfirmBtnClick();
                    }
                    mAppNormalUpdateDialog.dismiss();
                }
            }
        });
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAppNormalUpdateDialog != null) {
                    if (mAppNormalUpdateListener != null) {
                        mAppNormalUpdateListener.onCancelBtnClick();
                    }
                    mAppNormalUpdateDialog.dismiss();
                }
            }
        });
        mAppNormalUpdateDialog.show();
    }

    public void setListener(AppNormalUpdateListener listener) {
        this.mAppNormalUpdateListener = listener;
    }

    /**
     * 选择dialog
     *
     * @param context
     */
    public void showAppDownloadDialog(final Context context) {
        mAppDownloadDialog = new Dialog(context, R.style.DialogTheme);
        mAppDownloadDialog.setContentView(R.layout.dlg_app_download);
        mAppDownloadDialog.setCancelable(false);
        mAppDownloadDialog.setCanceledOnTouchOutside(false);

        ImageView mProgressIv = (ImageView) mAppDownloadDialog.findViewById(R.id.iv_progress);
        mDownloadProgressTv = (TextView) mAppDownloadDialog.findViewById(R.id.tv_download_msg);

        mClipDrawable = (ClipDrawable) mProgressIv.getDrawable();
        mClipDrawable.setLevel(0);

        mAppDownloadDialog.show();
    }

    public void setDownLoadProgress(final int percent){
        if (mClipDrawable != null){
            mClipDrawable.setLevel((int) (percent * 10000 / 100));
        }
        if (mDownloadProgressTv != null){
            mDownloadProgressTv.setText("已下载：" + percent + "%");
        }
    }




    /**
     * 选择dialog
     *
     * @param context
     */
    public void showShareDialog(final Context context) {
        mShareDialog = new Dialog(context, R.style.DialogTheme);
        mShareDialog.setContentView(R.layout.dlg_share);
        mShareDialog.setCancelable(true);
        mShareDialog.setCanceledOnTouchOutside(true);

        Window win = mShareDialog.getWindow();
        WindowManager.LayoutParams params = win.getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;//设置x坐标
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;//设置y坐标
        params.gravity = Gravity.BOTTOM;
        win.setAttributes(params);

        LinearLayout mWeChatCLl = (LinearLayout) mShareDialog.findViewById(R.id.btn_wechart_c);
        LinearLayout mWeChatMomentLl = (LinearLayout) mShareDialog.findViewById(R.id.btn_wechart_moment);
        Button mCancelBtn = (Button) mShareDialog.findViewById(R.id.btn_cancel);

        mWeChatCLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mShareDialogListener.onShareWeChatCClick();
                mShareDialog.dismiss();
            }
        });
        mWeChatMomentLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mShareDialogListener.onShareWeChatMomentClick();
                mShareDialog.dismiss();
            }
        });
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mShareDialog.dismiss();
            }
        });
        mShareDialog.show();
    }

    public void setListener(ShareDialogListener listener) {
        this.mShareDialogListener = listener;
    }


    /**
     * 关闭所有的提示框
     */
    public void dismiss() {
        if (mChoiceDialog != null && mChoiceDialog.isShowing()) {
            mChoiceDialog.dismiss();
        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        if (mEditDialog != null && mEditDialog.isShowing()) {
            mEditDialog.dismiss();
        }
        if (mDateDialog != null && mDateDialog.isShowing()) {
            mDateDialog.dismiss();
        }
        if (mTimeDialog != null && mTimeDialog.isShowing()) {
            mTimeDialog.dismiss();
        }
        if (mShareDialog != null && mShareDialog.isShowing()) {
            mShareDialog.dismiss();
        }
    }
}
