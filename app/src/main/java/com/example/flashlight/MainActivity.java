package com.example.doubletapflashlight;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        textView.setText(
                "Double Tap Flashlight\n\n" +
                "ফোনে দুইবার ট্যাপ করলে Flashlight চালু/বন্ধ হবে।"
        );
        textView.setTextSize(22);
        textView.setPadding(40, 100, 40, 40);

        setContentView(textView);
    }
}
