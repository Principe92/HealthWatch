package edu.slu.parks.healthwatch.health;

/**
 * Created by okori on 08-Apr-17.
 */

public class Article {
    private String title;
    private String summary;
    private String link;

    public Article(String title, String summary, String link) {
        this.title = title;
        this.summary = summary;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getLink() {
        return link;
    }
}
