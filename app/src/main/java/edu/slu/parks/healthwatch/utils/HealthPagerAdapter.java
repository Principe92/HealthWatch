package edu.slu.parks.healthwatch.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import edu.slu.parks.healthwatch.fragments.HealthFragment;

/**
 * Created by okori on 19-Dec-16.
 */

public class HealthPagerAdapter extends FragmentStatePagerAdapter {

    public HealthPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new HealthFragment();
    }

    @Override
    public int getCount() {
        return 10;
    }
}
