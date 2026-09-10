package com.example.doubletapflashlight;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
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
        info.setText("নিচের বোতামে চাপ দিয়ে Accessibility Service চালু করুন।");
        info.setTextSize(18);
        info.setPadding(0, 30, 0, 30);

        Button accessibility = new Button(this);
        accessibility.setText("Accessibility Service চালু করুন");

        layout.addView(title);
        layout.addView(info);
        layout.addView(accessibility);

        setContentView(layout);

        accessibility.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        });
    }
}
