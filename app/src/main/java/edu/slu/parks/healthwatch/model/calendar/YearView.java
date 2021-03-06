package edu.slu.parks.healthwatch.model.calendar;

import org.joda.time.DateTime;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.database.Table;
import edu.slu.parks.healthwatch.history.ViewType;

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

    @Override
    public String getSqlQuery(DateTime date) {
        return "SELECT * FROM " + Table.NAME
                + " WHERE strftime('%Y', date) = strftime('%Y', '" + date.toString() + "')"
                + " ORDER BY id";
    }

    @Override
    public int getXAxisValue(DateTime date) {
        return date.getMonthOfYear();
    }

    @Override
    public String getXAxisName() {
        return "Month";
    }

    @Override
    public String getName() {
        return getViewType().name();
    }
}
