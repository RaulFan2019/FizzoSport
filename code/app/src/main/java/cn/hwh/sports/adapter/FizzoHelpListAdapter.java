package cn.hwh.sports.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.hwh.sports.R;
import cn.hwh.sports.entity.net.GetArticleListRE;

/**
 * Created by Raul.Fan on 2017/3/9.
 */

public class FizzoHelpListAdapter extends BaseAdapter {


    private List<GetArticleListRE> mData ;
    private Context mContext;
    private LayoutInflater mInflater;

    public FizzoHelpListAdapter(Context context,List<GetArticleListRE> articleListREList){
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = articleListREList;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ArticleVH holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_fizzo_help_list, null);
            holder = new ArticleVH(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ArticleVH) convertView.getTag();
        }

        GetArticleListRE re = mData.get(position);
        holder.contentTv.setText(re.title);

        return convertView;
    }

    class ArticleVH {
        TextView contentTv;//设备名称文本

        public ArticleVH(View convertView) {
            contentTv = (TextView) convertView.findViewById(R.id.tv_content);
        }
    }
}
