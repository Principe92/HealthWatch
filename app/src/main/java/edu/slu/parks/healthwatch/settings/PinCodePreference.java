package edu.slu.parks.healthwatch.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.security.FingerPrint;
import edu.slu.parks.healthwatch.security.IFingerPrint;
import edu.slu.parks.healthwatch.security.IPinManager;
import edu.slu.parks.healthwatch.security.PinManager;

/**
 * Created by okorie on 05-Jan-17.
 */

public class PinCodePreference extends DialogPreference implements PinManager.PinManagerListener {
    private TextView status;
    private EditText pinView;
    private boolean pinIsVerified;
    private String newPin;
    private TextView passwordView;
    private IPinManager pinManager;
    private IFingerPrint fingerPrint;
    private View fingerPrintImage;
    private TextView usePrint;

    public PinCodePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        pinManager = new PinManager(context, this);
        fingerPrint = new FingerPrint(context, new FingerPrint.FingerPrintListener() {
            @Override
            public void requestFingerPrintPermission() {
                fingerPrint.requestFingerPrintPermission((Activity) getContext());
            }

            @Override
            public void addFingerPrints() {
                fingerPrint.addFingerPrints(passwordView, (Activity) getContext());
            }

            @Override
            public void onAuthenticated() {
                status.setText(R.string.new_4_digit_pin);
                passwordView.setVisibility(View.GONE);
                clearText();
                pinIsVerified = true;
            }
        });

        setDialogLayoutResource(R.layout.pincode);
        setPositiveButtonText(R.string.next);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        status = (TextView) getDialog().findViewById(R.id.status);
        passwordView = (TextView) getDialog().findViewById(R.id.password);
        pinView = (EditText) getDialog().findViewById(R.id.pin);

        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // do your magic
                        changePin();
                    }
                });

        passwordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pinManager.resetPin();
            }
        });

        if (fingerPrint.isFingerprintAuthAvailable() && fingerPrint.canUseFingerPrint()) {
            fingerPrintImage = (getDialog().findViewById(R.id.fingerprint));
            fingerPrintImage.setVisibility(View.VISIBLE);
            usePrint = (TextView) getDialog().findViewById(R.id.checkBox);
            usePrint.setVisibility(View.VISIBLE);
        }

        fingerPrint.startListening();
    }

    private void changePin() {
        String pin = pinView.getText().toString();

        if (!pinIsVerified) {
            pinIsVerified = pinManager.isPinValid(pin) || pinManager.isTemporaryPinValid(pin);

            if (pinManager.isTemporaryPinValid(pin)) pinManager.clearTemporaryPin();

            if (pinIsVerified) {
                status.setText(R.string.new_4_digit_pin);
                passwordView.setVisibility(View.GONE);
                fingerPrintImage.setVisibility(View.GONE);
                usePrint.setVisibility(View.GONE);
                clearText();
            }
        } else {

            if (pin.length() != 4) {
                showMessage(getContext().getString(R.string.new_pin_must_be_4_digit));
            } else {

                if (newPin == null) {
                    newPin = pin;
                    status.setText(R.string.enter_pin_again);
                    clearText();
                } else if (!newPin.equalsIgnoreCase(pin)) {
                    showMessage(getContext().getString(R.string.incorrect_pin));
                    status.setText(R.string.new_4_digit_pin);
                    newPin = null;
                    clearText();
                } else {
                    if (pinManager.savePin(pin)) {
                        showMessage(getContext().getString(R.string.pin_successfully_changed));
                    } else {
                        showMessage("Unable to update pin code");
                    }

                    pinIsVerified = false;
                    newPin = null;
                    getDialog().dismiss();
                }
            }
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        fingerPrint.stopListening();
    }

    private void clearText() {
        pinView.getText().clear();
    }


    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
