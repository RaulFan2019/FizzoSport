package cn.hwh.sports.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.hwh.sports.R;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.entity.adapter.WorkoutListAE;
import cn.hwh.sports.entity.net.GetUserWorkoutListRE;
import cn.hwh.sports.ui.common.PinnedSectionListView;
import cn.hwh.sports.utils.DeviceU;
import cn.hwh.sports.utils.EffortPointU;
import cn.hwh.sports.utils.ImageU;
import cn.hwh.sports.utils.LengthUtils;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by Raul.Fan on 2016/12/19.
 */
public class WorkoutListAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter {

    private List<WorkoutListAE> mData;
    private Context mContext;
    private LayoutInflater mInflater;


    public WorkoutListAdapter(Context context, List<WorkoutListAE> mWorkoutEntities) {
        this.mContext = context;
        this.mData = mWorkoutEntities;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == WorkoutListAE.SECTION;
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
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position).type == WorkoutListAE.SECTION) {
            return WorkoutListAE.SECTION;
        } else if (mData.get(position).workoutEntity.type == SportEnum.EffortType.RUNNING_OUTDOOR) {
            return WorkoutListAE.ITEM_RUN;
        } else if (mData.get(position).workoutEntity.type == SportEnum.EffortType.RUNNING_INDOOR) {
            return WorkoutListAE.ITEM_RUNNING_INDOOR;
        } else {
            return WorkoutListAE.ITEM_EFFORT;
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WorkoutListVH holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_workout_list, null);
            holder = new WorkoutListVH(convertView);
            convertView.setTag(holder);
        } else {
            holder = (WorkoutListVH) convertView.getTag();
        }
        final WorkoutListAE workoutListAE = mData.get(position);

        //total 信息
        if (workoutListAE.type == WorkoutListAE.SECTION) {
            holder.mEffortLl.setVisibility(View.GONE);
            holder.mRunLl.setVisibility(View.GONE);
            holder.mTotalLl.setVisibility(View.VISIBLE);
            holder.mTotalDateTv.setText(TimeU.formatWorkoutListWeekDay(workoutListAE.weekStartTime));
            holder.mTotalValueTv.setText(TimeU.formatChineseDurationByMin(workoutListAE.weekDuration));
            //户外跑步
        } else if (workoutListAE.type == WorkoutListAE.ITEM_RUN) {
            holder.mEffortLl.setVisibility(View.GONE);
            holder.mRunLl.setVisibility(View.VISIBLE);
            holder.mTotalLl.setVisibility(View.GONE);
            holder.mRunTimeTv.setText(TimeU.formatWorkoutListStartTime(workoutListAE.workoutEntity.starttime));
            holder.mRunLengthTv.setText(LengthUtils.formatLength(workoutListAE.workoutEntity.length) + "公里");
            holder.mRunPaceTv.setText(workoutListAE.workoutEntity.avgpace / 60 + "'" + workoutListAE.workoutEntity.avgpace % 60 + "''");
            ImageU.loadRunMapImage(workoutListAE.workoutEntity.staticmap, holder.mRunMapView);

        } else {
            holder.mEffortLl.setVisibility(View.VISIBLE);
            holder.mRunLl.setVisibility(View.GONE);
            holder.mTotalLl.setVisibility(View.GONE);

            if (workoutListAE.type == WorkoutListAE.ITEM_RUNNING_INDOOR) {
                holder.mEffortNameTv.setText("室内跑步");
                holder.mEffortTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_4);
            } else {
                if (workoutListAE.workoutEntity.type == SportEnum.EffortType.LOU_TI) {
                    holder.mEffortNameTv.setText("楼梯机");
                    holder.mEffortTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_5);
                } else if (workoutListAE.workoutEntity.type == SportEnum.EffortType.DAN_CHE) {
                    holder.mEffortNameTv.setText("动感单车");
                    holder.mEffortTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_6);
                } else if (workoutListAE.workoutEntity.type == SportEnum.EffortType.TUO_YUAN) {
                    holder.mEffortNameTv.setText("椭圆机");
                    holder.mEffortTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_7);
                } else if (workoutListAE.workoutEntity.type == SportEnum.EffortType.HUA_CHUAN) {
                    holder.mEffortNameTv.setText("划船机");
                    holder.mEffortTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_8);
                } else if (workoutListAE.workoutEntity.type == SportEnum.EffortType.XIAO_QI_XIE) {
                    holder.mEffortNameTv.setText("小器械");
                    holder.mEffortTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_9);
                } else if (workoutListAE.workoutEntity.type == SportEnum.EffortType.TIAO_SHENG){
                    holder.mEffortNameTv.setText("跳绳");
                    holder.mEffortTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_10);
                }else {
                    holder.mEffortNameTv.setText("无器械");
                    holder.mEffortTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_3);
                }
            }
            holder.mEffortTimeTv.setText(TimeU.formatWorkoutListStartTime(workoutListAE.workoutEntity.starttime));
            holder.mEffortDurationTv.setText(workoutListAE.workoutEntity.duration / 60 + "");
            holder.mEffortCalorieTv.setText(workoutListAE.workoutEntity.calorie + "");
            holder.mEffortAvgHrTv.setText(workoutListAE.workoutEntity.avgheartrate + "");
            holder.mEffortTv.setText(workoutListAE.workoutEntity.avg_effort + "% " + EffortPointU.getEffortPointTip(workoutListAE.workoutEntity.avg_effort));

            for (int i = 0, size = workoutListAE.workoutEntity.hz_zones.size(); i < size; i++) {
                GetUserWorkoutListRE.WorkoutEntity.HzZonesEntity hrZone = workoutListAE.workoutEntity.hz_zones.get(i);
                if (hrZone.minutes == 0 || workoutListAE.workoutEntity.duration / 60 == 0) {
                    holder.ZoneViews.get(i).setVisibility(View.GONE);
                } else {
                    holder.ZoneViews.get(i).setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.ZoneViews.get(i).getLayoutParams();
                    layoutParams.width = (int) DeviceU.dpToPixel(264 * hrZone.minutes / (workoutListAE.workoutEntity.duration / 60));
                    holder.ZoneViews.get(i).setLayoutParams(layoutParams);
                }
            }
        }
        return convertView;
    }

    class WorkoutListVH {
        /* total */
        LinearLayout mTotalLl;//total布局
        TextView mTotalDateTv;//total week str
        TextView mTotalValueTv;//total week duration

        /* run */
        LinearLayout mRunLl;
        TextView mRunTimeTv;//跑步开始时间
        TextView mRunLengthTv;//跑步距离
        TextView mRunPaceTv;//消耗卡路里
        ImageView mRunMapView;//地图

        /* effort*/
        LinearLayout mEffortLl;
        View mEffortTypeV;
        TextView mEffortNameTv;
        TextView mEffortTimeTv;
        TextView mEffortDurationTv;
        TextView mEffortCalorieTv;
        TextView mEffortAvgHrTv;
        TextView mEffortTv;
        View mEffortZone0V;
        View mEffortZone1V;
        View mEffortZone2V;
        View mEffortZone3V;
        View mEffortZone4V;
        View mEffortZone5V;
        List<View> ZoneViews;


        public WorkoutListVH(View convertView) {
            //Total
            mTotalLl = (LinearLayout) convertView.findViewById(R.id.ll_total);
            mTotalDateTv = (TextView) convertView.findViewById(R.id.tv_week_str);
            mTotalValueTv = (TextView) convertView.findViewById(R.id.tv_week_total_duration);

            //run
            mRunLl = (LinearLayout) convertView.findViewById(R.id.ll_workout_list_run);
            mRunTimeTv = (TextView) convertView.findViewById(R.id.tv_time_run);
            mRunLengthTv = (TextView) convertView.findViewById(R.id.tv_length);
            mRunPaceTv = (TextView) convertView.findViewById(R.id.tv_pace);
            mRunMapView = (ImageView) convertView.findViewById(R.id.iv_map);

            //effort
            mEffortLl = (LinearLayout) convertView.findViewById(R.id.ll_workout_list_effort);
            mEffortTypeV = convertView.findViewById(R.id.v_sport_type);
            mEffortNameTv = (TextView) convertView.findViewById(R.id.tv_effort_name);
            mEffortTimeTv = (TextView) convertView.findViewById(R.id.tv_time_effort);
            mEffortDurationTv = (TextView) convertView.findViewById(R.id.tv_effort_duration);
            mEffortCalorieTv = (TextView) convertView.findViewById(R.id.tv_effort_calorie);
            mEffortAvgHrTv = (TextView) convertView.findViewById(R.id.tv_avg_hr);
            mEffortTv = (TextView) convertView.findViewById(R.id.tv_avg_effort);
            mEffortZone0V = convertView.findViewById(R.id.v_zone_0);
            mEffortZone1V = convertView.findViewById(R.id.v_zone_1);
            mEffortZone2V = convertView.findViewById(R.id.v_zone_2);
            mEffortZone3V = convertView.findViewById(R.id.v_zone_3);
            mEffortZone4V = convertView.findViewById(R.id.v_zone_4);
            mEffortZone5V = convertView.findViewById(R.id.v_zone_5);
            ZoneViews = new ArrayList<>();
            ZoneViews.add(mEffortZone0V);
            ZoneViews.add(mEffortZone1V);
            ZoneViews.add(mEffortZone2V);
            ZoneViews.add(mEffortZone3V);
            ZoneViews.add(mEffortZone4V);
            ZoneViews.add(mEffortZone5V);
        }
    }
}
