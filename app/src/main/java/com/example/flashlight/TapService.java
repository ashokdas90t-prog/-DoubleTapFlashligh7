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

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        cameraManager =
                (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String[] ids = cameraManager.getCameraIdList();

            for (String id : ids) {
                Boolean flashAvailable =
                        cameraManager.getCameraCharacteristics(id)
                                .get(
                                        android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE
                                );

                if (Boolean.TRUE.equals(flashAvailable)) {
                    cameraId = id;
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();

        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

        // এই flag-এর মাধ্যমে Android touch gestures service-কে পাঠাবে
        info.flags =
                AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE;

        setServiceInfo(info);
    }

    @Override
    protected boolean onGesture(int gestureId) {

        // Android 11+ এর Double Tap gesture
        if (gestureId == 17) {
            toggleTorch();
            return true;
        }

        return super.onGesture(gestureId);
    }

    private void toggleTorch() {

        if (cameraManager == null || cameraId == null) {
            return;
        }

        try {
            torchOn = !torchOn;
            cameraManager.setTorchMode(cameraId, torchOn);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // এখানে কিছু করার দরকার নেই
    }

    @Override
    public void onInterrupt() {
    }
}
