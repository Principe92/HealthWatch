package edu.slu.parks.healthwatch.model.section;

import android.app.Activity;
import android.content.Intent;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.measure.HomeActivity;
import edu.slu.parks.healthwatch.utils.Constants;

/**
 * Created by okori on 10/31/2016.
 */
public class MeasureSection implements ISection {

    @Override
    public boolean isSection(int id) {
        return id == R.id.nav_measure;
    }

    @Override
    public int getTitle() {
        return R.string.measure;
    }

    @Override
    public void load(Activity activity, int active) {
        Intent intent = new Intent(activity, HomeActivity.class);
        intent.putExtra(Constants.ACTIVE_SECTION, active);
        activity.startActivity(intent);
    }
}
