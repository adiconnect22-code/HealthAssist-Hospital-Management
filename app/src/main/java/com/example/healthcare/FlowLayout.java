package com.example.healthcare;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/** Simple wrapping layout used for chips (departments, symptoms, filters). */
public class FlowLayout extends ViewGroup {

    private final int hGap;
    private final int vGap;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        float d = context.getResources().getDisplayMetrics().density;
        hGap = Math.round(8 * d);
        vGap = Math.round(8 * d);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxW = totalWidth - getPaddingLeft() - getPaddingRight();
        int x = 0, y = 0, rowH = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View c = getChildAt(i);
            if (c.getVisibility() == GONE) continue;
            c.measure(MeasureSpec.makeMeasureSpec(maxW, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int cw = c.getMeasuredWidth();
            int ch = c.getMeasuredHeight();
            if (x > 0 && x + cw > maxW) {
                x = 0;
                y += rowH + vGap;
                rowH = 0;
            }
            x += cw + hGap;
            rowH = Math.max(rowH, ch);
        }
        int height = y + rowH + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(totalWidth, resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int maxW = (r - l) - getPaddingLeft() - getPaddingRight();
        int x = 0, y = 0, rowH = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View c = getChildAt(i);
            if (c.getVisibility() == GONE) continue;
            int cw = c.getMeasuredWidth();
            int ch = c.getMeasuredHeight();
            if (x > 0 && x + cw > maxW) {
                x = 0;
                y += rowH + vGap;
                rowH = 0;
            }
            int left = getPaddingLeft() + x;
            int top = getPaddingTop() + y;
            c.layout(left, top, left + cw, top + ch);
            x += cw + hGap;
            rowH = Math.max(rowH, ch);
        }
    }
}
