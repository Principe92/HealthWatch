package edu.slu.parks.healthwatch.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.WaitingActivity;
import edu.slu.parks.healthwatch.database.HealthDb;
import edu.slu.parks.healthwatch.database.IHealthDb;
import edu.slu.parks.healthwatch.database.Record;
import edu.slu.parks.healthwatch.model.IAddressReceiver;
import edu.slu.parks.healthwatch.model.IDate;
import edu.slu.parks.healthwatch.model.JodaDate;
import edu.slu.parks.healthwatch.utils.AddressResultReceiver;
import edu.slu.parks.healthwatch.utils.Constants;
import edu.slu.parks.healthwatch.utils.FetchAddressIntentService;


public class MeasureFragment extends Fragment implements IAddressReceiver {

    private Record record;
    private OnFragmentInteractionListener mListener;
    private AddressResultReceiver addressResultReceiver;
    private IHealthDb database;
    private IDate date;
    private TextView locationView;
    private TextView diastolicView;
    private TextView systolicView;
    private TextView dateView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addressResultReceiver = new AddressResultReceiver(new Handler());
        database = new HealthDb(getActivity());
        date = new JodaDate(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_measure, container, false);

        Button button = (Button) view.findViewById(R.id.btn_measure);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), WaitingActivity.class);
                startActivity(intent);
            }
        });

        Button historyBtn = (Button) view.findViewById(R.id.btn_history);
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onHistoryButtonClick();
            }
        });

        diastolicView = (TextView) (view.findViewById(R.id.layout_diastolic)).findViewById(R.id.txt_pressure);
        systolicView = (TextView) (view.findViewById(R.id.layout_systolic)).findViewById(R.id.txt_pressure);
        locationView = (TextView) view.findViewById(R.id.txt_location);
        dateView = (TextView) view.findViewById(R.id.txt_date);

        ((TextView) view.findViewById(R.id.layout_systolic).findViewById(R.id.txt_pressure_metric)).setText(R.string.systolic_text);

        return view;
    }

    private void getLatestReading() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                record = database.getLatestReading();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (record != null) {
                            systolicView.setText(String.valueOf(record.systolic));
                            diastolicView.setText(String.valueOf(record.diastolic));
                            dateView.setText(date.toString(Constants.DATE_FORMAT, record.date));

                            locationView.setText(R.string.loading_location);
                            getAddressFromLocation(record.latitude, record.longitude);
                        } else {
                            Toast.makeText(getActivity(), R.string.no_records_available, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
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

    private void getAddressFromLocation(double latitude, double longitude) {
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, addressResultReceiver);
        intent.putExtra(Constants.LATITUDE, latitude);
        intent.putExtra(Constants.LONGITUDE, longitude);
        getActivity().startService(intent);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAddressReceived(int resultCode, Bundle resultData) {
        String mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);

        if (resultCode == Constants.SUCCESS_RESULT) {
            locationView.setText(mAddressOutput);
            locationView.setVisibility(View.VISIBLE);
        } else {
            locationView.setText(R.string.unknown_location);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (addressResultReceiver != null)
            addressResultReceiver.setReceiver(null);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (addressResultReceiver != null) {
            addressResultReceiver.setReceiver(this);
        }

        getLatestReading();
    }

    public interface OnFragmentInteractionListener {

        void onHistoryButtonClick();
    }
}
