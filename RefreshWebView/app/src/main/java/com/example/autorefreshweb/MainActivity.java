package com.example.autorefreshweb;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private Handler refreshHandler;
    private Runnable refreshRunnable;
    private static final String PREFS_NAME = "WebViewPrefs";
    private static final String URL_KEY = "saved_url";
    private static final int REFRESH_INTERVAL = 30000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setFullScreen();
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        setupWebView();

        String savedUrl = getSavedUrl();
        if (savedUrl == null || savedUrl.isEmpty()) {
            showUrlInputDialog();
        } else {
            loadUrl(savedUrl);
        }

        setupAutoRefresh();
    }

    private void setFullScreen() {
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    private void showUrlInputDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_input_url, null);
        EditText urlInput = dialogView.findViewById(R.id.urlInput);

        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("输入网址")
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton("保存", null)
            .create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String url = urlInput.getText().toString().trim();
                
                if (url.isEmpty()) {
                    Toast.makeText(this, "请输入网址", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + url;
                }
                
                saveUrl(url);
                loadUrl(url);
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void loadUrl(String url) {
        webView.loadUrl(url);
    }

    private void setupAutoRefresh() {
        refreshHandler = new Handler();
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                if (webView != null) {
                    webView.reload();
                }
                refreshHandler.postDelayed(this, REFRESH_INTERVAL);
            }
        };
        refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
    }

    private String getSavedUrl() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(URL_KEY, null);
    }

    private void saveUrl(String url) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(URL_KEY, url).apply();
        Toast.makeText(this, "网址已保存", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (refreshHandler != null && refreshRunnable != null) {
            refreshHandler.removeCallbacks(refreshRunnable);
        }
        if (webView != null) {
            webView.destroy();
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}