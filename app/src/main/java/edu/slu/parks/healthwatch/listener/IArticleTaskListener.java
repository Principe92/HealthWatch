package edu.slu.parks.healthwatch.listener;

import java.util.Collection;

import edu.slu.parks.healthwatch.health.Article;

/**
 * Created by okori on 08-Apr-17.
 */

public interface IArticleTaskListener {
    void updateArticles(Collection<Article> articles);
}
