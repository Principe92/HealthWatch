package edu.slu.parks.healthwatch.model.section;

import android.app.Activity;

/**
 * Created by okori on 10/31/2016.
 */

public interface ISection {
    boolean isSection(int id);

    int getTitle();

    void load(Activity activity, int active);
}
