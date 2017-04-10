package edu.slu.parks.healthwatch.model.section;

import android.app.Activity;
import android.content.Intent;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.help.HelpActivity;
import edu.slu.parks.healthwatch.utils.Constants;

/**
 * Created by okori on 10/31/2016.
 */
public class HelpSection implements ISection {

    @Override
    public boolean isSection(int id) {
        return id == R.id.nav_help;
    }

    @Override
    public int getTitle() {
        return R.string.help;
    }

    @Override
    public void load(Activity activity, int active) {
        Intent intent = new Intent(activity, HelpActivity.class);
        intent.putExtra(Constants.ACTIVE_SECTION, active);
        activity.startActivity(intent);
    }
}
