package com.choryan.spannabletextpacket.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

/**
 * @author: ChoRyan Quan
 * @date: 6/17/21
 */
public class STextView extends ShadeTextView {


    public STextView(Context context) {
        super(context);
    }

    public STextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
    }
}
