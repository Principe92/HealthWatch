package edu.slu.parks.healthwatch.model.calendar;

import org.joda.time.DateTime;

import edu.slu.parks.healthwatch.history.ViewType;

/**
 * Created by okori on 20-Nov-16.
 */

public interface ICalendarView {
    boolean isView(int id);

    void toggleCheck();

    boolean getStatus();

    ViewType getViewType();

    int getId();

    String getSqlQuery(DateTime date);

    int getXAxisValue(DateTime date);

    String getXAxisName();

    String getName();
}
