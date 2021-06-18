package com.choryan.spannabletextpacket.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.choryan.spannabletextpacket.interf.IBaseLayer;
import com.choryan.spannabletextpacket.layer.TextLayer;

import java.util.ArrayList;

/**
 * @author: ChoRyan Quan
 * @date: 6/17/21
 */
public class LayerSpan extends ReplacementSpan {

    private static final String TAG = "LayerSpan";

    private float rH, rW;
    private ArrayList<IBaseLayer> layerList;

    private Paint.FontMetricsInt fontMetrics = new Paint.FontMetricsInt();
    private Paint.FontMetricsInt paintMetrics = new Paint.FontMetricsInt();

    public LayerSpan(float H, float W) {
        rH = H;
        rW = W;
        layerList = new ArrayList<>();
    }

    public void addLayerList(IBaseLayer layer) {
        layerList.add(layer);
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        if (fm != null) {
            Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
            fm.ascent = fontMetricsInt.ascent;
            fm.descent = fontMetricsInt.descent;
            fm.top = fontMetricsInt.top;
            fm.bottom = fontMetricsInt.bottom;
            if (rH != 1 && rH != 0) {
                fm.top = (int) (fm.top * rH);
                fm.ascent = (int) (fm.ascent * rH);
                fm.bottom = (int) (fm.bottom * rH);
                fm.descent = (int) (fm.descent * rH);
            }
            fontMetrics.ascent = fm.ascent;
            fontMetrics.descent = fm.descent;
            fontMetrics.top = fm.top;
            fontMetrics.bottom = fm.bottom;
        }
        int width = measureWidth(paint, text, start, end);
        Log.d(TAG, "getSize: " + width);
        return width;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        int width = measureWidth(paint, text, start, end);
        RectF rect = new RectF();
        rect.set(x, y + fontMetrics.ascent, width + x, y + fontMetrics.descent);

        paint.getFontMetricsInt(paintMetrics);

        DrawParam param = new DrawParam();
        param.txtParam.text = text;
        if (rW > 0) {
            param.txtParam.x = (width - paint.measureText(text, start, end)) / 2 + rect.left;
        } else {
            param.txtParam.x = rect.left;
        }
        param.txtParam.centerY = rect.centerY();
        param.txtParam.y = rect.centerY() - (paintMetrics.descent + paintMetrics.ascent) / 2f;
        param.txtParam.start = start;
        param.txtParam.end = end;
        for (IBaseLayer layer : layerList) {
            layer.setRectF(rect, paint.getTextSize());
            layer.draw(start, canvas, param, paint);
        }
    }

    private int measureWidth(Paint paint, CharSequence text, int start, int end) {
        return rW > 0 ? (int) (rW * paint.getTextSize() * (end - start)) : (int) paint.measureText(text, start, end);
    }

    private class DrawParam implements TextLayer.ITxtDrawParam {
        public TextLayer.TxtParam txtParam = new TextLayer.TxtParam();

        @Override
        public TextLayer.TxtParam getTxtParam() {
            return txtParam;
        }
    }

}
