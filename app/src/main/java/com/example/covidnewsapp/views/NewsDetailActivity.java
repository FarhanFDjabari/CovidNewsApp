package com.example.covidnewsapp.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.covidnewsapp.R;
import com.example.covidnewsapp.Utils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class NewsDetailActivity extends AppCompatActivity {

    private TextView appbarTitle, appBarSubtitle;
    private Toolbar toolbar;
    private String newsUrl, newsSource, newsTitle, newsDesc;
    WebView webView;
    private Handler uiHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        uiHandler = new Handler();

        toolbar = findViewById(R.id.newsDetailToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = findViewById(R.id.webView);
        appbarTitle = findViewById(R.id.title_on_appbar);
        appBarSubtitle = findViewById(R.id.subtitle_on_appbar);

        Intent intent = getIntent();
        newsUrl = intent.getStringExtra("url");
        newsSource = intent.getStringExtra("source");
        newsTitle = intent.getStringExtra("title");
        newsDesc = intent.getStringExtra("desc");

        appbarTitle.setText(newsSource);
        appBarSubtitle.setText(newsUrl);

        initWebView(newsUrl);
    }

    public void initWebView(String url) {
        try {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.setWebViewClient(new WebViewClient());
                    webView.loadUrl(url);
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.removeView(webView);
        webView.removeAllViews();
        webView.destroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.share) {
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plan");
                intent.putExtra(Intent.EXTRA_SUBJECT, "News from " + newsSource);
                String body = newsTitle + "\n\n" + newsUrl + "\n" + "------ Share from KawalCovid Tracking App ------" + "\n\n" + newsDesc;
                intent.putExtra(Intent.EXTRA_TEXT, body);
                startActivity(Intent.createChooser(intent, "Share with:"));
            } catch(Exception e) {
                Toast.makeText(this, "this article cannot be share", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.view_web) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(newsUrl));
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
