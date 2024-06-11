package com.tom.npm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;


public class CardsActivity extends AppCompatActivity {

    private String species;

    private CardsDetailsRepository cardsDetailsRepository;

    private MediaPlayer mediaPlayer = null;

    private WebView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportActionBar() !=null) {
            getSupportActionBar().hide();
        }



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

        Button returnTo = findViewById(R.id.card_return);
        returnTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CardsActivity.this, MainActivity.class);
                overridePendingTransition(R.anim.default_anim_out, R.anim.default_anim_in);
                startActivity(intent);
            }
        });



        // Get the intent that started this activity
        Intent intent = getIntent();

// Get the species from the intent
        String species = intent.getStringExtra("species");

// Get the card details by species name
        CardsDetails cardDetails = CardsDetailsRepository.getCardDetailsByName(species);

        content = findViewById(R.id.card_content);
        content.getSettings().setJavaScriptEnabled(true);
        content.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);


                content.loadUrl("javascript:(function() { " +
                        "document.querySelector('.property-description h5').innerText = '" + cardDetails.getSpecies() + "';" +
                        "document.querySelector('.property-description p').innerText = '" + cardDetails.getDescriptions() + "';" +
                        "})()");
            }
        });
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
                Log.d("WebView", "onPlay called");
                // Handle the play event here
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer.create(CardsActivity.this, R.raw.sample);
                }
                mediaPlayer.start();
                Toast.makeText(CardsActivity.this, "播放！", Toast.LENGTH_SHORT).show();
            });
        }

        @android.webkit.JavascriptInterface
        public void onPause() {
            runOnUiThread(() -> {
                Log.d("WebView", "onPause called");
                // Handle the pause event here
                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                }
                Toast.makeText(CardsActivity.this, "暂停！", Toast.LENGTH_SHORT).show();
            });
        }


        @android.webkit.JavascriptInterface
        public void onSwitchChanged(String status) {
            if ("OFF".equals(status)) {
                Intent intent = new Intent(CardsActivity.this, WikiActivity.class);
                intent.putExtra("plantName", species);
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

    @Override
    protected void onPause() {
        super.onPause();
        // 保存WebView的状态
        Bundle bundle = new Bundle();
        content.saveState(bundle);
        SharedPreferences sharedPref = getSharedPreferences("WebViewState", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("cardsContent", new Gson().toJson(bundle));
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 恢复WebView的状态
        SharedPreferences sharedPref = getSharedPreferences("WebViewState", Context.MODE_PRIVATE);
        String webViewState = sharedPref.getString("cardsContent", null);
        if (webViewState != null) {
            Bundle bundle = new Gson().fromJson(webViewState, Bundle.class);
            content.restoreState(bundle);
        }
    }
}