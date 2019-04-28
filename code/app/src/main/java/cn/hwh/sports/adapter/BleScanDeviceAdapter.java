package cn.hwh.sports.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.hwh.sports.R;
import cn.hwh.sports.entity.adapter.BleScanAE;

/**
 * Created by Administrator on 2016/7/20.
 */
public class BleScanDeviceAdapter extends BaseAdapter {

    private static final String TAG = "BleScanDeviceAdapter";

    private Context mContext;
    private LayoutInflater mInflater;
    private List<BleScanAE> mData;
    private localListener mListener;

    public interface localListener {
        void onSelectDevice(int position);
    }

    public BleScanDeviceAdapter(Context context, List<BleScanAE> mList, localListener mListener) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = mList;
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return mData.size();
    }


    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        BleScanDeviceVH holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_heartbeat_scan, null);
            holder = new BleScanDeviceVH(convertView);
            convertView.setTag(holder);
        } else {
            holder = (BleScanDeviceVH) convertView.getTag();
        }

        BleScanAE leBleScan = mData.get(position);
        holder.deviceNameTv.setText(leBleScan.device.getName());
        //状态变化
        if (leBleScan.mConnectState == BleScanAE.STATE_DISCONNECT) {
            holder.connectingV.clearAnimation();
            holder.connectingV.setVisibility(View.GONE);
            holder.normalV.setVisibility(View.GONE);
//            Log.v(TAG, "rssi:" + leBleScan.rssi);
            holder.signalV.setVisibility(View.VISIBLE);
            if (leBleScan.rssi > -50) {
                holder.signalV.setBackgroundResource(R.drawable.ic_signal_5);
            } else if (leBleScan.rssi > -60){
                holder.signalV.setBackgroundResource(R.drawable.ic_signal_4);
            }else if (leBleScan.rssi > -70){
                holder.signalV.setBackgroundResource(R.drawable.ic_signal_3);
            }else if (leBleScan.rssi > -80){
                holder.signalV.setBackgroundResource(R.drawable.ic_signal_2);
            }else if (leBleScan.rssi > -90){
                holder.signalV.setBackgroundResource(R.drawable.ic_signal_1);
            }else {
                holder.signalV.setBackgroundResource(R.drawable.ic_signal_0);
            }

        } else if (leBleScan.mConnectState == BleScanAE.STATE_CONNECTING) {
            holder.normalV.setVisibility(View.GONE);
            holder.connectingV.setVisibility(View.VISIBLE);
            RotateAnimation anim = (RotateAnimation) AnimationUtils
                    .loadAnimation(mContext, R.anim.rotating);
            LinearInterpolator lir = new LinearInterpolator();
            anim.setInterpolator(lir);
            holder.connectingV.startAnimation(anim);
            holder.normalV.setVisibility(View.GONE);
            holder.signalV.setVisibility(View.GONE);
        } else if (leBleScan.mConnectState == BleScanAE.STATE_CONNECTED) {
//            holder.connectingV.clearAnimation();
//            holder.connectingV.setVisibility(View.GONE);
//            holder.normalV.setVisibility(View.VISIBLE);
//            holder.signalV.setVisibility(View.GONE);
        }

        holder.deviceLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSelectDevice(position);
            }
        });
//
        return convertView;
    }

    class BleScanDeviceVH {
        LinearLayout deviceLl;//选择设备的布局
        TextView deviceNameTv;//设备名称文本
        View connectingV;//表示正在连接的图标
        View normalV;//普通状态的V
        View signalV;

        public BleScanDeviceVH(View convertView) {
            deviceLl = (LinearLayout) convertView.findViewById(R.id.ll_device);
            deviceNameTv = (TextView) convertView.findViewById(R.id.tv_name);
            connectingV = convertView.findViewById(R.id.v_connecting);
            normalV = convertView.findViewById(R.id.v_normal);
            signalV = convertView.findViewById(R.id.v_signal);
        }
    }
}
