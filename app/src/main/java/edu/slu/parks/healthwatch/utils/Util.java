package edu.slu.parks.healthwatch.utils;

import java.util.ArrayList;
import java.util.List;

import edu.slu.parks.healthwatch.model.calendar.DayView;
import edu.slu.parks.healthwatch.model.calendar.ICalendarView;
import edu.slu.parks.healthwatch.model.calendar.MonthView;
import edu.slu.parks.healthwatch.model.calendar.WeekView;
import edu.slu.parks.healthwatch.model.calendar.YearView;
import edu.slu.parks.healthwatch.views.HealthSection;
import edu.slu.parks.healthwatch.views.HelpSection;
import edu.slu.parks.healthwatch.views.HistorySection;
import edu.slu.parks.healthwatch.views.ISection;
import edu.slu.parks.healthwatch.views.MeasureSection;
import edu.slu.parks.healthwatch.views.SettingsSection;

/**
 * Created by okori on 18-Nov-16.
 */

public class Util {
    public static String makeFragmentName(int viewId, int position) {
        return "android:switcher:" + viewId + ":" + position;
    }

    public static List<ICalendarView> buildCalendarViews() {
        List<ICalendarView> calendarViews = new ArrayList<>();
        calendarViews.add(new DayView());
        calendarViews.add(new MonthView());
        calendarViews.add(new WeekView());
        calendarViews.add(new YearView());

        return calendarViews;
    }

    public static List<ISection> buildNavigationSections() {
        List<ISection> sections = new ArrayList<>();
        sections.add(new HealthSection());
        sections.add(new HelpSection());
        sections.add(new HistorySection());
        sections.add(new MeasureSection());
        sections.add(new HealthSection());
        sections.add(new SettingsSection());

        return sections;
    }
}
