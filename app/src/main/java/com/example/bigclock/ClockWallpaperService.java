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
            if (visible) {
                // A one-second refresh keeps the clock accurate without heavy work.
                handler.postDelayed(drawTask, 1000);
            }
        }

        private void render(Canvas canvas) {
            final float width = canvas.getWidth();
            final float height = canvas.getHeight();

            // Soft purple-to-pink gradient similar to the reference look.
            paint.setShader(new LinearGradient(
                    0, 0, width * 0.82f, height,
                    new int[] {
                            0xFF6259C9,
                            0xFF8958D1,
                            0xFFB65BC6,
                            0xFFE88AB5
                    },
                    new float[] {0f, 0.38f, 0.72f, 1f},
                    Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, width, height, paint);
            paint.setShader(null);

            Date now = new Date();
            String time = new SimpleDateFormat("h:mm", Locale.ENGLISH).format(now);
            String date = new SimpleDateFormat(
                    "EEEE, d MMMM", Locale.ENGLISH).format(now).toUpperCase(Locale.ENGLISH);

            // Large, single-line, thin iPhone-inspired clock.
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));
            paint.setColor(0xFFFDFBFF);
            paint.setShadowLayer(3f, 0f, 2f, 0x33000000);

            float textSize = Math.min(width * 0.315f, height * 0.155f);
            paint.setTextSize(textSize);
            float maxTextWidth = width * 0.90f;
            float measured = paint.measureText(time);
            if (measured > maxTextWidth) {
                textSize *= maxTextWidth / measured;
                paint.setTextSize(textSize);
            }

            Paint.FontMetrics metrics = paint.getFontMetrics();
            float clockCenterY = height * 0.355f;
            float clockBaseline = clockCenterY - (metrics.ascent + metrics.descent) / 2f;
            canvas.drawText(time, width / 2f, clockBaseline, paint);

            paint.clearShadowLayer();
            paint.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            paint.setTextSize(Math.min(width * 0.050f, 22f * getResources()
                    .getDisplayMetrics().density));
            paint.setColor(0xFFF8F4FF);

            float dateY = clockBaseline + textSize * 0.72f;
            canvas.drawText(date, width / 2f, dateY, paint);
        }
    }
}
