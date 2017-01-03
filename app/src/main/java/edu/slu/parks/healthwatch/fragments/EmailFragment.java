package edu.slu.parks.healthwatch.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import edu.slu.parks.healthwatch.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmailFragment extends Fragment {
    private static final String TAG = "screen";
    private EmailListener mListener;


    private EditText email;

    public EmailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_email, container, false);

        email = (EditText) view.findViewById(R.id.email);
        Button next = (Button) view.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailIsValid(email.getText().toString())) {
                    if (mListener != null) mListener.saveEmail(email.getText().toString());

                    hideKeyboard();

                    SignUpFragment signUp = new SignUpFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                    transaction.replace(R.id.container, signUp, TAG).commit();
                } else {
                    hideKeyboard();

                    Snackbar.make(email, "Invalid email address", Snackbar.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EmailFragment.EmailListener) {
            mListener = (EmailFragment.EmailListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement EmailListener");
        }
    }

    private boolean emailIsValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public interface EmailListener {

        void saveEmail(String email);
    }
}
