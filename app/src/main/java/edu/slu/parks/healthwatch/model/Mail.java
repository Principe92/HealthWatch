package edu.slu.parks.healthwatch.model;

import android.content.Context;

import com.noelchew.sparkpostutil.library.EmailListener;
import com.noelchew.sparkpostutil.library.SparkPostEmailUtil;
import com.noelchew.sparkpostutil.library.SparkPostRecipient;
import com.noelchew.sparkpostutil.library.SparkPostSender;

import edu.slu.parks.healthwatch.R;

/**
 * Created by okori on 02-Jan-17.
 */
public class Mail implements IMail {

    private Context context;
    private String sparkPostApiKey;

    public Mail(Context context) {
        this.sparkPostApiKey = context.getString(R.string.sparkPostApiKey);
        this.context = context;
    }

    @Override
    public void sendMail(EmailMessage message) {
        SparkPostEmailUtil.sendEmail(context,
                sparkPostApiKey,
                message.getHeader(),
                message.getMessage(),
                new SparkPostSender(message.getFrom(), context.getString(R.string.app_name)),
                new SparkPostRecipient(message.getTo()),
                (EmailListener) context);
    }
}
