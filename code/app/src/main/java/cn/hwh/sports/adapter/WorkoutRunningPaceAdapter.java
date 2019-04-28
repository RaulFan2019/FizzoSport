package cn.hwh.sports.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.hwh.sports.R;
import cn.hwh.sports.entity.db.LengthSplitDE;
import cn.hwh.sports.entity.net.GetSearchStoreRE;
import cn.hwh.sports.ui.common.CircularImage;
import cn.hwh.sports.utils.LengthUtils;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by machenike on 2017/6/8 0008.
 */

public class WorkoutRunningPaceAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<LengthSplitDE> mData;
    private int maxPace;
    private int minPace;

    public WorkoutRunningPaceAdapter(Context context, List<LengthSplitDE> mSplits , int maxPace,int minPace){
        this.mContext = context;
        this.mData = mSplits;
        this.maxPace = maxPace;
        this.minPace = minPace;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PaceVH holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_list_workout_running_pace, null);
            holder = new PaceVH(convertView);
            convertView.setTag(holder);
        } else {
            holder = (PaceVH) convertView.getTag();
        }
        final LengthSplitDE split = mData.get(position);
        ClipDrawable mClipDrawable;

        if (split.duration == minPace){
            holder.mPaceIv.setImageResource(R.drawable.progress_pace_fast);
        }else {
            holder.mPaceIv.setImageResource(R.drawable.progress_pace);
        }

        mClipDrawable = (ClipDrawable) holder.mPaceIv.getDrawable();
        if (position == mData.size() - 1){
            mClipDrawable.setLevel(0);
            holder.mPaceTv.setTextColor(Color.parseColor("#000000"));
            holder.mLengthTv.setText("");
            holder.mPaceTv.setText("最后" + LengthUtils.formatLength(split.length) + "公里用时"
                    + TimeU.formatSecondsToPace((split.duration)));
        }else {
            mClipDrawable.setLevel((int) (split.duration * 100/maxPace * 10000 / 100));
            holder.mPaceTv.setTextColor(Color.parseColor("#ffffff"));
            holder.mLengthTv.setText((position +1 )+"");
            holder.mPaceTv.setText(TimeU.formatSecondsToPace((long) (split.duration * 1000 / split.length)));
        }
        return convertView;
    }

    class PaceVH {
        TextView mLengthTv;
        TextView mPaceTv;
        ImageView mPaceIv;

        public PaceVH(View convertView) {
            mLengthTv = (TextView) convertView.findViewById(R.id.tv_length);
            mPaceTv = (TextView) convertView.findViewById(R.id.tv_pace);
            mPaceIv = (ImageView) convertView.findViewById(R.id.iv_pace);
        }
    }

}
