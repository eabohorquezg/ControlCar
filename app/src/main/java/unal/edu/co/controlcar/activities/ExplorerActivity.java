package unal.edu.co.controlcar.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import unal.edu.co.controlcar.R;

public class ExplorerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);

        String url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setUrl(url);
    }

    private void setUrl(String url) {
        WebView webview = (WebView) findViewById(R.id.webExplorer);
        //webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(url);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
