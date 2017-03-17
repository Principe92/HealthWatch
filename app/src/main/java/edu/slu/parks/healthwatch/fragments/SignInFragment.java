package edu.slu.parks.healthwatch.fragments;


import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.security.FingerPrint;
import edu.slu.parks.healthwatch.security.IFingerPrint;
import edu.slu.parks.healthwatch.security.IPinManager;
import edu.slu.parks.healthwatch.security.PinManager;

public class SignInFragment extends Fragment implements PinLockListener, PinManager.PinManagerListener {

    private PinLockView mPinLockView;
    private TextView mStatusView;
    private SignInListener mListener;
    private IPinManager pinManager;
    private IFingerPrint fingerPrint;
    private boolean resetPin;
    private boolean firstIntent;
    private String lastPin;
    private FingerprintManager.CryptoObject mCryptoObject;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        fingerPrint = new FingerPrint(getContext(), new FingerPrint.FingerPrintListener() {
            @Override
            public void requestFingerPrintPermission() {
                fingerPrint.requestFingerPrintPermission(getActivity());
            }

            @Override
            public void addFingerPrints() {
                fingerPrint.addFingerPrints(mPinLockView, getActivity());
            }

            @Override
            public void onAuthenticated() {
                mListener.next(true);
            }
        });


        pinManager = new PinManager(getContext(), this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignInFragment.SignInListener) {
            mListener = (SignInFragment.SignInListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SignInFragment.SignInListener");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        fingerPrint.stopListening();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        mStatusView = (TextView) view.findViewById(R.id.status);

        mPinLockView = (PinLockView) view.findViewById(R.id.pin_lock_view);
        IndicatorDots mIndicatorDots = (IndicatorDots) view.findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLength(Integer.parseInt(getString(R.string.pinLength)));
        mPinLockView.setPinLockListener(this);

        if (fingerPrint.isFingerprintAuthAvailable() && fingerPrint.canUseFingerPrint()) {
            (view.findViewById(R.id.fingerprint)).setVisibility(View.VISIBLE);
            TextView usePrint = (TextView) view.findViewById(R.id.checkBox);
            usePrint.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onComplete(String pin) {

        if (resetPin) resetPin(pin);

        else if (mListener != null) {
            if (pinManager.isPinValid(pin) || pinManager.isTemporaryPinValid(pin)) {
                if (pinManager.isTemporaryPinValid(pin)) {
                    pinManager.clearTemporaryPin();
                    resetPin = firstIntent = true;
                    mStatusView.setText(R.string.enter_your_new_pin);
                    mPinLockView.resetPinLockView();
                } else
                    mListener.next(true);
            } else {
                Snackbar.make(mPinLockView, R.string.invalid_pin, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.reset_pin, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mPinLockView.resetPinLockView();
                                pinManager.resetPin();
                            }
                        })
                        .show();
            }
        }
    }

    private void resetPin(String pin) {
        if (firstIntent) {
            firstIntent = false;
            mStatusView.setText(R.string.enter_pin_again);
            lastPin = pin;
            mPinLockView.resetPinLockView();

        } else {

            if (pin.equalsIgnoreCase(lastPin)) {
                showMessage(getString(R.string.pin_successfully_changed));
                pinManager.savePin(pin);
                next();
            } else {
                firstIntent = true;
                showMessage(getString(R.string.incorrect_pin));
                mPinLockView.resetPinLockView();
                mStatusView.setText(R.string.enter_your_new_pin);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        fingerPrint.startListening();
    }

    @Override
    public void onEmpty() {
    }

    @Override
    public void onPinChange(int pinLength, String intermediatePin) {
    }

    private void next() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) mListener.next(true);
            }
        }, 1000);

    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public interface SignInListener {

        void next(boolean pinVerified);
    }
}
