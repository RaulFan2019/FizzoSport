package cn.hwh.sports.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.hwh.sports.R;
import cn.hwh.sports.entity.adapter.MainCoachChartAE;
import cn.hwh.sports.ui.common.MonitorColumnView;
import cn.hwh.sports.ui.common.WeekHistogramView;
import cn.hwh.sports.utils.DeviceU;
import cn.hwh.sports.utils.Log;

/**
 * Created by Administrator on 2016/11/29.
 */
public class MainCoachChartAdapter extends PagerAdapter {

    private static final String TAG = "MainCoachChartAdapter";

    TextView mTitleTv;
    TextView mMaxTv;
    TextView mMiddleTv;

    /* week tv */
    TextView mWeekTv0;
    TextView mWeekTv1;
    TextView mWeekTv2;
    TextView mWeekTv3;
    TextView mWeekTv4;
    TextView mWeekTv5;
    TextView mWeekTv6;

    /* column view */
    View mColumn0;
    View mColumn1;
    View mColumn2;
    View mColumn3;
    View mColumn4;
    View mColumn5;
    View mColumn6;

    List<View> mColumns;
    List<TextView> mWeekTvs;


    private List<MainCoachChartAE> mData;
    private Context mContext;

    public MainCoachChartAdapter(Context context, List<MainCoachChartAE> mainCoachChartAEs) {
        this.mContext = context;
        this.mData = mainCoachChartAEs;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_main_coach_vp, null);
        /* week tv view */
        mWeekTv0 = (TextView) view.findViewById(R.id.tv_week_0);
        mWeekTv1 = (TextView) view.findViewById(R.id.tv_week_1);
        mWeekTv2 = (TextView) view.findViewById(R.id.tv_week_2);
        mWeekTv3 = (TextView) view.findViewById(R.id.tv_week_3);
        mWeekTv4 = (TextView) view.findViewById(R.id.tv_week_4);
        mWeekTv5 = (TextView) view.findViewById(R.id.tv_week_5);
        mWeekTv6 = (TextView) view.findViewById(R.id.tv_week_6);
        mWeekTvs = new ArrayList<>();
        mWeekTvs.add(mWeekTv0);
        mWeekTvs.add(mWeekTv1);
        mWeekTvs.add(mWeekTv2);
        mWeekTvs.add(mWeekTv3);
        mWeekTvs.add(mWeekTv4);
        mWeekTvs.add(mWeekTv5);
        mWeekTvs.add(mWeekTv6);

        mColumn0 = view.findViewById(R.id.cv_0);
        mColumn1 = view.findViewById(R.id.cv_1);
        mColumn2 = view.findViewById(R.id.cv_2);
        mColumn3 = view.findViewById(R.id.cv_3);
        mColumn4 = view.findViewById(R.id.cv_4);
        mColumn5 = view.findViewById(R.id.cv_5);
        mColumn6 = view.findViewById(R.id.cv_6);
        mColumns = new ArrayList<>();
        mColumns.add(mColumn0);
        mColumns.add(mColumn1);
        mColumns.add(mColumn2);
        mColumns.add(mColumn3);
        mColumns.add(mColumn4);
        mColumns.add(mColumn5);
        mColumns.add(mColumn6);

        /* line view */
        mMaxTv = (TextView) view.findViewById(R.id.tv_max_line);
        mMiddleTv = (TextView) view.findViewById(R.id.tv_middle_line);
        mTitleTv = (TextView) view.findViewById(R.id.tv_title);

        MainCoachChartAE entity = mData.get(position);
        int maxValue;
        int middleValue;

        mTitleTv.setText(entity.title);
        middleValue = (int) ((entity.maxValue * 1.1) + 0.5) / 2;
        if (middleValue < 6){
            middleValue = 6;
        }
        maxValue = middleValue * 2;

//        Log.v(TAG,"entity.maxValue:" + entity.maxValue +",middleValue:"+ middleValue + "maxValue:" + maxValue);
        //line view
        if (maxValue > 2000) {
            mMaxTv.setText(maxValue / 1000 + "k");
        } else {
            mMaxTv.setText(maxValue + "");
        }
        if (middleValue > 2000) {
            mMiddleTv.setText(middleValue / 1000 + "k");
        } else {
            mMiddleTv.setText(middleValue + "");
        }
        //设置星期文本和柱状图
        for (int i = 0; i < 7; i++) {
            mWeekTvs.get(i).setText(entity.dayList.get(i).weekDay);
            LinearLayout.LayoutParams columnLayoutParams = (LinearLayout.LayoutParams) mColumns.get(i).getLayoutParams();
            columnLayoutParams.height = (int) DeviceU.dpToPixel(148 * entity.dayList.get(i).value / maxValue);
            mColumns.get(i).setLayoutParams(columnLayoutParams);
        }
        mColumns.get(6).setBackgroundResource(R.drawable.bg_monitor_column_selected);

        container.addView(view);
        return view;
    }
}
