package com.example.npm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.hardware.camera2.*;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        requestCameraPermission();

        //动态获取权限
        List<String> permissionList = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }

        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(CameraActivity.this, permissions, 1);
        } else {
            initView();
        }

        //为按钮绑定点击事件
        ImageView picture = findViewById(R.id.btnTakePhoto);
        ImageView change = findViewById(R.id.btnSwitch);

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPreviewAndCapture.takePhoto();
            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });


        ImageView instruction = findViewById(R.id.camera_instruction1);
        instruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InstructionFragment instructionFragment = new InstructionFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, instructionFragment)
                        .addToBackStack(null) // Add this transaction to the back stack
                        .commit();
                overridePendingTransition(R.anim.instruction_in, R.anim.instruction_out);
                // Hide other views
                findViewById(R.id.surfaceView).setVisibility(View.GONE);
                findViewById(R.id.camera_return).setVisibility(View.GONE);
                findViewById(R.id.btnSwitch).setVisibility(View.GONE);
                findViewById(R.id.btnTakePhoto).setVisibility(View.GONE);
                findViewById(R.id.camera_gallery1).setVisibility(View.GONE);
                findViewById(R.id.camera_gallery2).setVisibility(View.GONE);
                findViewById(R.id.camera_instruction1).setVisibility(View.GONE);
                findViewById(R.id.camera_instruction2).setVisibility(View.GONE);
            }
        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                initView();
            } else {
                // 使用对话框提供更明确的反馈
                showDialogToGetPermission();
            }
        }
    }

    private void showDialogToGetPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.dialog_permission, null);
        builder.setView(customLayout);

        AlertDialog dialog = builder.create();

        // 设置按钮的点击事件
        customLayout.findViewById(R.id.btn_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAppSettings();
                dialog.dismiss();
            }
        });

        customLayout.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
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

    private static final int REQUEST_CAMERA_PERMISSION = 1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
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
                    handler.post(new ImageSaver(reader.acquireNextImage(), CameraActivity.this));
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