package com.example.bigclock;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private int dp(float v) {
        return (int)(v * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(24), dp(36), dp(24), dp(24));
        root.setBackgroundColor(Color.rgb(18, 18, 28));

        TextView title = new TextView(this);
        title.setText("BIG CLOCK");
        title.setTextColor(Color.WHITE);
        title.setTextSize(27);
        title.setGravity(Gravity.CENTER);
        title.setLetterSpacing(0.10f);
        root.addView(title, new LinearLayout.LayoutParams(-1, dp(58)));

        TextView subtitle = new TextView(this);
        subtitle.setText("iPhone-inspired Lock Screen Wallpaper");
        subtitle.setTextColor(0xFFCFCAE8);
        subtitle.setTextSize(15);
        subtitle.setGravity(Gravity.CENTER);
        root.addView(subtitle, new LinearLayout.LayoutParams(-1, dp(42)));

        TextView preview = new TextView(this);
        preview.setText("10:11\\n\\nMONDAY, 23 NOVEMBER");
        preview.setTextColor(Color.WHITE);
        preview.setTextSize(29);
        preview.setGravity(Gravity.CENTER);
        preview.setBackgroundColor(0xFF7556C8);
        LinearLayout.LayoutParams pp = new LinearLayout.LayoutParams(-1, dp(230));
        pp.topMargin = dp(28);
        pp.bottomMargin = dp(24);
        root.addView(preview, pp);

        TextView info = new TextView(this);
        info.setText("এটি শুধু Lock Screen-এর Live Wallpaper। Home Screen Launcher বা App Dock পরিবর্তন করবে না। Set wallpaper চাপার পর Android-এর system preview-তে Lock screen অপশন থাকলে সেটি নির্বাচন করো।");
        info.setTextColor(0xFFE2E0EA);
        info.setTextSize(15);
        info.setGravity(Gravity.CENTER);
        info.setLineSpacing(dp(4), 1f);
        root.addView(info, new LinearLayout.LayoutParams(-1, -2));

        Button set = new Button(this);
        set.setText("Set Lock Screen Wallpaper");
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1, dp(54));
        bp.topMargin = dp(28);
        root.addView(set, bp);
        set.setOnClickListener(v -> openWallpaperPicker());

        TextView note = new TextView(this);
        note.setText("No special permissions required");
        note.setTextColor(0xFFAAA5B9);
        note.setTextSize(12);
        note.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams np = new LinearLayout.LayoutParams(-1, dp(42));
        root.addView(note, np);

        setContentView(root);
    }

    private void openWallpaperPicker() {
        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(this, ClockWallpaperService.class));
        try {
            startActivity(intent);
        } catch (Exception e) {
            Intent fallback = new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
            startActivity(fallback);
        }
    }
}
