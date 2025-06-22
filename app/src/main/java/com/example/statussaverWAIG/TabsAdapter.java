package com.example.statussaverWAIG;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class TabsAdapter  extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    public TabsAdapter(FragmentManager fm, int NoofTabs){
        super(fm);
        this.mNumOfTabs = NoofTabs;
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                WhatsApp_Fragment home = new WhatsApp_Fragment();
                return home;
            case 1:
                instagram_Fragment about = new instagram_Fragment();
                return about;
            case 2:
                other_Fragment contact = new other_Fragment();
                return contact;
            default:
                return null;
        }
    }
}
