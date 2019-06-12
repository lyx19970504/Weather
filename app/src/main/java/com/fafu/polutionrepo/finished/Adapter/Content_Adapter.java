package com.fafu.polutionrepo.finished.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.fafu.polutionrepo.finished.Fragments.MyFocusedPeopleFragment;
import com.fafu.polutionrepo.finished.Fragments.MyFocusedPollutionFragment;

public class Content_Adapter extends FragmentPagerAdapter {


    public Content_Adapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        if(i == 0){
            return new MyFocusedPeopleFragment();
        }else{
            return new MyFocusedPollutionFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0){
            return "我关注的人";
        }else{
            return "我关注的污染物";
        }
    }
}
