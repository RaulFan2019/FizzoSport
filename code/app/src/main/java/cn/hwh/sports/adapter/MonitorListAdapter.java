package cn.hwh.sports.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.hwh.sports.R;
import cn.hwh.sports.entity.adapter.MonitorListAE;
import cn.hwh.sports.ui.common.PinnedSectionListView;
import cn.hwh.sports.utils.LengthUtils;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by Raul.Fan on 2016/12/14.
 * 监测数据列表适配器
 */
public class MonitorListAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter {

    public static final int VALUE_TYPE_INT = 0x01;
    public static final int VALUE_TYPE_FLOAT = 0x02;
    public static final int VALUE_TYPE_LENGTH = 0x03;

    private List<MonitorListAE> mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private int mValueType;
//    Typeface typeFace;



    public MonitorListAdapter(Context context, List<MonitorListAE> mMoversEntities, int valueType) {
        this.mContext = context;
        this.mData = mMoversEntities;
        this.mInflater = LayoutInflater.from(context);
        this.mValueType = valueType;
//        this.typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/TradeGothicLTStd-BdCn20.otf");
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == MonitorListAE.SECTION;
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
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SelectVH holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_monitor_list, null);
            holder = new SelectVH(convertView);
            convertView.setTag(holder);
        } else {
            holder = (SelectVH) convertView.getTag();
        }

        final MonitorListAE monitorListAE = mData.get(position);
        //Total View
        if (monitorListAE.type == MonitorListAE.SECTION) {
            holder.mDetailRl.setVisibility(View.GONE);
            holder.mTotalRl.setVisibility(View.VISIBLE);
            holder.mTotalDateTv.setText(monitorListAE.weekString);
            holder.mTotalValueTv.setText(monitorListAE.preTip + " " + monitorListAE.weekValue + " " + monitorListAE.unit);

            if (mValueType == VALUE_TYPE_INT) {
                holder.mTotalValueTv.setText(monitorListAE.preTip + " " + (int)monitorListAE.weekValue + " " + monitorListAE.unit);
            }else if (mValueType == VALUE_TYPE_LENGTH){
                holder.mTotalValueTv.setText(monitorListAE.preTip + " " + LengthUtils.formatLength(monitorListAE.weekValue) + " " + monitorListAE.unit);
            }else {
                holder.mTotalValueTv.setText(monitorListAE.preTip + " " + monitorListAE.weekValue + " " + monitorListAE.unit);
            }
            //Details View
        } else {
            holder.mDetailRl.setVisibility(View.VISIBLE);
            holder.mTotalRl.setVisibility(View.GONE);
//            holder.mNormalValueTv.setTypeface(typeFace);
            holder.mNormalDateTv.setText(TimeU.formatItemShowDate(monitorListAE.date));
            if (mValueType == VALUE_TYPE_INT) {
                holder.mNormalValueTv.setText((int) monitorListAE.dayValue + "");
            }else if (mValueType == VALUE_TYPE_LENGTH){
                holder.mNormalValueTv.setText(LengthUtils.formatLength(monitorListAE.dayValue) + "");
            }else {
                holder.mNormalValueTv.setText(monitorListAE.dayValue + "");
            }

            holder.mNormalUnitTv.setText(monitorListAE.unit);
            if (monitorListAE.dayValue >= monitorListAE.targetValue) {
                holder.mCompleteImgV.setVisibility(View.VISIBLE);
            } else {
                holder.mCompleteImgV.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    class SelectVH {
        RelativeLayout mTotalRl;//顶部布局
        RelativeLayout mDetailRl;//详细布局
        /* details */
        TextView mNormalDateTv;
        TextView mNormalValueTv;
        TextView mNormalUnitTv;
        View mCompleteImgV;
        /* total */
        TextView mTotalDateTv;
        TextView mTotalValueTv;

        public SelectVH(View convertView) {
            mTotalRl = (RelativeLayout) convertView.findViewById(R.id.rv_total);
            mDetailRl = (RelativeLayout) convertView.findViewById(R.id.rv_detail);

            mTotalDateTv = (TextView) convertView.findViewById(R.id.tv_total_date_scope);
            mTotalValueTv = (TextView) convertView.findViewById(R.id.tv_total_value);

            mNormalDateTv = (TextView) convertView.findViewById(R.id.tv_normal_date);
            mNormalValueTv = (TextView) convertView.findViewById(R.id.tv_normal_value);
            mNormalUnitTv = (TextView) convertView.findViewById(R.id.tv_normal_unit);
            mCompleteImgV = convertView.findViewById(R.id.v_complete_img);
        }
    }
}
