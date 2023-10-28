package com.droidev.wwm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private TinyDB tinyDB;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("WhatsApp Web Mobile");

        tinyDB = new TinyDB(this);

        webView = (WebView) findViewById(R.id.webView);

        // Enable JavaScript in the WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);

        // Enable caching
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // Set the user agent to mimic a desktop browser
        String desktopUserAgent = tinyDB.getString("userAgent");
        webSettings.setUserAgentString(desktopUserAgent);

        webView.setWebViewClient(new MyWebViewClient());

        // Load WhatsApp web page in the WebView
        webView.loadUrl("https://web.whatsapp.com");

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            // Load the initial URL if no saved state is available
            webView.loadUrl("https://web.whatsapp.com");
        }

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            // Get the URL from the request
            Uri uri = request.getUrl();

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true; // Prevent the WebView from loading this URL
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.userAgent) {
            setUserAgent();
            return true;
        }

        if (id == R.id.reload) {

            webView.reload();
            return true;
        }

        if (id == R.id.getUserAgents) {

            String url = "https://www.whatismybrowser.com/guides/the-latest-user-agent/windows";

            Uri uri = Uri.parse(url);

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setUserAgent() {

        EditText editText = new EditText(this);

        LinearLayout lay = new LinearLayout(this);
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.addView(editText);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Set User Agent")
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .setView(lay)
                .show();

        editText.setText(tinyDB.getString("userAgent"));

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        positiveButton.setOnClickListener(v -> {

            tinyDB.putString("userAgent", editText.getText().toString());

            Toast.makeText(this, "User Agent is set.", Toast.LENGTH_SHORT).show();

            reloadWithNewUserAgent();

            dialog.dismiss();
        });
    }

    public void reloadWithNewUserAgent() {

        webView.getSettings().setUserAgentString(tinyDB.getString("userAgent"));
        webView.reload();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }
}