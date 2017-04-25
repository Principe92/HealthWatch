package edu.slu.parks.healthwatch.history.graph;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.database.Record;
import edu.slu.parks.healthwatch.model.calendar.GraphType;
import edu.slu.parks.healthwatch.model.calendar.ICalendarView;
import edu.slu.parks.healthwatch.model.calendar.IGraph;
import edu.slu.parks.healthwatch.model.pressure.PressureType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;


public class BarGraphFragment extends BaseGraphFragment implements IGraph, ColumnChartOnValueSelectListener {
    private ColumnChartView systolicGraph;
    private ColumnChartView diastolicGraph;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bar_graph, container, false);

        systolicGraph = (ColumnChartView) view.findViewById(R.id.graph_systolic);
        diastolicGraph = (ColumnChartView) view.findViewById(R.id.graph_diastolic);
        defaultView = view.findViewById(R.id.no_record);
        graph = view.findViewById(R.id.graph);

        systolicGraph.setOnValueTouchListener(this);
        diastolicGraph.setOnValueTouchListener(this);

        return view;
    }

    @Override
    public void loadGraph(List<Record> records, ICalendarView calendarView) {
        resetGraphs(diastolicGraph, systolicGraph);

        if (records != null && records.size() > 0) {

            int size = records.size();

            List<SubcolumnValue> sys = new ArrayList<>();
            List<SubcolumnValue> dias = new ArrayList<>();
            List<AxisValue> xAxisValues = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                Record r = records.get(i);

                sys.add(new SubcolumnValue((float) r.systolic, ContextCompat.getColor(getContext(), PressureType.GetType(r.diastolic).getColor())));
                dias.add(new SubcolumnValue((float) r.diastolic, ContextCompat.getColor(getContext(), PressureType.GetType(r.diastolic).getColor())));

                xAxisValues.add(new AxisValue(calendarView.getXAxisValue(r.date)));

                xAxis.setValues(xAxisValues);
                xAxis.setName(calendarView.getXAxisName());
            }

            addDataToGraph(systolicGraph, sys, ySysAxis);
            addDataToGraph(diastolicGraph, dias, yDiaAxis);
            graph.setVisibility(View.VISIBLE);
        } else {
            defaultView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public GraphType getType() {
        return GraphType.BAR;
    }

    @Override
    public Fragment getNewInstance(Bundle arg) {
        BarGraphFragment frag = new BarGraphFragment();
        frag.setArguments(arg);
        return frag;
    }

    private void addDataToGraph(ColumnChartView graph, List<SubcolumnValue> values, Axis axis) {
        Column column = new Column(values);

        List<Column> columns = new ArrayList<>();
        columns.add(column);

        ColumnChartData data = new ColumnChartData();
        data.setAxisYLeft(axis);
        data.setAxisXBottom(xAxis);
        data.setColumns(columns);

        graph.setColumnChartData(data);
    }

    private void resetGraphs(ColumnChartView... graphs) {
        for (ColumnChartView graph :
                graphs) {
            graph.setColumnChartData(new ColumnChartData());
        }

        defaultView.setVisibility(View.GONE);
    }

    @Override
    public void onValueDeselected() {

    }

    @Override
    public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
        if (mListener != null) mListener.showRecordDetails(subcolumnIndex);
    }
}
