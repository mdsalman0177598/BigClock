package com.example.bigclock;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }
    private SharedPreferences prefs;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL); root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(22), dp(30), dp(22), dp(22)); root.setBackgroundColor(0xFF090D11);

        TextView title = new TextView(this); title.setText("BIG CLOCK"); title.setTextColor(Color.WHITE);
        title.setTextSize(25); title.setGravity(Gravity.CENTER); title.setTypeface(Typeface.DEFAULT_BOLD);
        root.addView(title, new LinearLayout.LayoutParams(-1, dp(55)));
        TextView sub = new TextView(this); sub.setText("Choose your tall iPhone-style clock");
        sub.setTextColor(0xFFCCD3D8); sub.setTextSize(14); sub.setGravity(Gravity.CENTER);
        root.addView(sub, new LinearLayout.LayoutParams(-1, dp(42)));

        addStyle(root, "DARK GLASS", "dark", "Huge grey digits on a deep black background", 0xFF080B0E, 0xFF77797D);
        addStyle(root, "OCEAN GLASS", "ocean", "Huge translucent white digits over a sunset sea", 0xFF789CB9, 0xDDFEFFFF);

        TextView note = new TextView(this);
        note.setText("Select a style, then set it as your Live Wallpaper. The clock updates every second.");
        note.setTextColor(0xFFB9C2C9); note.setTextSize(13); note.setGravity(Gravity.CENTER);
        note.setPadding(0, dp(20), 0, dp(12)); root.addView(note, new LinearLayout.LayoutParams(-1, -2));
        setContentView(root);
    }

    private void addStyle(LinearLayout root, String titleText, String key, String desc, int bg, int fg) {
        LinearLayout card = new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER); card.setPadding(dp(12), dp(12), dp(12), dp(12)); card.setBackgroundColor(bg);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, dp(220)); cp.topMargin = dp(18); root.addView(card, cp);
        TextView title = new TextView(this); title.setText(titleText); title.setTextColor(Color.WHITE);
        title.setTextSize(13); title.setGravity(Gravity.CENTER); card.addView(title);
        TextView sample = new TextView(this); sample.setText("13:25"); sample.setTextColor(fg); sample.setGravity(Gravity.CENTER);
        sample.setTextSize(64); sample.setTypeface(Typeface.create("sans-serif-condensed-light", Typeface.NORMAL));
        card.addView(sample, new LinearLayout.LayoutParams(-1, dp(112)));
        TextView description = new TextView(this); description.setText(desc); description.setTextColor(0xFFE8EDF0);
        description.setTextSize(12); description.setGravity(Gravity.CENTER); card.addView(description);
        Button choose = new Button(this); choose.setText("Apply " + titleText);
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1, dp(48)); bp.topMargin = dp(8); card.addView(choose, bp);
        choose.setOnClickListener(v -> { prefs.edit().putString("clock_style", key).apply(); openWallpaperPicker(); });
    }

    private void openWallpaperPicker() {
        Intent i = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        i.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(this, ClockWallpaperService.class));
        try { startActivity(i); }
        catch (Exception e) { startActivity(new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)); }
    }
}
