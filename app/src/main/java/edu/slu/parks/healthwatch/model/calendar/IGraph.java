package edu.slu.parks.healthwatch.model.calendar;

import java.util.List;

import edu.slu.parks.healthwatch.database.Record;
import edu.slu.parks.healthwatch.model.ViewType;

/**
 * Created by okori on 18-Nov-16.
 */

public interface IGraph {

    void loadGraph(List<Record> date, ViewType viewType);
}
