package edu.slu.parks.healthwatch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import edu.slu.parks.healthwatch.utils.Constants;

/**
 * Created by okori on 14-Nov-16.
 */

public class GraphPagerAdapter extends FragmentPagerAdapter {

    public GraphPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new GraphFragment();
        Bundle arg = new Bundle();
        arg.putInt(Constants.GRAPH_TYPE, position);
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
