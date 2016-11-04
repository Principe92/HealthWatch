package edu.slu.parks.healthwatch;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MeasureFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MeasureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeasureFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView diastolicView;
    private TextView systolicView;

    public MeasureFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeasureFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeasureFragment newInstance(String param1, String param2) {
        MeasureFragment fragment = new MeasureFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_measure, container, false);

        Button button = (Button) view.findViewById(R.id.btn_measure);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMeasureButtonClick();
            }
        });

        Button historyBtn = (Button) view.findViewById(R.id.btn_history);
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onHistoryButtonClick();
            }
        });

        diastolicView = (TextView) view.findViewById(R.id.layout_diastolic).findViewById(R.id.txt_pressure);
        systolicView = (TextView) view.findViewById(R.id.layout_systolic).findViewById(R.id.txt_pressure);

        ((TextView) view.findViewById(R.id.layout_systolic).findViewById(R.id.txt_pressure_metric)).setText("Systolic (mmHg)");
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onMeasureButtonClick();

        void onHistoryButtonClick();
    }
}
