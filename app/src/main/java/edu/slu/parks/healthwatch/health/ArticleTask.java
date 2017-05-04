package edu.slu.parks.healthwatch.health;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by okori on 08-Apr-17.
 */

public class ArticleTask {


    private final IArticleTaskListener listener;
    private ArticleDownloader task;

    public ArticleTask(IArticleTaskListener listener) {
        this.listener = listener;
    }

    public void get() {
        if (task == null) {
            task = new ArticleDownloader();
            task.execute();
        }
    }


    private class ArticleDownloader extends AsyncTask<Void, Void, Collection<Article>> {

        @Override
        protected Collection<Article> doInBackground(Void... voids) {
            List<Article> articles = new ArrayList<>();

            articles.add(new Article("Understanding Your Blood Pressure Readings",
                    "This page outlines the categories of health regarding systolic and diastolic pressure values. "
                            + "It details the range for normal, prehypertension, hypertension stage 1, hypertension stage 2, "
                            + "and hypertension crisis. It also explains the origin of the numbers themselves, detailing the meaning of "
                            + "systolic and diastolic",
                    "http://www.heart.org/HEARTORG/Conditions/HighBloodPressure/"
                            + "KnowYourNumbers/Understanding-Blood-Pressure-Readings_UCM_301764_Article.jsp#.WOQEDxLyvUo"));


            articles.add(new Article(
                    "Blood Pressure Measurement is Changing!",
                    "This page outlines the concerns with using outdated blood pressure devices, such as the mercury sphygmomanometer, and "
                            + "transitioning to automated devices. The inaccuracy of such devices as well as common phenomena, such as “White "
                            + "Coat Hypertension” are thoroughly described. The role of automated, personal blood pressure monitors is clearly "
                            + "the direction in which the industry is headed.",
                    "http://heart.bmj.com/content/85/1/3"
            ));

            articles.add(new Article(
                    "High Blood Pressure (Hypertension)",
                    "This article thoroughly outlines what hypertension is, how it develops, how it can negatively impact your health, and "
                            + "how to reduce it as much as possible. It is a useful guide promoting health and wellness awareness.",
                    "http://www.nhs.uk/conditions/Blood-pressure-(high)/Pages/Introduction.aspx"
            ));

            articles.add(new Article(
                    "Low Blood Pressure (Hypotension)",
                    "This page details causes, risks, and conditions that are associated with low blood pressure, or hypotension.",
                    "http://www.mayoclinic.org/diseases-conditions/low-blood-pressure/basics/causes/con-20032298"
            ));

            articles.add(new Article(
                    "Vital Signs",
                    "This article further elaborates on four of the primary vital signs tracking the body’s basic functions, including body "
                            + "temperature, pulse rate, breathing rate, and blood pressure. The site goes into detail about how to properly collect "
                            + "the values for all the vital signs, then goes into more specific detail about the correct procedure for finding the "
                            + "blood pressure values. It also explains the advantages and disadvantages of the different blood pressure measurement "
                            + "methods",
                    "https://www.urmc.rochester.edu/encyclopedia/content.aspx?ContentTypeID=85&ContentID=P00866"
            ));

            articles.add(new Article(
                    "Blood Pressure Tables for Children and Adolescents",
                    "This article links to a table with specific pressure values for children and adolescents according to their age and height.",
                    "https://www.nhlbi.nih.gov/health-pro/guidelines/current/hypertension-pediatric-jnc-4/blood-pressure-tables"
            ));

            articles.add(new Article(
                    "Oscillometric Blood Pressure Estimation: Past, Present, and Future",
                    "This study explores the role of the oscillometric method in calculating blood pressure values via digital monitors. "
                            + "The study aims to identify the different types of algorithms used, the general method in which it works, and the "
                            + "advantages and disadvantages of each.",
                    "http://ieeexplore.ieee.org/document/7109154/?reload=true"
            ));

            articles.add(new Article(
                    "Anxiety and Expectations Predict the White Coat Effect",
                    "This article details the role of anxiety when taking one’s blood pressure in a clinical environment. The interesting "
                            + "phenomena known as the “White Coat Effect” is explored in great detail throughout the article.",
                    "http://journals.lww.com/bpmonitoring/pages/articleviewer.aspx?year=2005&issue=12000&article=00006&type=abstract"
            ));

            return articles;
        }

        @Override
        protected void onPostExecute(Collection<Article> articles) {
            if (isCancelled()) return;

            listener.updateArticles(articles);
            task = null;
        }
    }
}
