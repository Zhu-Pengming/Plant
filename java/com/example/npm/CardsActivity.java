package com.example.npm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class CardsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cards);
        WebView webView = findViewById(R.id.webViewCard);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new WebAppInterface(), "Android");
        webView.loadUrl("file:///android_asset/switch.html");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                SharedPreferences sharedPref = getSharedPreferences("SwitchState", Context.MODE_PRIVATE);
                String switchState = sharedPref.getString("state", "OFF");
                webView.loadUrl("javascript:setSwitchState('" + switchState + "')");
            }
        });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) WebView returnTo = findViewById(R.id.card_return);
        returnTo.getSettings().setJavaScriptEnabled(true);
        returnTo.setWebViewClient(new WebViewClient());
        returnTo.setWebChromeClient(new WebChromeClient());
        returnTo.addJavascriptInterface(new WebAppInterface(), "Android");
        returnTo.loadUrl("file:///android_asset/btn_return.html");


        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) WebView content = findViewById(R.id.card_content);
        content.loadUrl("file:///android_asset/cards_content.html");


        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) WebView play2 = findViewById(R.id.card_play);
        play2.loadUrl("file:///android_asset/playrecord.html");
        play2.getSettings().setJavaScriptEnabled(true);
        play2.setWebViewClient(new WebViewClient());
        play2.setWebChromeClient(new WebChromeClient());
        play2.addJavascriptInterface(new CardsActivity.WebAppInterface(), "Android");
    }

    public class WebAppInterface {

        @android.webkit.JavascriptInterface
        public void onPlay() {
            runOnUiThread(() -> {
                // Handle the play event here
                Toast.makeText(CardsActivity.this, "播放！", Toast.LENGTH_SHORT).show();
            });
        }

        @android.webkit.JavascriptInterface
        public void onPause() {
            runOnUiThread(() -> {
                // Handle the pause event here
                Toast.makeText(CardsActivity.this, "暂停！", Toast.LENGTH_SHORT).show();
            });
        }
        @android.webkit.JavascriptInterface
        public void onButtonClicked() {
            runOnUiThread(() -> {
                // Handle the button click event here
                Toast.makeText(CardsActivity.this, "按钮点击了！", Toast.LENGTH_SHORT).show();
            });
        }

        @android.webkit.JavascriptInterface
        public void onSwitchChanged(String status) {
            if ("OFF".equals(status)) {
                Intent intent = new Intent(CardsActivity.this, WikiActivity.class);
                overridePendingTransition(R.anim.default_anim_in, R.anim.default_anim_out);
                startActivity(intent);
            }
        }

        @android.webkit.JavascriptInterface
        public void saveSwitchState(String status) {
            SharedPreferences sharedPref = getSharedPreferences("SwitchState", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("state", status);
            editor.apply();
        }
    }
}