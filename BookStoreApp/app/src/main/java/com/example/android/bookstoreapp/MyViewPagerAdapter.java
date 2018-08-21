package com.example.android.bookstoreapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides the appropriate {@link Fragment} for a view pager.
 */
public class MyViewPagerAdapter extends FragmentPagerAdapter {

    //lists of fragments and their according titles
    private final List<Fragment> fragments = new ArrayList<>();
    private final List<String> fragmentTitles = new ArrayList<>();

    /**
     * Create a new {@link MyViewPagerAdapter} object.
     *
     * @param fm is the fragment manager that will keep each fragment's state in the adapter across swipes.
     */
    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * Return the {@link Fragment} that should be displayed for the given page number.
     */
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    /**
     * Return the total number of pages.
     */
    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return fragmentTitles.get(position);
    }

    //a helper method - adds a fragment with its according title to the lists
    public void addFragment(Fragment fragment, String title) {
        fragments.add(fragment);
        fragmentTitles.add(title);
    }
}
