package cn.hwh.sports.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;

import cn.hwh.sports.fragment.workout.WorkoutIndoorChartFragment;
import cn.hwh.sports.fragment.workout.WorkoutIndoorHrFragment;
import cn.hwh.sports.fragment.workout.WorkoutRunningOutdoorChartFragment;
import cn.hwh.sports.fragment.workout.WorkoutRunningOutdoorHrFragment;
import cn.hwh.sports.fragment.workout.WorkoutRunningOutdoorMapFragment;
import cn.hwh.sports.fragment.workout.WorkoutRunningOutdoorPaceFragment;
import cn.hwh.sports.fragment.workout.WorkoutRunningOutdoorSummaryFragment;

/**
 * Created by machenike on 2017/6/5 0005.
 */

public class WorkoutIndoorPagerAdapter extends FragmentStatePagerAdapter {

    public enum Tab {
        HEARTRATE("效果"),
        CHART("趋势");
        private final String name;

        private Tab(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return (otherName != null) && name.equals(otherName);
        }

        public String toString() {
            return name;
        }
    }

    Fragment[] mFragments;
    Tab[] mTabs;

    private static final Field sActiveField;

    static {
        Field f = null;
        try {
            Class<?> c = Class.forName("android.support.v4.app.FragmentManagerImpl");
            f = c.getDeclaredField("mActive");
            f.setAccessible(true);
        } catch (Exception e) {
        }
        sActiveField = f;
    }

    public WorkoutIndoorPagerAdapter(FragmentManager fm, Tab[] tabs , Fragment[]  Fragments) {
        super(fm);
        mTabs = tabs;
//        mFragments = new Fragment[mTabs.length];
        mFragments = Fragments;


        //dirty way to get reference of cached fragment
        try {
            ArrayList<Fragment> mActive = (ArrayList<Fragment>) sActiveField.get(fm);
            if (mActive != null) {
                for (Fragment fragment : mActive) {
                    if (fragment instanceof WorkoutIndoorHrFragment)
                        setFragment(Tab.HEARTRATE, fragment);
                    else if (fragment instanceof WorkoutIndoorChartFragment)
                        setFragment(Tab.CHART, fragment);
                }
            }
        } catch (Exception e) {
        }
    }

    private void setFragment(Tab tab, Fragment f) {
        for (int i = 0; i < mTabs.length; i++)
            if (mTabs[i] == tab) {
                mFragments[i] = f;
                break;
            }
    }

    @Override
    public Fragment getItem(int position) {
        if (mFragments[position] == null) {
            switch (mTabs[position]) {
                case HEARTRATE:
                    mFragments[position] = WorkoutIndoorHrFragment.newInstance();
                    break;
                case CHART:
                    mFragments[position] = WorkoutIndoorChartFragment.newInstance();
                    break;
            }
        }
        return mFragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs[position].toString();
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }

}
