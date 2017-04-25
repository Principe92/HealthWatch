package edu.slu.parks.healthwatch.async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.database.Record;
import edu.slu.parks.healthwatch.model.IAddressReceiver;
import edu.slu.parks.healthwatch.utils.AddressResultReceiver;
import edu.slu.parks.healthwatch.utils.Constants;
import edu.slu.parks.healthwatch.utils.FetchAddressIntentService;

/**
 * Created by okori on 12-Jan-17.
 */

public class AddressDownloader {
    private final Context context;
    private HashMap<Integer, AddressTask> tasks;

    public AddressDownloader(Context context) {
        this.context = context;
        this.tasks = new HashMap<>();
    }

    public void download(Record record, TextView textView) {
        if (cancelPotentialDownload(record)) {
            AddressTask task = new AddressTask(textView, context);
            tasks.put(record.id, task);
            task.execute(record);
        }
    }

    private boolean cancelPotentialDownload(Record record) {
        AddressTask addressTask = getAddressTask(record);

        if (addressTask != null) {
            String taskId = addressTask.id;

            if ((taskId == null) || (!taskId.equalsIgnoreCase(String.valueOf(record.id)))) {
                addressTask.cancel(true);
                tasks.remove(record.id);
            } else {
                // The same address is already being downloaded.
                return false;
            }
        }

        return true;
    }

    private AddressTask getAddressTask(Record record) {
        if (record != null) {
            return tasks.get(record.id);
        }

        return null;
    }

    private class AddressTask extends AsyncTask<Record, Void, String> implements IAddressReceiver {
        public String id;
        private boolean done;
        private WeakReference<TextView> location;
        private Context context;
        private AddressResultReceiver addressResultReceiver;
        private String address;

        public AddressTask(TextView location, Context context) {
            this.location = new WeakReference<>(location);
            this.context = context;
            this.addressResultReceiver = new AddressResultReceiver(new Handler());
            this.addressResultReceiver.setReceiver(this);
        }

        @Override
        protected String doInBackground(Record... records) {
            Record record = records[0];
            id = String.valueOf(record.id);
            getAddressFromLocation(record.latitude, record.longitude);

            while (!done) {
            }

            return address;
        }

        private void getAddressFromLocation(double latitude, double longitude) {
            Intent intent = new Intent(context, FetchAddressIntentService.class);
            intent.putExtra(Constants.RECEIVER, addressResultReceiver);
            intent.putExtra(Constants.LATITUDE, latitude);
            intent.putExtra(Constants.LONGITUDE, longitude);
            context.startService(intent);
        }

        @Override
        protected void onPostExecute(String s) {
            if (isCancelled()) return;

            if (location != null) {
                TextView view = location.get();

                if (view != null) {
                    Log.i(this.getClass().getName(), "Address ready for: " + id);
                    view.setText(s);
                }
            }
        }

        @Override
        public void onAddressReceived(int resultCode, Bundle resultData) {
            String mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            address = resultCode == Constants.SUCCESS_RESULT ? mAddressOutput : context.getString(R.string.unknown_location);
            done = true;
        }
    }
}
