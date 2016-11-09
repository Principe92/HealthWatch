package edu.slu.parks.healthwatch.views;

import android.support.v4.app.Fragment;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.fragments.HelpFragment;

/**
 * Created by okori on 10/31/2016.
 */
public class HelpSection implements ISection {
    @Override
    public Fragment getFragment() {
        return new HelpFragment();
    }

    @Override
    public boolean isSection(int id) {
        return id == R.id.nav_help;
    }

    @Override
    public int getTitle() {
        return R.string.help;
    }
}
