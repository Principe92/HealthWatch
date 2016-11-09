package edu.slu.parks.healthwatch.views;

import android.support.v4.app.Fragment;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.fragments.HealthFragment;

/**
 * Created by okori on 10/31/2016.
 */
public class HealthSection implements ISection {
    @Override
    public Fragment getFragment() {
        return new HealthFragment();
    }

    @Override
    public boolean isSection(int id) {
        return id == R.id.nav_health;
    }

    @Override
    public int getTitle() {
        return R.string.health;
    }
}
