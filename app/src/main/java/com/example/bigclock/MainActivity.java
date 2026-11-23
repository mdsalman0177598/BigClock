package com.example.bigclock;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(22), dp(28), dp(22), dp(22));
        root.setBackgroundColor(Color.rgb(28, 25, 66));

        TextView title = new TextView(this);
        title.setText("BIG CLOCK");
        title.setTextColor(Color.WHITE);
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);
        title.setLetterSpacing(0.12f);
        root.addView(title, new LinearLayout.LayoutParams(-1, dp(52)));

        TextView subtitle = new TextView(this);
        subtitle.setText("iPhone-inspired clock wallpaper");
        subtitle.setTextColor(0xFFDCD7FF);
        subtitle.setTextSize(14);
        subtitle.setGravity(Gravity.CENTER);
        root.addView(subtitle, new LinearLayout.LayoutParams(-1, dp(34)));

        LinearLayout preview = new LinearLayout(this);
        preview.setOrientation(LinearLayout.VERTICAL);
        preview.setGravity(Gravity.CENTER);
        preview.setBackgroundColor(0xFF7956C9);
        LinearLayout.LayoutParams previewParams =
                new LinearLayout.LayoutParams(-1, dp(330));
        previewParams.topMargin = dp(28);
        previewParams.bottomMargin = dp(24);
        root.addView(preview, previewParams);

        TextView time = new TextView(this);
        time.setText(new SimpleDateFormat("h:mm", Locale.ENGLISH).format(new Date()));
        time.setTextColor(Color.WHITE);
        time.setTextSize(66);
        time.setGravity(Gravity.CENTER);
        time.setTypeface(android.graphics.Typeface.create(
                "sans-serif-thin", android.graphics.Typeface.NORMAL));
        preview.addView(time, new LinearLayout.LayoutParams(-1, dp(112)));

        TextView date = new TextView(this);
        date.setText(new SimpleDateFormat(
                "EEEE, d MMMM", Locale.ENGLISH).format(new Date()).toUpperCase(Locale.ENGLISH));
        date.setTextColor(0xFFF7F3FF);
        date.setTextSize(16);
        date.setGravity(Gravity.CENTER);
        date.setTypeface(android.graphics.Typeface.create(
                "sans-serif-medium", android.graphics.Typeface.NORMAL));
        preview.addView(date, new LinearLayout.LayoutParams(-1, dp(42)));

        TextView info = new TextView(this);
        info.setText("Set this live wallpaper from Android's wallpaper preview. Your phone may offer Home screen, Lock screen, or both depending on its software.");
        info.setTextColor(0xFFE8E5F5);
        info.setTextSize(14);
        info.setGravity(Gravity.CENTER);
        info.setLineSpacing(dp(4), 1f);
        root.addView(info, new LinearLayout.LayoutParams(-1, -2));

        Button set = new Button(this);
        set.setText("Set Clock Wallpaper");
        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(-1, dp(54));
        buttonParams.topMargin = dp(24);
        root.addView(set, buttonParams);
        set.setOnClickListener(v -> openWallpaperPicker());

        setContentView(root);
    }

    private void openWallpaperPicker() {
        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(this, ClockWallpaperService.class));
        try {
            startActivity(intent);
        } catch (Exception error) {
            startActivity(new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER));
        }
    }
}
