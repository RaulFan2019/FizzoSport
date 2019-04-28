package cn.hwh.sports.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import cn.hwh.sports.R;

/**
 * Created by Raul.Fan on 2017/4/7.
 */

public class SelectSportVpAdapter extends PagerAdapter {

    private Context mContext;

    private int[] imgRes;

    public SelectSportVpAdapter(Context context, int[] mainCoachChartAEs) {
        this.mContext = context;
        this.imgRes = mainCoachChartAEs;
    }

    @Override
    public int getCount() {
        return imgRes.length;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_vp_sport_select, null);
        ImageView img = (ImageView) view.findViewById(R.id.iv_sport_type);
        img.setImageResource(imgRes[position]);

        container.addView(view);
        return view;
    }
}
