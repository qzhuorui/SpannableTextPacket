package com.choryan.spannabletextpacket.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.choryan.spannabletextpacket.interf.BackgroundDrawer;
import com.choryan.spannabletextpacket.interf.ForegroundDrawer;
import com.choryan.spannabletextpacket.interf.LineDrawer;
import com.choryan.spannabletextpacket.span.LayerSpan;
import com.choryan.spannabletextpacket.span.SingleWarpSpan;

/**
 * @author: ChoRyan Quan
 * @date: 6/17/21
 */
public class ShadeTextView extends TextView {

    private SpannableStringBuilder spannableStringBuilder;

    public ShadeTextView(Context context) {
        super(context);
    }

    public ShadeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text instanceof SpannableStringBuilder) {
            spannableStringBuilder = (SpannableStringBuilder) text;
            LayerSpan[] layerSpans = spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), LayerSpan.class);
            if (layerSpans != null) {
                for (int i = 0; i < spannableStringBuilder.length(); i++) {
                    layerSpans = spannableStringBuilder.getSpans(i, i + 1, LayerSpan.class);
                    if (layerSpans != null && layerSpans.length > 0) {
                        spannableStringBuilder.setSpan(new SingleWarpSpan(layerSpans[layerSpans.length - 1]), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
            remove(spannableStringBuilder, LayerSpan.class);
        } else {
            spannableStringBuilder = null;
        }
        super.setText(text, type);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getLayout() != null && spannableStringBuilder != null) {
            Paint.FontMetricsInt fontMetricsInt = new Paint.FontMetricsInt();
            getPaint().getFontMetricsInt(fontMetricsInt);
            drawLineRect(fontMetricsInt, canvas, getLayout(), BackgroundDrawer.class);
            super.onDraw(canvas);
            drawLineRect(fontMetricsInt, canvas, getLayout(), ForegroundDrawer.class);
        } else {
            super.onDraw(canvas);
        }
    }

    private <T> void remove(SpannableStringBuilder sb, Class<T> c) {
        T[] spans = sb.getSpans(0, sb.length(), c);
        if (spans != null) {
            for (T span : spans) {
                sb.removeSpan(span);
            }
        }
    }

    private <T extends LineDrawer> void drawLineRect(Paint.FontMetricsInt fontMetricsInt, Canvas canvas, Layout layout, Class<T> drawerClass) {
        LineDrawer[] drawers = spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), drawerClass);
        if (drawers != null) {
            LineDrawer drawer = null;
            int lineStart, lineEnd, spanStar, spanEnd;
            float left, right;
            int lineCount = getLineCount();
            for (int line = 0; line < lineCount; line++) {
                lineStart = layout.getLineStart(line);
                lineEnd = layout.getLineEnd(line);
                for (LineDrawer lineDrawer : drawers) {
                    drawer = lineDrawer;
                    spanStar = Math.max(lineStart, spannableStringBuilder.getSpanStart(drawer));
                    spanEnd = Math.min(lineEnd, spannableStringBuilder.getSpanEnd(drawer));
                    if (spanEnd > spanStar) {
                        if (lineStart < spanStar) {
                            left = layout.getLineLeft(line) + Layout.getDesiredWidth(spannableStringBuilder, lineStart, spanStar, getPaint());
                        } else {
                            left = layout.getLineLeft(line);
                        }
                        if (lineEnd > spanEnd) {
                            right = layout.getLineRight(line) - StaticLayout.getDesiredWidth(spannableStringBuilder, spanEnd, lineEnd, getPaint());
                        } else {
                            right = layout.getLineRight(line);
                        }
                        left += getPaddingLeft();
                        right += getPaddingLeft();

                        drawer.draw(canvas, getPaint(), left, layout.getLineTop(line) + getPaddingTop(), right, layout.getLineBottom(line) + getPaddingTop(), layout.getLineBaseline(line));
                    }
                }
            }
        }
    }
}
