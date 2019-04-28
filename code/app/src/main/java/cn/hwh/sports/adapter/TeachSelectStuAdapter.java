package cn.hwh.sports.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.hwh.sports.R;
import cn.hwh.sports.entity.net.MoverListRE;
import cn.hwh.sports.ui.common.CircularImage;
import cn.hwh.sports.utils.ImageU;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by Raul.Fan on 2016/11/30.
 */

public class TeachSelectStuAdapter extends BaseAdapter {


    private List<MoverListRE.MoversEntity> mMoversEntities;
    private Context mContext;
    private LayoutInflater mInflater;

    public TeachSelectStuAdapter(Context context,List<MoverListRE.MoversEntity> mMoversEntities){
        this.mContext = context;
        this.mMoversEntities = mMoversEntities;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mMoversEntities.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SelectVH holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_teach_select_stu_list, null);
            holder = new SelectVH(convertView);
            convertView.setTag(holder);
        } else {
            holder = (SelectVH) convertView.getTag();
        }
        final MoverListRE.MoversEntity moversEntity = mMoversEntities.get(position);

        ImageU.loadUserImage(moversEntity.avatar,holder.mAvatarIv);
        holder.mNickNameTv.setText(moversEntity.nickname);
        if (TimeU.nowDay().equals(moversEntity.setupdate)){
            holder.mDateTv.setText("今日添加");
            holder.mDateTv.setTextSize(15.7f);
            holder.mDateTv.setTextColor(Color.parseColor("#4e4e4e"));
        }else {
            holder.mDateTv.setText(TimeU.formatDateToStr(TimeU.formatStrToDate(moversEntity.setupdate,TimeU.FORMAT_TYPE_3),TimeU.FORMAT_TYPE_5) + "添加");
            holder.mDateTv.setTextSize(11.7f);
            holder.mDateTv.setTextColor(Color.parseColor("#989da3"));
        }
        return convertView;
    }


    class SelectVH {
        CircularImage mAvatarIv;
        TextView mNickNameTv;
        TextView mDateTv;

        public SelectVH(View convertView) {
            mAvatarIv = (CircularImage) convertView.findViewById(R.id.iv_avatar);
            mNickNameTv = (TextView) convertView.findViewById(R.id.tv_nickname);
            mDateTv = (TextView) convertView.findViewById(R.id.tv_date);
        }
    }
}
