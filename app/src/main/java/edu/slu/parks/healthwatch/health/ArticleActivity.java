package edu.slu.parks.healthwatch.health;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import edu.slu.parks.healthwatch.BaseActivity;
import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.utils.Constants;

public class ArticleActivity extends BaseActivity {

    private WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SwipeRefreshLayout layout = ((SwipeRefreshLayout) findViewById(R.id.root));

        Intent intent = getIntent();

        if (intent != null) {
            String page = intent.getStringExtra(Constants.ARTICLE_URL);
            String title = intent.getStringExtra(Constants.ARTICLE_TITLE);

            if (page != null && !page.isEmpty()) {
                ActionBar bar = getSupportActionBar();
                if (bar != null) {
                    bar.setDisplayHomeAsUpEnabled(true);
                    bar.setTitle(title);
                }

                myWebView = (WebView) findViewById(R.id.page);
                WebSettings webSettings = myWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                myWebView.setWebViewClient(new WebViewClient() {

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);

                        layout.setRefreshing(true);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);

                        layout.setRefreshing(false);
                    }
                });
                myWebView.loadUrl(page);
            } else {
                goBack();
            }
        } else {
            goBack();
        }

        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                myWebView.reload();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                goBack();
                return true;

            case R.id.menu_refresh:
                myWebView.reload();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_article;
    }

    private void goBack() {
        onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.article, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
}
