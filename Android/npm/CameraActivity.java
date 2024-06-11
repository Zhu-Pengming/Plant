package com.tom.npm;

import static com.tom.npm.ChatActivity.OPEN_GALLERY_REQUEST_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.hardware.camera2.*;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class CameraActivity extends AppCompatActivity {
    private SetPreviewAndCapture setPreviewAndCapture;

    private int currentCameraId = CameraCharacteristics.LENS_FACING_FRONT;//手机后面的摄像头

    private ResizeAbleSurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Size previewSize;//图片尺寸
    private android.util.Size mWinSize;//获取屏幕的尺寸
    private ImageReader imageReader;//接受图片数据

    private CameraManager cameraManager;
    private CameraDevice cameraDevice;

    private HandlerThread handlerThread;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        if(getSupportActionBar() !=null) {
            getSupportActionBar().hide();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // 权限已经被授予
            initView();
            setupListeners();
        } else {
            // 权限未被授予，请求权限
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            // 向用户展示一个对话框来解释为什么需要这项权限
            new AlertDialog.Builder(this)
                    .setTitle("需要相机权限")
                    .setMessage("此应用需要相机权限来拍摄照片和录制视频。请授权使用相机。")
                    .setPositiveButton("确定", (dialog, which) -> {
                        // 重新请求权限
                        ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    })
                    .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        } else {
            // 第一次请求权限或用户在过去拒绝过权限请求且未选择“不再询问”，直接请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，初始化视图
                initView();
                setupListeners();
            } else {
                // 权限被拒绝，向用户解释无法使用相机的影响
                Toast.makeText(this, "没有相机权限，应用无法使用相机功能。", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static final int REQUEST_CAMERA_PERMISSION = 1;




    private void setupListeners() {
        ImageView picture = findViewById(R.id.btnTakePhoto);
        ImageView change = findViewById(R.id.btnSwitch);

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setPreviewAndCapture == null) {
                    Log.e("CameraActivity", "setPreviewAndCapture is not initialized");
                    return;
                }
                setPreviewAndCapture.takePhoto();
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });

        ImageView returnTo = findViewById(R.id.camera_return);
        returnTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraActivity.this, MainActivity.class);
                overridePendingTransition(R.anim.default_anim_out, R.anim.default_anim_in);
                startActivity(intent);
            }
        });

        ImageView instruction = findViewById(R.id.camera_instruction1);
        instruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInstructionFragment();
            }
        });

        ImageView gallery = findViewById(R.id.camera_gallery1);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理相册事件
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, OPEN_GALLERY_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case OPEN_GALLERY_REQUEST_CODE:
                    handleGalleryResult(data);
                    break;
            }
        }
    }

    private Uri uri;

    private void handleGalleryResult(Intent data) {
        uri = data.getData();
        if (uri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap image = BitmapFactory.decodeStream(inputStream);


                Intent processScanIntent = new Intent(CameraActivity.this, ProcessScanActivity.class);
                processScanIntent.putExtra("imageUri", uri.toString()); // Pass the image Uri to the ProcessScanActivity
                overridePendingTransition(R.anim.default_anim_in, R.anim.default_anim_out);
                startActivity(processScanIntent);

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void showInstructionFragment() {
        InstructionFragment instructionFragment = new InstructionFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, instructionFragment)
                .addToBackStack(null)  // 添加到后退栈以便用户可以通过后退按钮返回
                .commit();
        overridePendingTransition(R.anim.instruction_in, R.anim.instruction_out);

        // 隐藏其他视图
        hideOtherViews();
    }

    private void hideOtherViews() {
        findViewById(R.id.surfaceView).setVisibility(View.GONE);
        findViewById(R.id.camera_return).setVisibility(View.GONE);
        findViewById(R.id.btnSwitch).setVisibility(View.GONE);
        findViewById(R.id.btnTakePhoto).setVisibility(View.GONE);
        findViewById(R.id.camera_gallery1).setVisibility(View.GONE);
        findViewById(R.id.camera_gallery2).setVisibility(View.GONE);
        findViewById(R.id.camera_instruction1).setVisibility(View.GONE);
        findViewById(R.id.camera_instruction2).setVisibility(View.GONE);
    }



    /**
     * 加载布局，初始化组件
     */
    @SuppressLint("WrongViewCast")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initView(){

        View view = findViewById(R.id.surfaceView);
        Log.d("CameraActivity", "View class: " + view.getClass().getSimpleName());
        surfaceView = (ResizeAbleSurfaceView) view;


        mWinSize = Utils.loadWinSize(this);
        if (mWinSize == null) {
            Log.e("CameraActivity", "Failed to load window size"); // Optionally return or handle this case
        }

        //surfaceView.resize(1080,1080);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                //打开相机同时开启预览
                setAndOpenCamera();
                //解决预览拉升
                int height = surfaceView.getHeight();
                int width = surfaceView.getWidth();
                if (height > width) {
                    //正常情况，竖屏
                    float justH = width * 4.f / 3;
                    //设置View在水平方向的缩放比例,保证宽高比为3:4
                    surfaceView.setScaleX(height / justH);
                } else {
                    float justW = height * 4.f / 3;
                    surfaceView.setScaleY(width / justW);
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                //关闭相机释放资源
                closeCamera();
            }
        });

        //获取相机管理
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        //开启子线程，处理某些耗时操作
        handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }



    private void setAndOpenCamera() {
        try {
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(String.valueOf(currentCameraId));
            StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (map == null) {
                Log.e("CameraActivity", "No StreamConfigurationMap available");
                return; // Early exit if map is not available
            }

            previewSize = Utils.fitPhotoSize(map, mWinSize);
            if (previewSize == null) {
                Log.e("CameraActivity", "No valid preview size");
                return; // Early exit if no valid size is determined
            }

            imageReader = ImageReader.newInstance(previewSize.getWidth(), previewSize.getHeight(), ImageFormat.JPEG, 2);
            imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    handler.post(new ImageSaver(reader.acquireNextImage(), CameraActivity.this, new ImageSaver.ImageSaveCallback() {
                        @Override
                        public void onImageSaved(final Uri uri) {
                            // The image has been saved, do something with the Uri
                            Log.d("CameraActivity", "Image saved with URI: " + uri.toString());

                            // Run the dialog creation on the UI thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Create an ImageView to display the image
                                    ImageView imageView = new ImageView(CameraActivity.this);
                                    imageView.setImageURI(uri);

                                    AlertDialog builder = new AlertDialog.Builder(CameraActivity.this)
                                            .setTitle(" 使用这张图片 ？ ")
                                            .setView(imageView) // Add the ImageView to the dialog
                                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // User chose to use the image, start the ProcessScanActivity
                                                    Intent processScanIntent = new Intent(CameraActivity.this, ProcessScanActivity.class);
                                                    processScanIntent.putExtra("imageUri", uri.toString()); // Pass the image Uri to the ProcessScanActivity
                                                    overridePendingTransition(R.anim.default_anim_in, R.anim.default_anim_out);
                                                    startActivity(processScanIntent);
                                                }
                                            })
                                            .setNegativeButton("否", null) // User chose not to use the image, do nothing
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();

                                    builder.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(26);
                                    builder.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(26);

                                    builder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                                    builder.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.WHITE);



                                }
                            });
                        }
                    }));
                }
            }, handler);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return; // Check permissions again
            }

            cameraManager.openCamera(String.valueOf(currentCameraId), stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (surfaceView == null) {
            surfaceView = (ResizeAbleSurfaceView) findViewById(R.id.surfaceView);
            initView();
        }
    }

    /**
     * 打开相机后的状态回调，获取CameraDevice对象
     */
    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            //打开相机后开启预览，以及拍照的工具类,主要是将CameraDevice对象传递进工具类
            setPreviewAndCapture = new SetPreviewAndCapture(cameraDevice, surfaceHolder,
                    imageReader, handler,CameraActivity.this, previewSize);
            setPreviewAndCapture.startPreview();


        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
            cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
            cameraDevice = null;
            finish();
        }
    };

    /**
     * 切换前后摄像头的方法
     */
    private void switchCamera(){
        try{
            for (String cameraId : cameraManager.getCameraIdList()){
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                //判断当前摄像头为后置摄像头，且存在前置摄像头
                if (currentCameraId == CameraCharacteristics.LENS_FACING_FRONT &&
                        cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK){
                    //后置转前置
                    currentCameraId = CameraCharacteristics.LENS_FACING_BACK;
                    //重新打开相机
                    cameraDevice.close();
                    setAndOpenCamera();
                    break;
                }else if (currentCameraId == CameraCharacteristics.LENS_FACING_BACK &&
                        cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT){
                    //前置转后置
                    currentCameraId = CameraCharacteristics.LENS_FACING_FRONT;
                    cameraDevice.close();
                    setAndOpenCamera();
                    break;
                }
            }
        }catch(CameraAccessException e){
            e.printStackTrace();
        }
    }

    /**
     * 关闭相机
     */
    private void closeCamera() {

        //关闭相机
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        //关闭拍照处理器
        if (imageReader != null) {
            imageReader.close();
            imageReader = null;
        }
    }


}