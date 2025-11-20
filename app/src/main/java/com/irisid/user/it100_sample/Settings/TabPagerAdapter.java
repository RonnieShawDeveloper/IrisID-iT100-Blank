package com.irisid.user.it100_sample.Settings;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabPagerAdapter extends FragmentStatePagerAdapter
{
    // Count number of tabs
    private int tabCount;

    public TabPagerAdapter(FragmentManager fm, int tabCount)
    {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position)
    {
        // Returning the current tabs
        switch (position)
        {
            case 0:
                TabMoviesFragment tabMoviesFragment = new TabMoviesFragment();
                return tabMoviesFragment;

            case 1:
                TabImagesFragment tabImagesFragment = new TabImagesFragment();
                return tabImagesFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount()
    {
        return tabCount;
    }
}
