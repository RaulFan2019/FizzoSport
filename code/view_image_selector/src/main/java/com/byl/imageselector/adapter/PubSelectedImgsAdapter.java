package com.byl.imageselector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.byl.imageselector.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;


public class PubSelectedImgsAdapter extends BaseAdapter {

    Context context;
    List<String> list;
    OnItemClickClass onItemClickClass;

    public PubSelectedImgsAdapter(Context context, List<String> data, OnItemClickClass onItemClickClass) {
        this.context = context;
        this.list = data;
        this.onItemClickClass = onItemClickClass;
    }

    @Override
    public int getCount() {
        return list.size() + 1;
    }

    @Override
    public Object getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View view, ViewGroup arg2) {
        Holder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_selected_imgs, null);
            holder = new Holder();
            holder.imageView = (ImageView) view.findViewById(R.id.imageView);
            holder.delete_img = (ImageView) view.findViewById(R.id.delete_img);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        if (position == 0) {
            holder.imageView.setImageResource(R.drawable.item_add_img_pressed);
            holder.delete_img.setVisibility(View.GONE);
            holder.imageView.setOnClickListener(new OnAddPicClick());
        } else {
            ImageLoader.getInstance().displayImage("file://" + list.get(position - 1), holder.imageView);
            holder.delete_img.setOnClickListener(new OnPhotoClick(list.get(position - 1)));
        }

        return view;
    }

    class Holder {
        ImageView imageView, delete_img;
    }

    public interface OnItemClickClass {
        public void OnItemClick(View v, String filepath);

        public void OnAddPickClick();
    }

    class OnAddPicClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            onItemClickClass.OnAddPickClick();
        }
    }

    class OnPhotoClick implements OnClickListener {
        String filepath;

        public OnPhotoClick(String filepath) {
            this.filepath = filepath;
        }

        @Override
        public void onClick(View v) {
            if (list != null && onItemClickClass != null) {
                onItemClickClass.OnItemClick(v, filepath);
            }
        }
    }

}
