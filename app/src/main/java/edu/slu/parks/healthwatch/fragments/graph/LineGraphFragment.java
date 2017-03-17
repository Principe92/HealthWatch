package edu.slu.parks.healthwatch.fragments.graph;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;


public class LineGraphFragment extends BaseGraphFragment implements IGraph, LineChartOnValueSelectListener {
    private LineChartView systolicGraph;
    private LineChartView diastolicGraph;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_line_graph, container, false);

        systolicGraph = (LineChartView) view.findViewById(R.id.graph_systolic);
        diastolicGraph = (LineChartView) view.findViewById(R.id.graph_diastolic);
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

            List<PointValue> sys = new ArrayList<>();
            List<PointValue> dias = new ArrayList<>();
            List<AxisValue> xAxisValues = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                Record r = records.get(i);
                float day = (float) (r.date.getMillis() / 1.0);
                float sysValue = r.systolic;
                float diasValue = r.diastolic;

                sys.add(new PointValue(day, sysValue));
                dias.add(new PointValue(day, diasValue));

                xAxisValues.add(new AxisValue(calendarView.getXAxisValue(r.date)));

                xAxis.setValues(xAxisValues);
                xAxis.setName(calendarView.getXAxisName());
            }

            addDataToGraph(systolicGraph, sys, Color.BLUE, ySysAxis);
            addDataToGraph(diastolicGraph, dias, Color.RED, yDiaAxis);
            graph.setVisibility(View.VISIBLE);
        } else {
            defaultView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public GraphType getType() {
        return GraphType.LiNE;
    }

    @Override
    public Fragment getNewInstance(Bundle arg) {
        LineGraphFragment frag = new LineGraphFragment();
        frag.setArguments(arg);
        return frag;
    }

    private void addDataToGraph(LineChartView graph, List<PointValue> values, int lineColor, Axis axis) {
        Line line = new Line(values)
                .setColor(lineColor)
                .setStrokeWidth(2)
                .setCubic(true);


        List<Line> lines = new ArrayList<>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setAxisYLeft(axis);
        data.setAxisXBottom(xAxis);
        data.setLines(lines);

        graph.setLineChartData(data);
    }

    private void resetGraphs(LineChartView... graphs) {
        for (LineChartView graph :
                graphs) {
            graph.setLineChartData(new LineChartData());
        }

        defaultView.setVisibility(View.GONE);
    }

    @Override
    public void onValueDeselected() {
    }

    @Override
    public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
        if (mListener != null) mListener.showRecordDetails(pointIndex);
    }
}
