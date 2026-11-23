package com.example.bigclock;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.LinearGradient;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ClockWallpaperService extends WallpaperService {
    @Override public Engine onCreateEngine() { return new ClockEngine(); }

    private class ClockEngine extends Engine {
        private final Handler handler = new Handler(Looper.getMainLooper());
        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        private boolean visible;
        private final Runnable drawTask = new Runnable() { @Override public void run() { drawFrame(); } };

        @Override public void onVisibilityChanged(boolean isVisible) {
            visible = isVisible;
            handler.removeCallbacks(drawTask);
            if (visible) drawFrame();
        }
        @Override public void onSurfaceChanged(SurfaceHolder h, int f, int w, int he) {
            super.onSurfaceChanged(h, f, w, he); drawFrame();
        }
        @Override public void onSurfaceDestroyed(SurfaceHolder h) {
            visible = false; handler.removeCallbacks(drawTask); super.onSurfaceDestroyed(h);
        }
        @Override public void onDestroy() {
            visible = false; handler.removeCallbacks(drawTask); super.onDestroy();
        }
        private void drawFrame() {
            Canvas c = null;
            try { c = getSurfaceHolder().lockCanvas(); if (c != null) render(c); }
            catch (Exception ignored) { }
            finally { if (c != null) try { getSurfaceHolder().unlockCanvasAndPost(c); } catch (Exception ignored) { } }
            handler.removeCallbacks(drawTask);
            if (visible) handler.postDelayed(drawTask, 1000);
        }

        private void render(Canvas c) {
            final float w = c.getWidth(), h = c.getHeight();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ClockWallpaperService.this);
            boolean ocean = "ocean".equals(prefs.getString("clock_style", "dark"));
            if (ocean) drawOcean(c, w, h); else drawDark(c, w, h);

            Date now = new Date();
            String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(now);
            String date = new SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(now);

            // Tall, condensed iPhone-inspired numerals. The complete time stays on one line.
            p.reset(); p.setAntiAlias(true); p.setSubpixelText(true);
            p.setTypeface(Typeface.create("sans-serif-condensed-light", Typeface.NORMAL));
            p.setTextAlign(Paint.Align.CENTER);
            p.setTextSize(h * 0.485f);
            p.setTextScaleX(0.88f);
            float measured = p.measureText(time);
            float fit = Math.min(1f, (w * 0.94f) / Math.max(1f, measured));
            p.setTextScaleX(0.88f * fit);
            p.setColor(ocean ? 0xDDFEFFFF : 0xFF77797D);
            p.setShadowLayer(ocean ? 2f : 5f, 0f, 2f, ocean ? 0x557080A0 : 0x66000000);
            Paint.FontMetrics fm = p.getFontMetrics();
            float baseline = h * 0.625f;
            c.drawText(time, w / 2f, baseline, p);

            p.clearShadowLayer();
            p.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            p.setTextScaleX(1f); p.setTextSize(Math.max(16f, w * 0.040f));
            p.setColor(ocean ? 0xF5FFFFFF : 0xFFF1F1F1);
            c.drawText(date, w / 2f, h * 0.105f, p);
        }

        private void drawDark(Canvas c, float w, float h) {
            p.reset(); p.setAntiAlias(true);
            p.setShader(new LinearGradient(0, 0, 0, h,
                    new int[]{0xFF050709, 0xFF090D10, 0xFF030506, 0xFF020303}, null, Shader.TileMode.CLAMP));
            c.drawRect(0, 0, w, h, p); p.setShader(null);
            p.setShader(new RadialGradient(w * 0.5f, h * 0.78f, w * 0.75f,
                    new int[]{0x24365C68, 0x00365C68}, null, Shader.TileMode.CLAMP));
            c.drawRect(0, 0, w, h, p); p.setShader(null);
            p.setColor(0x182C6872); p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(h * 0.002f);
            c.drawLine(0, h * 0.79f, w, h * 0.79f, p); p.setStyle(Paint.Style.FILL);
        }

        private void drawOcean(Canvas c, float w, float h) {
            p.reset(); p.setAntiAlias(true);
            p.setShader(new LinearGradient(0, 0, 0, h * 0.72f,
                    new int[]{0xFF789CB9, 0xFFB7AFC1, 0xFFF0BFC0, 0xFFF6D9CC}, null, Shader.TileMode.CLAMP));
            c.drawRect(0, 0, w, h, p); p.setShader(null);
            // Soft sunset glow in the sky.
            p.setShader(new RadialGradient(w * 0.72f, h * 0.40f, w * 0.72f,
                    new int[]{0x66FFE5D5, 0x00FFE5D5}, null, Shader.TileMode.CLAMP));
            c.drawRect(0, 0, w, h * 0.68f, p); p.setShader(null);
            // Sea and layered, gently curved waves.
            p.setShader(new LinearGradient(0, h * 0.56f, 0, h,
                    new int[]{0xFF78AFC0, 0xFF397F96, 0xFF1B6079}, null, Shader.TileMode.CLAMP));
            c.drawRect(0, h * 0.56f, w, h, p); p.setShader(null);
            Path wave = new Path(); wave.moveTo(0, h * 0.59f);
            wave.cubicTo(w * 0.25f, h * 0.55f, w * 0.55f, h * 0.64f, w, h * 0.58f);
            wave.lineTo(w, h); wave.lineTo(0, h); wave.close();
            p.setColor(0x5579C1CC); c.drawPath(wave, p);
            Path foam = new Path(); foam.moveTo(0, h * 0.72f);
            foam.cubicTo(w * 0.22f, h * 0.68f, w * 0.65f, h * 0.76f, w, h * 0.70f);
            p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(h * 0.006f); p.setColor(0x66E9F4F1); c.drawPath(foam, p);
            p.setStyle(Paint.Style.FILL);
            p.setShader(new LinearGradient(0, h * 0.82f, 0, h,
                    new int[]{0xFFE8DAD2, 0xFFF2E8E0}, null, Shader.TileMode.CLAMP));
            c.drawRect(0, h * 0.91f, w, h, p); p.setShader(null);
        }
    }
}
