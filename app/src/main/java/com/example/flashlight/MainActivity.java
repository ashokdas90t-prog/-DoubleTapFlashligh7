package com.example.doubletapflashlight;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 60, 40, 40);

        TextView title = new TextView(this);
        title.setText("Double Tap Flashlight");
        title.setTextSize(26);

        TextView info = new TextView(this);
        info.setText("প্রথমে Overlay Permission দিন, তারপর সার্ভিস চালু করুন।");
        info.setTextSize(18);
        info.setPadding(0, 30, 0, 30);

        Button permission = new Button(this);
        permission.setText("১. Overlay Permission দিন");

        Button start = new Button(this);
        start.setText("২. সার্ভিস চালু করুন");

        layout.addView(title);
        layout.addView(info);
        layout.addView(permission);
        layout.addView(start);

        setContentView(layout);

        permission.setOnClickListener(v -> {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                Intent intent = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())
                );
                startActivity(intent);
            }
        });

        start.setOnClickListener(v -> {
            Intent intent = new Intent(this, TapService.class);

            if (android.os.Build.VERSION.SDK_INT >= 26) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        });
    }
}
