package edu.slu.parks.healthwatch.model.calendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.List;

import edu.slu.parks.healthwatch.database.Record;

/**
 * Created by okori on 18-Nov-16.
 */

public interface IGraph {

    void loadGraph(List<Record> date, ICalendarView calendarView);

    GraphType getType();

    Fragment getNewInstance(Bundle arg);
}
