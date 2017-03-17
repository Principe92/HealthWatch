package edu.slu.parks.healthwatch.model.calendar;

import org.joda.time.DateTime;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.database.Table;
import edu.slu.parks.healthwatch.model.ViewType;

/**
 * Created by okori on 20-Nov-16.
 */
public class DayView implements ICalendarView {
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
        return ViewType.DAY;
    }

    @Override
    public int getId() {
        return R.id.cal_day;
    }

    @Override
    public String getSqlQuery(DateTime date) {
        return "SELECT * FROM " + Table.NAME
                + " WHERE strftime('%Y-%m-%d', date) = strftime('%Y-%m-%d', '" + date.toString() + "')"
                + " ORDER BY id";
    }

    @Override
    public int getXAxisValue(DateTime date) {
        return date.getHourOfDay();
    }

    @Override
    public String getXAxisName() {
        return "Hour";
    }

    @Override
    public String getName() {
        return getViewType().name();
    }
}
