package cn.hwh.sports.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.hwh.sports.R;
import cn.hwh.sports.entity.adapter.SportTargetSetAE;

/**
 * Created by Administrator on 2016/11/22.
 */

public class SportTargetSetHolder extends RecyclerView.ViewHolder{

    public TextView mSportTargetTv;
    public View mCheckV;
    public View mWeekTargetImgV;
    public RelativeLayout mItemRl;



    public SportTargetSetHolder(View itemView) {
        super(itemView);
        mSportTargetTv = (TextView) itemView.findViewById(R.id.tv_sport_days);
        mCheckV = itemView.findViewById(R.id.v_target_selected);
        mWeekTargetImgV = itemView.findViewById(R.id.v_week_target);
        mItemRl = (RelativeLayout) itemView.findViewById(R.id.rl_item_sport);
    }

    public void bindView(int position,List<SportTargetSetAE> mTargetList){
        mSportTargetTv.setText(mTargetList.get(position).mWeekDay);
        mWeekTargetImgV.setBackgroundResource(mTargetList.get(position).mDayImgRec);

        if(mTargetList.get(position).mIsSelected){
            mCheckV.setVisibility(View.VISIBLE);
        }else {
            mCheckV.setVisibility(View.INVISIBLE);
        }
    }





}
