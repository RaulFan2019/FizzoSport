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
import cn.hwh.sports.entity.net.GetSearchStoreRE;
import cn.hwh.sports.entity.net.MoverListRE;
import cn.hwh.sports.ui.common.CircularImage;
import cn.hwh.sports.utils.ImageU;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by Raul.Fan on 2016/11/30.
 */

public class SearchStoreAdapter extends BaseAdapter {

    private List<GetSearchStoreRE.StoresEntity> mStoreEntities;
    private Context mContext;
    private LayoutInflater mInflater;

    public SearchStoreAdapter(Context context, List<GetSearchStoreRE.StoresEntity> mStoreEntities){
        this.mContext = context;
        this.mStoreEntities = mStoreEntities;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mStoreEntities.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SelectVH holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_search_store_list, null);
            holder = new SelectVH(convertView);
            convertView.setTag(holder);
        } else {
            holder = (SelectVH) convertView.getTag();
        }
        final GetSearchStoreRE.StoresEntity store = mStoreEntities.get(position);

        ImageU.loadUserImage(store.logo,holder.mLogoIv);
        holder.mNameTv.setText(store.name);
        holder.mAddressTv.setText(store.address);
        return convertView;
    }


    class SelectVH {
        CircularImage mLogoIv;
        TextView mNameTv;
        TextView mAddressTv;

        public SelectVH(View convertView) {
            mLogoIv = (CircularImage) convertView.findViewById(R.id.iv_store_logo);
            mNameTv = (TextView) convertView.findViewById(R.id.tv_store_name);
            mAddressTv = (TextView) convertView.findViewById(R.id.tv_store_address);
        }
    }
}
