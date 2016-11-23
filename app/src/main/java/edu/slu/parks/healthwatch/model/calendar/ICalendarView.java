package edu.slu.parks.healthwatch.model.calendar;

import edu.slu.parks.healthwatch.model.ViewType;

/**
 * Created by okori on 20-Nov-16.
 */

public interface ICalendarView {
    boolean isView(int id);

    void toggleCheck();

    boolean getStatus();

    ViewType getViewType();

    int getId();
}
