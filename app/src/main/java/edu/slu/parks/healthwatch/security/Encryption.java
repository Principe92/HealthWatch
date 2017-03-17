package edu.slu.parks.healthwatch.security;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;

import org.joda.time.DateTime;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.security.auth.x500.X500Principal;

import edu.slu.parks.healthwatch.utils.Constants;

/**
 * Created by okori on 30-Dec-16.
 */
public class Encryption implements IEncryption {

    private final Context context;
    private String mAlias;

    public Encryption(Context context, String mAlias) {
        this.context = context;
        this.mAlias = mAlias;
    }

    public void setmAlias(String mAlias) {
        this.mAlias = mAlias;
    }

    @Override
    public String createKeys() throws NoSuchProviderException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException {

        DateTime start = new DateTime();
        DateTime end = start.plusYears(1);

        KeyPairGeneratorSpec spec =
                new KeyPairGeneratorSpec.Builder(context)
                        .setAlias(mAlias)
                        .setSubject(new X500Principal("CN=" + mAlias))
                        .setSerialNumber(BigInteger.valueOf(2016))
                        .setStartDate(start.toDate())
                        .setEndDate(end.toDate())
                        .build();

        KeyPairGenerator generator = KeyPairGenerator.getInstance(Constants.RSA, Constants.KEYSTORE);
        generator.initialize(spec);
        return generator.generateKeyPair().getPublic().toString();
    }

    @Override
    public String sign(String data) throws KeyStoreException, UnrecoverableEntryException,
            NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException, CertificateException {

        byte[] bytes = data.getBytes();
        KeyStore keyStore = KeyStore.getInstance(Constants.KEYSTORE);

        keyStore.load(null);

        KeyStore.Entry entry = keyStore.getEntry(mAlias, null);

        if (entry == null) return null;
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) return null;

        Signature signature = Signature.getInstance(Constants.SIGNATURE);
        signature.initSign(((KeyStore.PrivateKeyEntry) entry).getPrivateKey());

        signature.update(bytes);
        byte[] signed = signature.sign();
        return Base64.encodeToString(signed, Base64.DEFAULT);
    }

    @Override
    public boolean verify(String input, String signature) throws KeyStoreException, CertificateException,
            NoSuchAlgorithmException, IOException, UnrecoverableEntryException, InvalidKeyException, SignatureException {

        byte[] data = input.getBytes();
        byte[] sig;

        if (signature == null) return false;

        try {
            sig = Base64.decode(signature, Base64.DEFAULT);
        } catch (IllegalArgumentException e) {
            return false;
        }

        KeyStore keyStore = KeyStore.getInstance(Constants.KEYSTORE);

        keyStore.load(null);

        KeyStore.Entry entry = keyStore.getEntry(mAlias, null);

        if (entry == null) return false;
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) return false;

        Signature sello = Signature.getInstance(Constants.SIGNATURE);

        sello.initVerify(((KeyStore.PrivateKeyEntry) entry).getCertificate());
        sello.update(data);
        return sello.verify(sig);
    }

    @Override
    public boolean hasKeys() throws KeyStoreException, CertificateException, NoSuchAlgorithmException,
            IOException, UnrecoverableEntryException {
        KeyStore keyStore = KeyStore.getInstance(Constants.KEYSTORE);

        keyStore.load(null);

        KeyStore.Entry entry = keyStore.getEntry(mAlias, null);

        return entry != null && entry instanceof KeyStore.PrivateKeyEntry;
    }
}
