package cn.hwh.sports.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.hwh.sports.R;
import cn.hwh.sports.entity.adapter.EffortExplainAE;
import cn.hwh.sports.entity.adapter.MainCoachChartAE;
import cn.hwh.sports.utils.DeviceU;

/**
 * Created by Administrator on 2016/11/29.
 */
public class EffortExplainAdapter extends PagerAdapter {

    private static final String TAG = "MainCoachChartAdapter";

    private List<EffortExplainAE> mData;
    private Context mContext;
    Typeface typeFace;

    TextView mTitleTv;
    TextView mNumTv;
    View mImgV;
    TextView mInfoTv;
    TextView mTipTv;

    public EffortExplainAdapter(Context context, List<EffortExplainAE> mAEs) {
        this.mContext = context;
        this.mData = mAEs;
        typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/TradeGothicLTStd-BdCn20.otf");
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_running_indoor_warm_up_vp, null);

        mTitleTv = (TextView) view.findViewById(R.id.tv_title);
        mNumTv = (TextView) view.findViewById(R.id.tv_num);
        mImgV = view.findViewById(R.id.img);
        mInfoTv = (TextView) view.findViewById(R.id.tv_info);
        mNumTv.setTypeface(typeFace);
        mTipTv = (TextView) view.findViewById(R.id.tv_tip);

        EffortExplainAE ae = mData.get(position);

        mNumTv.setText((position + 1) + "");
        mTitleTv.setText(ae.title);
        mImgV.setBackgroundResource(ae.imageRes);
        mInfoTv.setText(ae.info);
        mTipTv.setText(ae.tip);

        container.addView(view);
        return view;
    }
}
