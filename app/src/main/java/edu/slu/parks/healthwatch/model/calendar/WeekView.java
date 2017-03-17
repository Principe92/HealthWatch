package edu.slu.parks.healthwatch.model.calendar;

import org.joda.time.DateTime;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.database.Table;
import edu.slu.parks.healthwatch.model.ViewType;

/**
 * Created by okori on 20-Nov-16.
 */
public class WeekView implements ICalendarView {
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
        return ViewType.WEEK;
    }

    @Override
    public int getId() {
        return R.id.cal_week;
    }

    @Override
    public String getSqlQuery(DateTime date) {
        DateTime start = date.dayOfWeek().withMinimumValue();
        DateTime end = date.dayOfWeek().withMaximumValue();

        return "SELECT * FROM " + Table.NAME
                + " WHERE strftime('%Y-%m-%d', date) >= strftime('%Y-%m-%d', '" + start.toString() + "')"
                + " AND strftime('%Y-%m-%d', date) <= strftime('%Y-%m-%d', '" + end.toString() + "')"
                + " ORDER BY id";
    }

    @Override
    public int getXAxisValue(DateTime date) {
        return date.getDayOfWeek();
    }

    @Override
    public String getXAxisName() {
        return "Day";
    }

    @Override
    public String getName() {
        return getViewType().name();
    }
}
