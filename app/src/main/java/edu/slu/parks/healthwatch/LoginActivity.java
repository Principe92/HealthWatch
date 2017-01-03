package edu.slu.parks.healthwatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.noelchew.sparkpostutil.library.EmailListener;

import org.joda.time.DateTime;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import edu.slu.parks.healthwatch.fragments.EmailFragment;
import edu.slu.parks.healthwatch.fragments.SignInFragment;
import edu.slu.parks.healthwatch.fragments.SignUpFragment;
import edu.slu.parks.healthwatch.security.Encryption;
import edu.slu.parks.healthwatch.security.IEncryption;
import edu.slu.parks.healthwatch.security.IPreference;
import edu.slu.parks.healthwatch.security.Preference;
import edu.slu.parks.healthwatch.utils.Constants;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements EmailFragment.EmailListener,
        SignInFragment.SignInListener, SignUpFragment.SignUpListener, EmailListener {
    private static final String TAG = "screen";
    private IPreference preference;
    private IEncryption encryption;
    private boolean hasLogin;
    private boolean pinIsVerified;
    private boolean goHome;
    private View container;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_login);
        container = findViewById(R.id.container);
        preference = new Preference(this);
        encryption = new Encryption();
        hasLogin = preference.getBoolean(Constants.HAS_LOGIN);

        Intent intent = getIntent();
        if (intent != null) {
            goHome = intent.getBooleanExtra(Constants.GOHOME, false);
        }

        if (state != null) return;

        Fragment screen = getFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, screen, TAG)
                .commit();

    }

    private void createKeys() {
        try {
            encryption.createKeys(this);
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidAlgorithmParameterException ignored) {
        }
    }

    public Fragment getFragment() {

        return hasLogin ? new SignInFragment() : new EmailFragment();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {

        SignUpFragment fragment = (SignUpFragment) getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment != null)
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void saveEmail(String email) {
        preference.saveString(email, getString(R.string.key_email));
    }

    @Override
    public boolean isPinValid(String pin) {
        try {
            String savedPin = preference.getString(Constants.PIN);
            pinIsVerified = encryption.verify(pin, savedPin);
            return pinIsVerified;
        } catch (KeyStoreException | UnrecoverableEntryException
                | NoSuchAlgorithmException | InvalidKeyException
                | SignatureException | IOException | CertificateException e) {

            Log.e(getLocalClassName(), e.getMessage());
            return false;
        }
    }

    @Override
    public void next() {
        preference.saveString(DateTime.now().toString(), Constants.PAUSED);

        if (goHome) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else {
            onBackPressed();
        }
    }

    @Override
    public boolean savePin(String pin) {
        try {
            createKeys();
            String signed = encryption.sign(pin);
            preference.saveString(signed, Constants.PIN);
            preference.saveBoolean(true, Constants.HAS_LOGIN);
            return true;
        } catch (KeyStoreException | UnrecoverableEntryException
                | NoSuchAlgorithmException | InvalidKeyException
                | SignatureException | IOException | CertificateException e) {

            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (!hasLogin || pinIsVerified)
            super.onBackPressed();
    }

    private void showMessage(String msg) {
        Snackbar.make(container, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess() {
        showMessage("Reset pin code has been sent to your email");
    }

    @Override
    public void onError(String errorMessage) {
        showMessage(errorMessage);
    }
}

