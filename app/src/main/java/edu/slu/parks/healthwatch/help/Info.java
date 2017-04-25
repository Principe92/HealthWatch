package edu.slu.parks.healthwatch.help;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by okori on 09-Apr-17.
 */

public class Info implements IHelp {
    private String filename;

    public Info() {
        this.filename = "info.pdf";
    }

    @Override
    public String getTitle() {
        return "Tips & Instructions";
    }

    @Override
    public String getSummary() {
        return "Includes tips for good reading, how to properly wear the device" +
                "correct measurement posture, and steps for taking a measurement";
    }

    @Override
    public void onClick(Context context) {
        Uri uri = copyReadAssets(context);

        if (uri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        }
    }

    private Uri copyReadAssets(Context context) {
        AssetManager assetManager = context.getAssets();

        InputStream in;
        OutputStream out;
        File file = new File(context.getFilesDir(), filename);

        if (!file.exists()) {
            try {
                in = assetManager.open(filename);
                out = new FileOutputStream(file);

                copyFile(in, out);
                in.close();
                out.flush();
                out.close();
            } catch (Exception e) {
                return null;
            }
        }


        return FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

}
