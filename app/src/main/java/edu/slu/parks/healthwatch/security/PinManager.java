package edu.slu.parks.healthwatch.security;

import android.content.Context;
import android.util.Log;

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

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.model.EmailMessage;
import edu.slu.parks.healthwatch.model.IMail;
import edu.slu.parks.healthwatch.model.Mail;
import edu.slu.parks.healthwatch.utils.Constants;
import edu.slu.parks.healthwatch.utils.Util;

/**
 * Created by okori on 06-Jan-17.
 */
public class PinManager implements IPinManager {
    private Context context;
    private PinManagerListener mListener;
    private IPreference preference;
    private IEncryption encryption;
    private IMail mailSender;

    public PinManager(Context context, PinManagerListener mListener) {
        this.context = context;
        this.preference = new Preference(context);
        this.encryption = new Encryption(context, Constants.ALIAS);
        this.mailSender = new Mail(context);
        this.mListener = mListener;
    }

    @Override
    public void createKeys() {
        try {
            encryption.createKeys();
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidAlgorithmParameterException ignored) {
        }
    }

    @Override
    public boolean hasPin() {
        try {
            return preference.getBoolean(Constants.HAS_LOGIN) && encryption.hasKeys();
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException
                | UnrecoverableEntryException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void clearTemporaryPin() {
        preference.saveString(Constants.TEMPORARY_PIN, "");
    }

    @Override
    public boolean shouldLoginIn() {
        String paused = preference.getString(Constants.PAUSED);
        boolean checkLogin = preference.getBoolean(Constants.CHECK_LOGIN);

        if (!checkLogin) return false;

        int timeout;

        try {
            timeout = Integer.parseInt(preference.getString(context.getString(R.string.key_timeout)));

        } catch (NumberFormatException e) {
            timeout = 1;
        }

        if (!paused.isEmpty()) {
            DateTime pausedTime = DateTime.parse(paused);
            DateTime now = DateTime.now();
            if (now.getDayOfYear() > pausedTime.getDayOfYear() || (now.getMinuteOfDay() - pausedTime.getMinuteOfDay()) > timeout) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isPinValid(String pin) {
        try {
            String savedPin = preference.getString(Constants.PIN);

            return encryption.verify(pin, savedPin);

        } catch (KeyStoreException | UnrecoverableEntryException
                | NoSuchAlgorithmException | InvalidKeyException
                | SignatureException | IOException | CertificateException e) {

            Log.e(this.getClass().getName(), e.getMessage());
            return false;
        }
    }

    @Override
    public void resetPin() {
        int code = (int) (Math.random() * 9000) + 1000;
        saveTemporaryPin(String.valueOf(code));
        EmailMessage message = Util.createResetMessage(preference, context, code);
        mailSender.sendMail(message);
    }

    @Override
    public boolean savePin(String pin) {
        try {
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
    public boolean saveTemporaryPin(String pin) {
        try {
            String signed = encryption.sign(pin);
            preference.saveString(signed, Constants.TEMPORARY_PIN);
            preference.saveString(DateTime.now().toString(), Constants.TEMPORARY_PIN_ISSUED);
            return true;
        } catch (KeyStoreException | UnrecoverableEntryException
                | NoSuchAlgorithmException | InvalidKeyException
                | SignatureException | IOException | CertificateException e) {
            return false;
        }
    }

    @Override
    public boolean isTemporaryPinValid(String pin) {
        try {
            String savedPin = preference.getString(Constants.TEMPORARY_PIN);

            if (!savedPin.isEmpty()) {
                DateTime issued = DateTime.parse(preference.getString(Constants.TEMPORARY_PIN_ISSUED));

                return !DateTime.now().minusDays(1).isAfter(issued) && encryption.verify(pin, savedPin);
            }
        } catch (KeyStoreException | UnrecoverableEntryException
                | NoSuchAlgorithmException | InvalidKeyException
                | SignatureException | IOException | CertificateException e) {

            Log.e(this.getClass().getName(), e.getMessage());
        }

        return false;
    }

    private void showMessage(String message) {
        if (mListener != null) mListener.showMessage(message);
    }

    public interface PinManagerListener {
        void showMessage(String message);
    }
}
