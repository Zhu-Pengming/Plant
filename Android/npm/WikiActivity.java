package com.tom.npm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;


public class WikiActivity extends AppCompatActivity {

    private String plantName;
    private PlantDetailsRepository plantDetailsRepository;

    private String species;
    private String isPoisonous;
    private String isFlower;
    private String isInvasive;
    private String plantType;
    private String growthCycle;
    private String plantingTime;
    private String function;
    private String englishName;


    private WebView defaultContent;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wiki);
        plantDetailsRepository = new PlantDetailsRepository();

        if(getSupportActionBar() !=null) {
            getSupportActionBar().hide();
        }

        // Get the intent that started this activity
        Intent intent = getIntent();



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

        Button returnTo = findViewById(R.id.wiki_return);
        returnTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WikiActivity.this, MainActivity.class);
                overridePendingTransition(R.anim.default_anim_out, R.anim.default_anim_in);
                startActivity(intent);
            }
        });


        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) WebView play = findViewById(R.id.wiki_play);
        play.loadUrl("file:///android_asset/playrecord.html");
        play.getSettings().setJavaScriptEnabled(true);
        play.setWebViewClient(new WebViewClient());
        play.setWebChromeClient(new WebChromeClient());
        play.addJavascriptInterface(new WikiActivity.WebAppInterface(), "Android");





        // Get plantName from the intent
        plantName = intent.getStringExtra("plantName");

        plantDetailsRepository = new PlantDetailsRepository();


        PlantDetails plantDetails = plantDetailsRepository.getPlantDetailsBySpecies(plantName);


        if (plantDetails != null) {
            species = plantDetails.getSpecies();
            isPoisonous = plantDetails.getIsPoisonous();
            isFlower = plantDetails.getIsFlower();
            isInvasive = plantDetails.getIsInvasive();
            plantType = plantDetails.getPlantType();
            growthCycle = plantDetails.getGrowthCycle();
            plantingTime = plantDetails.getPlantingTime();
            function = plantDetails.getFunction();
        } else {
            System.out.println("No plant details found for the given species.");
        }

        defaultContent = findViewById(R.id.wiki_content);
        defaultContent.getSettings().setJavaScriptEnabled(true);
        defaultContent.loadUrl("file:///android_asset/wiki.html");

        // 在HTML文件加载完成后，执行JavaScript函数来修改HTML内容
        defaultContent.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                String javascript = generateJavascript();
                defaultContent.evaluateJavascript(javascript, null);
            }
        });



    }
    private String generateJavascript() {
        // Generate the JavaScript code to modify the HTML content
        String javascript = "javascript:(function() { " +
                "document.querySelector('.heading').innerText = '" + species + "'; " +
                "document.querySelector('.black .hex').innerText = '" + isPoisonous + "'; " +
                "document.querySelector('.eerie-black .hex').innerText = '" + isFlower + "'; " +
                "document.querySelector('.chinese-black .hex').innerText = '" + isInvasive + "'; " +
                "document.querySelector('.night-rider .hex').innerText = '" + plantType + "'; " +
                "document.querySelector('.chinese-white .hex').innerText = '" + growthCycle + "'; " +
                "document.querySelector('.anti-flash-white .hex').innerText = '" + plantingTime + "'; " +
                "document.querySelector('.white .hex').innerText = '" + function + "'; " +
                "})()";
        return javascript;
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
        public void onSwitchChanged(String status) {
            if ("ON".equals(status)) {
                Intent intent = new Intent(WikiActivity.this, CardsActivity.class);
                intent.putExtra("species", species); // Add this line to pass 'species'
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
        defaultContent.saveState(bundle);
        SharedPreferences sharedPref = getSharedPreferences("WebViewState", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("wikiContent", new Gson().toJson(bundle));
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 恢复WebView的状态
        SharedPreferences sharedPref = getSharedPreferences("WebViewState", Context.MODE_PRIVATE);
        String webViewState = sharedPref.getString("wikiContent", null);
        if (webViewState != null) {
            Bundle bundle = new Gson().fromJson(webViewState, Bundle.class);
            defaultContent.restoreState(bundle);
        }
    }
}