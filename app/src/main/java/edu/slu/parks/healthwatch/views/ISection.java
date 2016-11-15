package edu.slu.parks.healthwatch.views;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by okori on 10/31/2016.
 */

public interface ISection {
    Fragment getFragment();

    boolean isSection(int id);

    int getTitle();

    void load(Activity activity, int active);
}
