package edu.slu.parks.healthwatch.model;

/**
 * Created by okori on 02-Jan-17.
 */
public class EmailMessage {
    private String to;
    private String from;
    private String header;
    private String message;

    public EmailMessage(String to, String from, String header, String message) {
        this.to = to;
        this.from = from;
        this.header = header;
        this.message = message;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public String getHeader() {
        return header;
    }

    public String getMessage() {
        return message;
    }
}
