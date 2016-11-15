package edu.slu.parks.healthwatch.views;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import edu.slu.parks.healthwatch.HistoryActivity;
import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.fragments.HistoryFragment;
import edu.slu.parks.healthwatch.utils.Constants;

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

    @Override
    public void load(Activity activity, int active) {
        Intent intent = new Intent(activity, HistoryActivity.class);
        intent.putExtra(Constants.ACTIVE_SECTION, active);
        activity.startActivity(intent);
    }
}
