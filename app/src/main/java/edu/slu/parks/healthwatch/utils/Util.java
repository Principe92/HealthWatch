package edu.slu.parks.healthwatch.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.model.EmailMessage;
import edu.slu.parks.healthwatch.model.calendar.DayView;
import edu.slu.parks.healthwatch.model.calendar.ICalendarView;
import edu.slu.parks.healthwatch.model.calendar.MonthView;
import edu.slu.parks.healthwatch.model.calendar.WeekView;
import edu.slu.parks.healthwatch.model.calendar.YearView;
import edu.slu.parks.healthwatch.security.IPreference;
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

    public static boolean emailIsValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static EmailMessage createResetMessage(IPreference preference, Context context, int code) {
        String to = preference.getString(context.getString(R.string.key_email));
        String from = "feedback@sparkpostbox.com";
        String header = "HealthWatch - Pin code reset";

        String msg = "Hi," + "\n\n"
                + "Your temporary pin is: %d. Please reset your pin code within 24 hours."
                + " This pin can only be used once. \n\n"
                + "Thanks, \n"
                + "HealthWatch";

        String message = String.format(Locale.getDefault(), msg, code);

        return new EmailMessage(to, from, header, message);
    }
}
