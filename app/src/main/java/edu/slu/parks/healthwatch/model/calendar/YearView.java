package edu.slu.parks.healthwatch.model.calendar;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.model.ViewType;

/**
 * Created by okori on 20-Nov-16.
 */
public class YearView implements ICalendarView {
    private boolean checked;

    @Override
    public boolean isView(int id) {
        return id == getId();
    }

    @Override
    public void toggleCheck() {
        checked = !checked;
    }

    @Override
    public boolean getStatus() {
        return checked;
    }

    @Override
    public ViewType getViewType() {
        return ViewType.YEAR;
    }

    @Override
    public int getId() {
        return R.id.cal_year;
    }
}
