package edu.slu.parks.healthwatch.views;

import android.support.v4.app.Fragment;

import edu.slu.parks.healthwatch.HistoryFragment;
import edu.slu.parks.healthwatch.R;

/**
 * Created by okori on 10/31/2016.
 */
public class HistorySection implements ISection {
    @Override
    public Fragment getFragment() {
        return new HistoryFragment();
    }

    @Override
    public boolean isSection(int id) {
        return id == R.id.nav_history;
    }

    @Override
    public int getTitle() {
        return R.string.history;
    }
}
