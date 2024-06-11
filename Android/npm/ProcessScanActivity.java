package com.tom.npm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class ProcessScanActivity extends AppCompatActivity {

    private final String APIKey = "2b10MglNpUK4UBkH5myWDPWe";
    private PlantIdentificationUtil plantIdentificationUtil;

    private String identifyspiece;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_process_scan);

        if(getSupportActionBar() !=null) {
            getSupportActionBar().hide();
        }

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) WebView process = findViewById(R.id.process_web);
        process.loadUrl("file:///android_asset/scan_loader.html");

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) WebView loader = findViewById(R.id.process_load);
        loader.loadUrl("file:///android_asset/loading.html");

        Intent intent = getIntent();

        // Get the image Uri from the intent
        String imageUriString = intent.getStringExtra("imageUri");

        // Convert the Uri string to a Uri
        Uri imageUri = Uri.parse(imageUriString);

        if (imageUri != null) {
            // Convert the Uri to a Bitmap
            Bitmap imageBitmap = null;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (imageBitmap != null) {
                // Initialize the Retrofit service
                PlantIdentificationService service = RetrofitClientInstance.getRetrofitInstance(APIKey).create(PlantIdentificationService.class);
                plantIdentificationUtil = new PlantIdentificationUtil(service, APIKey);



                // Call identifyPlant method
                plantIdentificationUtil.identifyPlant(imageBitmap, new PlantIdentificationUtil.PlantIdentificationCallback() {
                    @Override
                    public void onSuccess(PlantIdentificationResponse response) {
                        runOnUiThread(() -> {


                            identifyspiece = response.getBestMatch();
                            Toast.makeText(ProcessScanActivity.this, "Plant identified: " + identifyspiece, Toast.LENGTH_LONG).show();
                            Log.d("plantIdentificationUtil","Plant identified: " + identifyspiece);

                            PlantDetailsRepository repository = new PlantDetailsRepository();



// 调用模糊匹配方法
                            PlantDetails matchedPlantDetails = repository.getPlantDetailsByEnglishName(identifyspiece);

// 打印匹配的植物详细信息
                            if (matchedPlantDetails != null) {
                                System.out.println(matchedPlantDetails.getSpecies());
                            } else {
                                System.out.println("No plant details found for the given English name.");
                            }

                            // Create an Intent to start WikiActivity
                            Intent wikiIntent = new Intent(ProcessScanActivity.this, WikiActivity.class);

                            // Put identifyspiece into the Intent
                            wikiIntent.putExtra("plantName", matchedPlantDetails.getSpecies());

                            // Start WikiActivity
                            startActivity(wikiIntent);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            Toast.makeText(ProcessScanActivity.this, error, Toast.LENGTH_LONG).show();
                        });
                    }
                });
            }
        }
    }
}