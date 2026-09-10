package com.example.doubletapflashlight;
import android.view.View;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;

public class TapService extends Service {

    private WindowManager windowManager;
    private View overlay;
    private CameraManager cameraManager;
    private String cameraId;
    private boolean torchOn = false;
    private long lastTapTime = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        Notification notification =
                new Notification.Builder(this, "tapflash")
                        .setContentTitle("Double Tap Flashlight")
                        .setContentText("Double tap service is running")
                        .setSmallIcon(android.R.drawable.ic_menu_camera)
                        .build();

        startForeground(7, notification);

        cameraManager =
                (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            for (String id : cameraManager.getCameraIdList()) {
                cameraId = id;
                break;
            }
        } catch (Exception ignored) {
        }

        if (Build.VERSION.SDK_INT >= 23 &&
                Settings.canDrawOverlays(this)) {
            addOverlay();
        }
    }

    private void addOverlay() {
        windowManager =
                (WindowManager) getSystemService(WINDOW_SERVICE);

        overlay = new View(this) {
            @Override
            public boolean onTouchEvent(MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    long now = System.currentTimeMillis();

                    if (now - lastTapTime < 400) {
                        toggleTorch();
                        lastTapTime = 0;
                    } else {
                        lastTapTime = now;
                    }
                }

                return true;
            }
        };

        int type;

        if (Build.VERSION.SDK_INT >= 26) {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        WindowManager.LayoutParams params =
                new WindowManager.LayoutParams(
                        80,
                        80,
                        type,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT
                );

        params.gravity = Gravity.TOP | Gravity.LEFT;

        try {
            windowManager.addView(overlay, params);
        } catch (Exception ignored) {
        }
    }

    private void toggleTorch() {

        if (cameraId == null || cameraManager == null) {
            return;
        }

        try {
            torchOn = !torchOn;
            cameraManager.setTorchMode(cameraId, torchOn);
        } catch (Exception ignored) {
        }
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= 26) {

            NotificationChannel channel =
                    new NotificationChannel(
                            "tapflash",
                            "Double Tap Flashlight",
                            NotificationManager.IMPORTANCE_LOW
                    );

            NotificationManager manager =
                    (NotificationManager)
                            getSystemService(NOTIFICATION_SERVICE);

            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        if (windowManager != null && overlay != null) {
            try {
                windowManager.removeView(overlay);
            } catch (Exception ignored) {
            }
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
