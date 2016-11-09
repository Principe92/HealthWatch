package edu.slu.parks.healthwatch.views;

import android.support.v4.app.Fragment;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.fragments.SettingsFragment;

/**
 * Created by okori on 07-Nov-16.
 */

public class SettingsSection implements ISection {
    @Override
    public Fragment getFragment() {
        return new SettingsFragment();
    }

    @Override
    public boolean isSection(int id) {
        return id == R.id.nav_settings;
    }

    @Override
    public int getTitle() {
        return R.string.settings;
    }
}
