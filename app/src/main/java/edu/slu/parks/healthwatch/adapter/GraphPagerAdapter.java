package edu.slu.parks.healthwatch.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import edu.slu.parks.healthwatch.model.calendar.IGraph;
import edu.slu.parks.healthwatch.utils.Constants;

/**
 * Created by okori on 14-Nov-16.
 */

public class GraphPagerAdapter extends FragmentPagerAdapter {
    private List<IGraph> graphs;

    public GraphPagerAdapter(FragmentManager fm, List<IGraph> graphs) {
        super(fm);
        this.graphs = graphs;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle arg = new Bundle();
        arg.putInt(Constants.GRAPH_TYPE, position);
        return graphs.get(position).getNewInstance(arg);
    }

    @Override
    public int getCount() {
        return graphs.size();
    }
}
