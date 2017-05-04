package edu.slu.parks.healthwatch.health;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.Collection;

import edu.slu.parks.healthwatch.NavigationActivity;
import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.utils.Constants;

public class HealthActivity extends NavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.health);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_health);

        RecyclerView mainView = (RecyclerView) findViewById(R.id.list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mainView.setLayoutManager(mLayoutManager);

        final HealthListAdapter mAdapter = new HealthListAdapter(new IHealthListAdapterListener() {
            @Override
            public void showArticle(Article article) {
                loadArticle(article);
            }
        });
        mainView.setAdapter(mAdapter);

        new ArticleTask(new IArticleTaskListener() {
            @Override
            public void updateArticles(Collection<Article> articles) {
                mAdapter.addAll(articles);
            }
        }).get();
    }

    private void loadArticle(Article article) {
        Intent intent = new Intent(this, ArticleActivity.class);
        intent.putExtra(Constants.ARTICLE_TITLE, article.getTitle());
        intent.putExtra(Constants.ARTICLE_URL, article.getLink());
        startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_health;
    }

}
