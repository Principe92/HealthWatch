package edu.slu.parks.healthwatch.help;

/**
 * Created by okori on 09-Apr-17.
 */

public class Info implements IHelp {
    @Override
    public String getTitle() {
        return "Info Document";
    }

    @Override
    public String getSummary() {
        return "Article includes how to ";
    }

    @Override
    public void onClick() {

    }
}
