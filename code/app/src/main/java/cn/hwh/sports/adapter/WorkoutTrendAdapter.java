package cn.hwh.sports.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;

import cn.hwh.sports.fragment.workout.WorkoutTrendMouthFragment;
import cn.hwh.sports.fragment.workout.WorkoutTrendWeekFragment;
import cn.hwh.sports.fragment.workout.WorkoutTrendYearFragment;

/**
 * Created by Raul.Fan on 2017/4/26.
 */

public class WorkoutTrendAdapter extends FragmentStatePagerAdapter {


    public enum Tab {
        WEEK("周"),
        MOUTH("月"),
        YEAR("年");
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

    public Fragment[] mFragments;
    public Tab[] mTabs;

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


    public WorkoutTrendAdapter(FragmentManager fm, Tab[] tabs) {
        super(fm);
        mTabs = tabs;
        mFragments = new Fragment[mTabs.length];

        //dirty way to get reference of cached fragment
        try {
            ArrayList<Fragment> mActive = (ArrayList<Fragment>) sActiveField.get(fm);
            if (mActive != null) {
                for (Fragment fragment : mActive) {
                    if (fragment instanceof WorkoutTrendWeekFragment) {
                        setFragment(Tab.WEEK, fragment);
                    } else if (fragment instanceof WorkoutTrendMouthFragment) {
                        setFragment(Tab.MOUTH, fragment);
                    } else if (fragment instanceof WorkoutTrendYearFragment) {
                        setFragment(Tab.YEAR, fragment);
                    }
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
                case WEEK:
                    mFragments[position] = WorkoutTrendWeekFragment.newInstance();
                    break;
                case MOUTH:
                    mFragments[position] = WorkoutTrendMouthFragment.newInstance();
                    break;
                case YEAR:
                    mFragments[position] = WorkoutTrendYearFragment.newInstance();
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
