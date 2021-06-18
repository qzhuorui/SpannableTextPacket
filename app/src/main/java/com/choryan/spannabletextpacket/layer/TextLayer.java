package com.choryan.spannabletextpacket.layer;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * @author: ChoRyan Quan
 * @date: 6/17/21
 */
public class TextLayer extends BaseLayer {
    @Override
    protected void drawLayer(int index, Canvas c, DrawParam param, Paint paint) {
        if (param instanceof ITxtDrawParam) {
            TxtParam txtParam = ((ITxtDrawParam) param).getTxtParam();
            if (txtParam != null) {
                c.drawText(txtParam.text, txtParam.start, txtParam.end, txtParam.x, txtParam.y, paint);
            }
        }
    }

    public interface ITxtDrawParam extends DrawParam {
        TxtParam getTxtParam();
    }

    public static class TxtParam {
        public float x, y, centerY;
        public int start, end;
        public CharSequence text;
    }
}
