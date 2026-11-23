package com.example.bigclock;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ClockWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new ClockEngine();
    }

    private class ClockEngine extends Engine {

        private final Handler handler = new Handler(Looper.getMainLooper());
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private boolean visible;

        private final Runnable drawTask = new Runnable() {
            @Override
            public void run() {
                drawFrame();
            }
        };

        @Override
        public void onVisibilityChanged(boolean isVisible) {
            visible = isVisible;
            handler.removeCallbacks(drawTask);
            if (visible) drawFrame();
        }

        @Override
        public void onSurfaceChanged(
                SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            drawFrame();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            visible = false;
            handler.removeCallbacks(drawTask);
            super.onSurfaceDestroyed(holder);
        }

        @Override
        public void onDestroy() {
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
                    try {
                        holder.unlockCanvasAndPost(canvas);
                    } catch (Exception ignored) {
                    }
                }
            }

            handler.removeCallbacks(drawTask);
            if (visible) handler.postDelayed(drawTask, 1000);
        }

        private void render(Canvas canvas) {
            float w = canvas.getWidth();
            float h = canvas.getHeight();

            paint.setShader(new LinearGradient(
                    0, 0, w * 0.85f, h,
                    new int[]{
                            0xFF5657C9,
                            0xFF9360D5,
                            0xFFD875B9,
                            0xFFF2A2A8
                    },
                    new float[]{0f, 0.38f, 0.72f, 1f},
                    Shader.TileMode.CLAMP));

            canvas.drawRect(0, 0, w, h, paint);
            paint.setShader(null);

            Date now = new Date();

            String time = new SimpleDateFormat(
                    "h:mm", Locale.getDefault()).format(now);

            String date = new SimpleDateFormat(
                    "EEEE, d MMMM", Locale.getDefault()).format(now);

            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(0xFFF9F7FF);
            paint.setTypeface(
                    Typeface.create("sans-serif-thin", Typeface.NORMAL));

            float timeSize = Math.min(w * 0.235f, h * 0.115f);
            paint.setTextSize(timeSize);

            Paint.FontMetrics fm = paint.getFontMetrics();
            float baseline = h * 0.34f -
                    (fm.ascent + fm.descent) / 2f;

            canvas.drawText(time, w / 2f, baseline, paint);

            paint.setTypeface(
                    Typeface.create("sans-serif-medium", Typeface.NORMAL));

            paint.setTextSize(Math.min(
                    w * 0.052f,
                    22f * getResources().getDisplayMetrics().density));

            paint.setColor(0xFFF5F0FF);
            float dateY = baseline + timeSize * 0.78f;

            canvas.drawText(
                    date.toUpperCase(Locale.getDefault()),
                    w / 2f, dateY, paint);
        }
    }
}
