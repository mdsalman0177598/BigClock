package com.example.bigclock;

<<<<<<< HEAD
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
=======
import android.graphics.*;
>>>>>>> origin/main
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
<<<<<<< HEAD

=======
>>>>>>> origin/main
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ClockWallpaperService extends WallpaperService {
<<<<<<< HEAD
    @Override public Engine onCreateEngine() {
        return new ClockEngine();
    }

    private class ClockEngine extends Engine {
        private final Handler handler = new Handler(Looper.getMainLooper());
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private boolean visible;
        private final Runnable drawTask = new Runnable() {
            @Override public void run() {
                drawFrame();
            }
        };

        @Override public void onVisibilityChanged(boolean isVisible) {
            visible = isVisible;
            handler.removeCallbacks(drawTask);
            if (visible) drawFrame();
        }

        @Override public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            drawFrame();
        }

        @Override public void onSurfaceDestroyed(SurfaceHolder holder) {
            visible = false;
            handler.removeCallbacks(drawTask);
            super.onSurfaceDestroyed(holder);
        }

        @Override public void onDestroy() {
            visible = false;
            handler.removeCallbacks(drawTask);
            super.onDestroy();
        }

        private void drawFrame() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) render(canvas);
            } catch (Exception ignored) {
            } finally {
                if (canvas != null) {
                    try { holder.unlockCanvasAndPost(canvas); } catch (Exception ignored) { }
                }
            }
            handler.removeCallbacks(drawTask);
            if (visible) handler.postDelayed(drawTask, 1000);
        }

        private void render(Canvas canvas) {
            final float w = canvas.getWidth();
            final float h = canvas.getHeight();

            paint.setShader(new LinearGradient(0, 0, w * 0.85f, h,
                    new int[] { 0xFF5657C9, 0xFF9360D5, 0xFFD875B9, 0xFFF2A2A8 },
                    new float[] { 0f, 0.38f, 0.72f, 1f }, Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, w, h, paint);
            paint.setShader(null);

            Date now = new Date();
            String time = new SimpleDateFormat("h:mm", Locale.getDefault()).format(now);
            String date = new SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(now);

            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(0xFFF9F7FF);
            paint.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));

            float timeSize = Math.min(w * 0.235f, h * 0.115f);
            paint.setTextSize(timeSize);
            Paint.FontMetrics fm = paint.getFontMetrics();
            float baseline = h * 0.34f - (fm.ascent + fm.descent) / 2f;
            canvas.drawText(time, w / 2f, baseline, paint);

            paint.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            paint.setTextSize(Math.min(w * 0.052f, 22f * getResources().getDisplayMetrics().density));
            paint.setColor(0xFFF5F0FF);
            float dateY = baseline + timeSize * 0.78f;
            canvas.drawText(date.toUpperCase(Locale.getDefault()), w / 2f, dateY, paint);
=======
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
>>>>>>> origin/main
        }
    }
}
