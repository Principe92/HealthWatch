package edu.slu.parks.healthwatch.help;

import android.content.Context;

/**
 * Created by okori on 09-Apr-17.
 */

public interface IHelp {
    String getTitle();

    String getSummary();

    void onClick(Context context);
}
