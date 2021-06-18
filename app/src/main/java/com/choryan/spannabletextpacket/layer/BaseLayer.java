package com.choryan.spannabletextpacket.layer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.choryan.spannabletextpacket.interf.IBaseLayer;

/**
 * @author: ChoRyan Quan
 * @date: 6/17/21
 */
public class BaseLayer implements IBaseLayer {

    protected IPaintHandler paintHandler;
    protected IDrawDispatcher drawDispatcher;

    protected float offsetX, offsetY, degree, scale;
    private float absSize;
    protected RectF rect;


    @Override
    public void draw(int index, Canvas canvas, DrawParam param, Paint paint) {
        if (offsetX != 0 || offsetY != 0 || degree != 0 || (scale != 0 && scale != 1)) {
            canvas.save();
            canvas.translate(offsetX * absSize, offsetY * absSize);
            if (degree != 0) {
                canvas.rotate(degree, rect.centerX(), rect.centerY());
            }
            if (scale != 0 && scale != 1) {
                canvas.scale(scale, scale, rect.centerX(), rect.centerY());
            }
            paint.reset();
            paint.set(paint);
            if (paintHandler != null) {
                paintHandler.handlePaint(index, paint, rect);
            }
            if (drawDispatcher != null) {
                drawDispatcher.draw(index, this, canvas, param, paint);
            } else {
                drawLayer(index, canvas, param, paint);
            }
            canvas.restore();
        } else {
            paint.reset();
            paint.set(paint);
            if (paintHandler != null) {
                paintHandler.handlePaint(index, paint, rect);
            }
            if (drawDispatcher != null) {
                drawDispatcher.draw(index, this, canvas, param, paint);
            } else {
                drawLayer(index, canvas, param, paint);
            }
        }
    }

    protected void drawLayer(int index, Canvas c, DrawParam param, Paint paint) {

    }

    @Override
    public void setRectF(RectF rect, float absSize) {
        this.absSize = absSize;
        if (this.rect == null || this.rect != rect) {
            this.rect = rect;
            onRectChange(rect, absSize);
        }
    }

    protected void onRectChange(RectF rect, float absSize) {

    }

    @Override
    public void offset(float x, float y) {
        offsetX = x;
        offsetY = y;
    }

    @Override
    public void rotate(float degree) {
        this.degree = degree;
    }

    @Override
    public void scale(float scale) {
        this.scale = scale;
    }

    @Override
    public void setPaintHandler(IPaintHandler shader) {
        paintHandler = shader;
    }

    @Override
    public void setDrawDispatcher(IDrawDispatcher dispatcher) {
        drawDispatcher = dispatcher;
    }

}
