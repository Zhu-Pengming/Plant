package com.example.npm;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageSaver implements Runnable {
    private Image mImage;
    private Context context;

    private final ImageSaveCallback callback;




    public ImageSaver(Image image, Context context, ImageSaveCallback callback) {
        this.mImage = image;
        this.context = context;
        this.callback = callback;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
        ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        Bitmap img = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = timeStamp + ".jpg";
        saveImageToPublic(context, fileName, bytes, "Pictures",callback);
        Log.d("hhhhh","hhhhh");
        mImage.close();
    }

    public static void saveImageToPublic(Context context, String fileName, byte[] image, String subDir,ImageSaveCallback callback) {
        Log.d("hhhhh","hhhhh");
        String subDirection = TextUtils.isEmpty(subDir) ? "DCIM" : subDir.endsWith("/") ? subDir.substring(0, subDir.length() - 1) : subDir;

        try {
            Log.d("hhhhh","hhhhh");
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, subDirection);
            } else {
                contentValues.put(MediaStore.Images.Media.DATA, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath());
            }
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            Log.d("hhhhh","hhhhh");
            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (uri != null) {
                try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri)) {
                    if (outputStream != null) {
                        outputStream.write(image);
                        outputStream.flush();
                        outputStream.close();
                        callback.onImageSaved(uri);
                        Log.d("ImageSaver", "Success");
                    }
                } catch (IOException e) {
                    Log.e("ImageSaver", "Error saving image: ", e);
                }
            }
        } catch (Exception e) {
            Log.e("ImageSaver", "Failed to save image: ", e);
        }
    }
    public interface ImageSaveCallback {
        void onImageSaved(Uri uri);
    }
}

