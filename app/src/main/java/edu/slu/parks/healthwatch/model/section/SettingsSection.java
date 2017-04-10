package edu.slu.parks.healthwatch.model.section;

import android.app.Activity;
import android.content.Intent;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.settings.SettingsActivity;
import edu.slu.parks.healthwatch.utils.Constants;

/**
 * Created by okori on 07-Nov-16.
 */

public class SettingsSection implements ISection {

    @Override
    public boolean isSection(int id) {
        return id == R.id.nav_settings;
    }

    @Override
    public int getTitle() {
        return R.string.settings;
    }

    @Override
    public void load(Activity activity, int active) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        intent.putExtra(Constants.ACTIVE_SECTION, active);
        activity.startActivity(intent);
    }
}
