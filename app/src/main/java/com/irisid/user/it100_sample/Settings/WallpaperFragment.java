package com.irisid.user.it100_sample.Settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.irisid.user.it100_sample_project.R;

public class WallpaperFragment extends Fragment
{
    private TabLayout tabLayout;
    private ViewPager viewpager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_wallpaper, container, false);

        //Logger.e("TAG >> ", "WallpaperFragment");

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        viewpager = (ViewPager) rootView.findViewById(R.id.viewpager);

        // Initializing the TabLayout
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.movies)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.images)));

        // Creating TabPagerAdapter
        TabPagerAdapter pagerAdapter = new TabPagerAdapter(getFragmentManager(), tabLayout.getTabCount());
        viewpager.setAdapter(pagerAdapter);
        viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // Set TabSelectedListener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                viewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });

        return rootView;
    }
}
