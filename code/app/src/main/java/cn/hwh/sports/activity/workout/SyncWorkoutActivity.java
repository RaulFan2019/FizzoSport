package cn.hwh.sports.activity.workout;

import android.content.Intent;
import android.graphics.drawable.ClipDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.entity.event.SyncWatchWorkoutEE;
import cn.hwh.sports.utils.T;

/**
 * Created by Raul.Fan on 2016/10/18.
 */
public class SyncWorkoutActivity extends BaseActivity {

    private static final String TAG = "SyncWorkoutActivity";

    private int mTotalCount;
    private int mLostCount;//剩余未同步数量

    @BindView(R.id.tv_total)
    TextView mTotalTv;//总结文本
    @BindView(R.id.tv_count)
    TextView mCountTv;//剩余同步历史文本
    @BindView(R.id.tv_sync_percent)
    TextView mPercentTv;//进度白分比文本
    @BindView(R.id.iv_progress)
    ImageView mProgressIv;//进度条图形

    private ClipDrawable mClipDrawable;

    private List<WorkoutDE> mWorkoutList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sync_workout;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleEventBus(SyncWatchWorkoutEE event) {
//        Log.v(TAG, "event.msg:" + event.msg);
        if (event.msg == SyncWatchWorkoutEE.MSG_COUNT) {
            mLostCount = event.workoutCount;
            mCountTv.setText("数据同步中.. " + (mTotalCount - mLostCount) + "/" + mTotalCount);
        } else if (event.msg == SyncWatchWorkoutEE.MSG_FINISH) {
            T.showShort(SyncWorkoutActivity.this, "同步完成");
            finish();
//            if (event.workoutCount != -1) {
//                mLostCount = 0;
//            }
//            mTotalTv.append("记录总数：" + mTotalCount + ",未同步数量:" + mLostCount + "\n");
//            for (WorkoutDE workoutDE : mWorkoutList) {
//                mTotalTv.append("开始时间：" + workoutDE.startTime + ",时长:" + workoutDE.duration
//                        + "，心跳数量:" + workoutDE.totalHrSize + "，步数：" + workoutDE.endStep);
//            }
//            finish();
            //同步一条历史的进度
        } else if (event.msg == SyncWatchWorkoutEE.MSG_PERCENT) {
            mPercentTv.setText(event.workoutCount + "");
            mClipDrawable.setLevel((int) (event.workoutCount * 10000 / 100));
            //一条历史同步成功
        } else if (event.msg == SyncWatchWorkoutEE.MSG_SYNC_SUCCESS) {
            mWorkoutList.add(event.workoutDE);
        }
    }

    @OnClick({R.id.btn_close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_close:
                BleManager.getBleManager().mBleConnectE.stopSync();
                finish();
                break;
        }
    }

    @Override
    protected void initData() {
        mCheckNewData = false;
        EventBus.getDefault().register(this);
        LocalApplication.getInstance().showSyncHistoryDialogActivity = true;
        mTotalCount = getIntent().getExtras().getInt("totalCount");
        mLostCount = mTotalCount;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mCountTv.setText("数据同步中.. 0/" + mTotalCount);
        mClipDrawable = (ClipDrawable) mProgressIv.getDrawable();
        mClipDrawable.setLevel(0);
    }

    @Override
    public void onBackPressed() {
        BleManager.getBleManager().mBleConnectE.stopSync();
        finish();
    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void causeGC() {
        EventBus.getDefault().unregister(this);
        LocalApplication.getInstance().showSyncHistoryDialogActivity = false;
    }
}
