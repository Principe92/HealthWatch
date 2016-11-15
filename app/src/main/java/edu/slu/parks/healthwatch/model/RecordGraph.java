package edu.slu.parks.healthwatch.model;

import java.lang.ref.WeakReference;
import java.util.Date;

/**
 * Created by okori on 14-Nov-16.
 */

public class RecordGraph extends Date {
    private final WeakReference<GraphTask> graphTaskWeakReference;

    public RecordGraph(GraphTask graphTask) {
        this.graphTaskWeakReference = new WeakReference<GraphTask>(graphTask);
    }


    public GraphTask getGraphTask() {
        return graphTaskWeakReference.get();
    }
}
