package edu.slu.parks.healthwatch.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import edu.slu.parks.healthwatch.utils.Constants;

/**
 * Created by okori on 11/3/2016.
 */

public class AlertDialogFragment extends DialogFragment {
    private Listener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getArguments().getInt(Constants.TITLE));
        builder.setMessage(getArguments().getInt(Constants.MESSAGE))
                .setPositiveButton(getArguments().getInt(Constants.OK), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null)
                            listener.onOkayButtonClick();
                    }
                })
                .setNegativeButton(getArguments().getInt(Constants.CANCEL), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        // Create the AlertDialogFragment object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AlertDialogFragment.Listener) {
            listener = (AlertDialogFragment.Listener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AlertDialogFragment.Listener");
        }
    }

    public interface Listener {
        void onOkayButtonClick();
    }
}
