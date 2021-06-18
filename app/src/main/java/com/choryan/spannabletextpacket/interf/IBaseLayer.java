package com.choryan.spannabletextpacket.interf;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * @author: ChoRyan Quan
 * @date: 6/17/21
 */
public interface IBaseLayer {

    void draw(int index, Canvas canvas, DrawParam param, Paint paint);

    void setRectF(RectF rect, float absSize);

    void offset(float x, float y);

    void rotate(float degree);

    void scale(float scale);

    void setPaintHandler(IPaintHandler shader);

    void setDrawDispatcher(IDrawDispatcher dispatcher);


    interface DrawParam {

    }

    interface IPaintHandler {
        void handlePaint(int index, Paint paint, RectF rectF);
    }

    interface IDrawDispatcher {
        void draw(int index, IBaseLayer layer, Canvas canvas, DrawParam param, Paint paint);
    }
}
