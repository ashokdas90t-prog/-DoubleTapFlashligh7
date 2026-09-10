package com.example.doubletapflashlight;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.view.accessibility.AccessibilityEvent;

public class TapService extends AccessibilityService {

    private CameraManager cameraManager;
    private String cameraId;
    private boolean torchOn = false;
    private long lastTap = 0;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            for (String id : cameraManager.getCameraIdList()) {
                cameraId = id;
                break;
            }
        } catch (Exception ignored) {
        }

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        long now = System.currentTimeMillis();

        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            if (now - lastTap < 400) {
                toggleTorch();
                lastTap = 0;
            } else {
                lastTap = now;
            }
        }
    }

    private void toggleTorch() {
        if (cameraManager == null || cameraId == null) return;

        try {
            torchOn = !torchOn;
            cameraManager.setTorchMode(cameraId, torchOn);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onInterrupt() {
    }
}
