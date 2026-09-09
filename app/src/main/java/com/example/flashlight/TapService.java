package com.example.flashlight;

import android.app.*;
import android.content.*;
import android.graphics.PixelFormat;
import android.hardware.camera2.*;
import android.os.*;
import android.provider.Settings;
import android.view.*;
import java.util.*;

public class TapService extends Service {
    WindowManager wm;
    View overlay;
    long last = 0;
    boolean torch = false;
    String camId;

    public void onCreate() {
        super.onCreate();
        createChannel();

        startForeground(7, new Notification.Builder(this, "tapflash")
                .setContentTitle("Double Tap Flashlight")
                .setContentText("Double tap gesture service is running")
                .setSmallIcon(android.R.drawable.ic_menu_camera)
                .build());

        CameraManager cm = (CameraManager) getSystemService(CAMERA_SERVICE);

        try {
            for (String id : cm.getCameraIdList()) {
                CameraCharacteristics c =
                        cm.getCameraCharacteristics(id);

                Boolean f = c.get(
                        CameraCharacteristics.FLASH_INFO_AVAILABLE
                );

                if (Boolean.TRUE.equals(f)) {
                    camId = id;
                    break;
                }
            }
        } catch (Exception ignored) {}

        if (Build.VERSION.SDK_INT >= 23 &&
                Settings.canDrawOverlays(this)) {
            addOverlay();
        }
    }

    void addOverlay() {
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        overlay = new View(this) {
            public boolean onTouchEvent(
                    android.view.MotionEvent e) {

                if (e.getAction() == MotionEvent.ACTION_UP) {
                    long now = System.currentTimeMillis();

                    if (now - last < 450) {
                        toggleTorch();
                        last = 0;
                    } else {
                        last = now;
                    }
                }

                return true;
            }
        };

        int type;

        if (Build.VERSION.SDK_INT >= 26)
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        else
            type = WindowManager.LayoutParams.TYPE_PHONE;

        WindowManager.LayoutParams p =
                new WindowManager.LayoutParams(
                        1,
                        1,
                        type,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                        PixelFormat.TRANSLUCENT
                );

        p.gravity = Gravity.TOP | Gravity.LEFT;

        try {
            wm.addView(overlay, p);
        } catch (Exception ignored) {}
    }

    void toggleTorch() {
        if (camId == null) return;

        try {
            CameraManager cm =
                    (CameraManager) getSystemService(CAMERA_SERVICE);

            torch = !torch;
            cm.setTorchMode(camId, torch);

        } catch (Exception e) {
            torch = !torch;
        }
    }

    void createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {

            NotificationChannel ch =
                    new NotificationChannel(
                            "tapflash",
                            "Double Tap Flashlight",
                            NotificationManager.IMPORTANCE_LOW
                    );

            ((NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE))
                    .createNotificationChannel(ch);
        }
    }

    public int onStartCommand(Intent i, int f, int id) {
        return START_STICKY;
    }

    public void onDestroy() {
        if (wm != null && overlay != null)
            try {
                wm.removeView(overlay);
            } catch (Exception ignored) {}

        super.onDestroy();
    }

    public android.os.IBinder onBind(Intent i) {
        return null;
    }
}
