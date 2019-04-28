package cn.hwh.sports.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.hwh.sports.R;
import cn.hwh.sports.adapter.viewholder.SportTargetSetHolder;
import cn.hwh.sports.entity.adapter.SportTargetSetAE;

/**
 * Created by Administrator on 2016/11/22.
 */

public class SportTargetSetAdapter extends RecyclerView.Adapter<SportTargetSetHolder> {
    private LayoutInflater mInflater;
    private Context context;
    private List<SportTargetSetAE> mTargetList;

    private OnItemClickListener mItemListener;

    public SportTargetSetAdapter( Context context,List<SportTargetSetAE> mTargetList) {
        this.context = context;
        this.mTargetList = mTargetList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public SportTargetSetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_sport_target_set,parent,false);
        final SportTargetSetHolder holder = new SportTargetSetHolder(view);
        holder.mItemRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemListener != null){
                    mItemListener.onItemClick(holder.getAdapterPosition());
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final SportTargetSetHolder holder, int position) {
        holder.bindView(position,mTargetList);
    }

    @Override
    public int getItemCount() {
        return mTargetList.size();
    }


    public void setOnItemListener(OnItemClickListener mItemListener){
        this.mItemListener = mItemListener;
    }


    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
