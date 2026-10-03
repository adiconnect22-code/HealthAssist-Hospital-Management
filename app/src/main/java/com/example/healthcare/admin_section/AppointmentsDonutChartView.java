package com.example.healthcare.admin_section;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Custom Donut/Ring Chart View in 'admin_section' package.
 */
public class AppointmentsDonutChartView extends View {

    private final Paint confirmedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint completedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pendingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint cancelledPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint centerTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint subTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF ovalRect = new RectF();

    private int confirmedCount = 18;
    private int completedCount = 6;
    private int pendingCount = 3;
    private int cancelledCount = 1;

    public AppointmentsDonutChartView(Context context) {
        super(context);
        init();
    }

    public AppointmentsDonutChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppointmentsDonutChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        float strokeWidth = 32f;

        confirmedPaint.setStyle(Paint.Style.STROKE);
        confirmedPaint.setStrokeWidth(strokeWidth);
        confirmedPaint.setColor(Color.parseColor("#237053"));

        completedPaint.setStyle(Paint.Style.STROKE);
        completedPaint.setStrokeWidth(strokeWidth);
        completedPaint.setColor(Color.parseColor("#223894"));

        pendingPaint.setStyle(Paint.Style.STROKE);
        pendingPaint.setStrokeWidth(strokeWidth);
        pendingPaint.setColor(Color.parseColor("#E5A132"));

        cancelledPaint.setStyle(Paint.Style.STROKE);
        cancelledPaint.setStrokeWidth(strokeWidth);
        cancelledPaint.setColor(Color.parseColor("#4B5563"));

        centerTextPaint.setColor(Color.parseColor("#111827"));
        centerTextPaint.setTextSize(48f);
        centerTextPaint.setFakeBoldText(true);
        centerTextPaint.setTextAlign(Paint.Align.CENTER);

        subTextPaint.setColor(Color.parseColor("#6B7280"));
        subTextPaint.setTextSize(26f);
        subTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setOverviewData(int confirmed, int completed, int pending, int cancelled) {
        this.confirmedCount = Math.max(0, confirmed);
        this.completedCount = Math.max(0, completed);
        this.pendingCount = Math.max(0, pending);
        this.cancelledCount = Math.max(0, cancelled);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        if (width == 0 || height == 0) return;

        float padding = 24f;
        float size = Math.min(width, height) - padding * 2;
        float left = (width - size) / 2f;
        float top = (height - size) / 2f;
        ovalRect.set(left, top, left + size, top + size);

        int total = confirmedCount + completedCount + pendingCount + cancelledCount;
        float safeTotal = (total > 0) ? total : 1f;

        float startAngle = -90f;

        float sweepConfirmed = (confirmedCount / safeTotal) * 360f;
        if (sweepConfirmed > 0) {
            canvas.drawArc(ovalRect, startAngle, sweepConfirmed, false, confirmedPaint);
            startAngle += sweepConfirmed;
        }

        float sweepCompleted = (completedCount / safeTotal) * 360f;
        if (sweepCompleted > 0) {
            canvas.drawArc(ovalRect, startAngle, sweepCompleted, false, completedPaint);
            startAngle += sweepCompleted;
        }

        float sweepPending = (pendingCount / safeTotal) * 360f;
        if (sweepPending > 0) {
            canvas.drawArc(ovalRect, startAngle, sweepPending, false, pendingPaint);
            startAngle += sweepPending;
        }

        float sweepCancelled = (cancelledCount / safeTotal) * 360f;
        if (sweepCancelled > 0) {
            canvas.drawArc(ovalRect, startAngle, sweepCancelled, false, cancelledPaint);
        }

        float centerX = width / 2f;
        float centerY = height / 2f;

        canvas.drawText(String.valueOf(total), centerX, centerY + 10f, centerTextPaint);
        canvas.drawText("Total", centerX, centerY + 42f, subTextPaint);
    }
}
