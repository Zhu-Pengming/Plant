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
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.List;


public class WikiActivity extends AppCompatActivity {

    private String plantName;
    private PlantDetailsRepository plantDetailsRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wiki);
        plantDetailsRepository = new PlantDetailsRepository();

        // 读取Excel文件
        plantDetailsRepository.readExcelFile(this.getApplicationContext(), plantName);

        // 获取匹配的植物详情
        List<PlantDetails> plantDetailsList = plantDetailsRepository.getPlantDetailsList();


        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) WebView webView = findViewById(R.id.webViewWiki);
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

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) WebView returnHo = findViewById(R.id.wiki_return);
        returnHo.loadUrl("file:///android_asset/btn_return.html");
        returnHo.getSettings().setJavaScriptEnabled(true);
        returnHo.setWebViewClient(new WebViewClient());
        returnHo.setWebChromeClient(new WebChromeClient());
        returnHo.addJavascriptInterface(new WikiActivity.WebAppInterface(), "Android");


        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) WebView play = findViewById(R.id.wiki_play);
        play.loadUrl("file:///android_asset/playrecord.html");
        play.getSettings().setJavaScriptEnabled(true);
        play.setWebViewClient(new WebViewClient());
        play.setWebChromeClient(new WebChromeClient());
        play.addJavascriptInterface(new WikiActivity.WebAppInterface(), "Android");




        // 将 plantDetailsList 转换为 JSON 字符串
        Gson gson = new Gson();
        String plantDetailsJson = gson.toJson(plantDetailsList);

        WebView content = findViewById(R.id.wiki_content);
        content.getSettings().setJavaScriptEnabled(true);
        content.loadUrl("file:///android_asset/wiki.html");
        content.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:(function() { " +
                        "var plantDetailsList = JSON.parse('" + plantDetailsJson + "');" +
                        "var wrapperElements = document.querySelectorAll('.wrapper .hex'); " +
                        "for (var i = 0; i < wrapperElements.length; i++) { " +
                        "    if (plantDetailsList[i]) {" +
                        "        wrapperElements[i].innerText = plantDetailsList[i].hex; " +
                        "    }" +
                        "} " +
                        "})()");
            }
        });
    }

    public class WebAppInterface {

        @android.webkit.JavascriptInterface
        public void onPlay() {
            runOnUiThread(() -> {
                // Handle the play event here
                Toast.makeText(WikiActivity.this, "播放！", Toast.LENGTH_SHORT).show();
            });
        }

        @android.webkit.JavascriptInterface
        public void onPause() {
            runOnUiThread(() -> {
                // Handle the pause event here
                Toast.makeText(WikiActivity.this, "暂停！", Toast.LENGTH_SHORT).show();
            });
        }
        @android.webkit.JavascriptInterface
        public void onButtonClicked() {
            runOnUiThread(() -> {
                // Handle the button click event here
                Toast.makeText(WikiActivity.this, "按钮点击了！", Toast.LENGTH_SHORT).show();
            });
        }

        @android.webkit.JavascriptInterface
        public void onSwitchChanged(String status) {
            if ("ON".equals(status)) {
                Intent intent = new Intent(WikiActivity.this, CardsActivity.class);
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