package org.techconnect.activities;

/**
 * Created by tim on 9/29/17.
 */

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.ImageView;

import android.webkit.WebSettings;
import android.webkit.WebView;

import org.techconnect.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;


public class HTMLActivity extends AppCompatActivity {
    public static final String EXTRA_HTML = "htmlactivity.htmlFile";
    public static final String EXTRA_HTML_TITLE = "title";

    @Bind(R.id.webView)
    WebView webView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html);
        ButterKnife.bind(this);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        // Enable pinch to zoom without the zoom button
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            webView.getSettings().setDisplayZoomControls(false);
        }

        final String mimeType = "text/html";
        final String encoding = "UTF-8";

        if (getIntent().hasExtra(EXTRA_HTML) && getIntent().hasExtra(EXTRA_HTML_TITLE)) {
            webView.loadUrl(getIntent().getStringExtra(EXTRA_HTML));
            this.setTitle(getIntent().getStringExtra(EXTRA_HTML_TITLE));
        }

//        if (getIntent() != null && getIntent().hasExtra(EXTRA_IS_FILE)) {
//            if (getIntent().getBooleanExtra(EXTRA_IS_FILE, false)) {
//                String file = getIntent().getStringExtra(EXTRA_FILE);
//                System.out.println("FIRST: " + file);
//                String html = "IMAGE<br/><img src='file://" + file + "' />";
//                webView.loadDataWithBaseURL("", file, mimeType, encoding, "");
//            }
//        }


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
