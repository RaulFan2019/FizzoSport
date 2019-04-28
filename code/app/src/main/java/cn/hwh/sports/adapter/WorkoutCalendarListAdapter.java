package cn.hwh.sports.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.hwh.sports.R;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.entity.adapter.WorkoutListAE;
import cn.hwh.sports.entity.net.GetArticleListRE;
import cn.hwh.sports.entity.net.GetCalendarRE;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by Raul.Fan on 2017/3/9.
 */

public class WorkoutCalendarListAdapter extends BaseAdapter {


    private List<GetCalendarRE.CalendarEntity.WorkoutsEntity> mData ;
    private Context mContext;
    private LayoutInflater mInflater;

    public WorkoutCalendarListAdapter(Context context, List<GetCalendarRE.CalendarEntity.WorkoutsEntity> workoutList){
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = workoutList;
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
        workoutVH holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_list_workout_calendar, null);
            holder = new workoutVH(convertView);
            convertView.setTag(holder);
        } else {
            holder = (workoutVH) convertView.getTag();
        }

        GetCalendarRE.CalendarEntity.WorkoutsEntity re = mData.get(position);

        if (re.resource == SportEnum.resource.PC){
            holder.mTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_pc);
        }else {
            if (re.type == SportEnum.EffortType.RUNNING_OUTDOOR){
                holder.mTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_1);
            }else if (re.type  == SportEnum.EffortType.RUNNING_INDOOR){
                holder.mTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_4);
            }else if (re.type  == SportEnum.EffortType.LOU_TI){
                holder.mTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_5);
            }else if (re.type  == SportEnum.EffortType.DAN_CHE){
                holder.mTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_6);
            }else if (re.type  == SportEnum.EffortType.TUO_YUAN){
                holder.mTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_7);
            }else if (re.type  == SportEnum.EffortType.HUA_CHUAN){
                holder.mTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_8);
            }else if (re.type == SportEnum.EffortType.TIAO_SHENG){
                holder.mTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_10);
            }else {
                holder.mTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_3);
            }
        }
        holder.mTimeTv.setText(TimeU.formatDateToStr(TimeU.formatStrToDate(re.starttime,TimeU.FORMAT_TYPE_1),TimeU.FORMAT_TYPE_7)
                                +"-"
                                +TimeU.formatDateToStr(TimeU.formatStrToDate(re.finishtime,TimeU.FORMAT_TYPE_1),TimeU.FORMAT_TYPE_7));
        holder.mNameTv.setText(re.name);
        return convertView;
    }

    class workoutVH {
        View mTypeV;
        TextView mNameTv;
        TextView mTimeTv;

        public workoutVH(View convertView) {
            mTypeV = convertView.findViewById(R.id.v_sport_type);
            mTimeTv = (TextView) convertView.findViewById(R.id.tv_sport_time);
            mNameTv = (TextView) convertView.findViewById(R.id.tv_sport_name);
        }
    }
}
