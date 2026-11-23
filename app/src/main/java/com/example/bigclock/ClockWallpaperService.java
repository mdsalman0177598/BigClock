package com.example.bigclock;

import android.graphics.*;
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ClockWallpaperService extends WallpaperService {
    @Override public Engine onCreateEngine() { return new ClockEngine(); }

    class ClockEngine extends Engine {
        final Handler h = new Handler(Looper.getMainLooper());
        final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        final Runnable r = this::draw;
        boolean visible;

        @Override public void onVisibilityChanged(boolean v) {
            visible = v; h.removeCallbacks(r); if (v) draw();
        }
        @Override public void onSurfaceChanged(SurfaceHolder s, int f, int w, int hh) {
            super.onSurfaceChanged(s, f, w, hh); draw();
        }
        @Override public void onDestroy() { visible = false; h.removeCallbacks(r); }

        void draw() {
            SurfaceHolder sh = getSurfaceHolder();
            Canvas c = null;
            try { c = sh.lockCanvas(); if (c != null) paint(c); }
            catch (Exception e) { }
            finally { if (c != null) try { sh.unlockCanvasAndPost(c); } catch (Exception e) { } }
            h.removeCallbacks(r);
            if (visible) h.postDelayed(r, 1000);
        }

        void paint(Canvas c) {
            int w = c.getWidth(), ht = c.getHeight();
            p.setShader(new LinearGradient(0, 0, w, ht,
                new int[]{0xFF5B6CFF, 0xFFC96BD6, 0xFFFFB48A}, null, Shader.TileMode.CLAMP));
            c.drawRect(0, 0, w, ht, p);
            p.setShader(null);
            Date now = new Date();
            p.setTextAlign(Paint.Align.CENTER);
            p.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
            p.setColor(0xF2FFFFFF);
            float size = w * 0.62f;
            p.setTextSize(size);
            float y = ht * 0.30f + size * 0.8f;
            c.drawText(new SimpleDateFormat("hh", Locale.US).format(now), w / 2f, y, p);
            c.drawText(new SimpleDateFormat("mm", Locale.US).format(now), w / 2f, y + size * 0.85f, p);
            p.setTextSize(w * 0.06f);
            p.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            p.setColor(0xCCFFFFFF);
            c.drawText(new SimpleDateFormat("EEEE, d MMMM", Locale.ENGLISH).format(now), w / 2f, ht * 0.30f, p);
        }
    }
}
