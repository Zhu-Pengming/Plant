package com.example.npm;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

public class AutoHeightTextView extends androidx.appcompat.widget.AppCompatTextView {

    public AutoHeightTextView(Context context) {
        super(context);
    }

    public AutoHeightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoHeightTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        adjustHeight(text);
    }

    private void adjustHeight(CharSequence text) {
        int length = text.length();
        ViewGroup.LayoutParams params = getLayoutParams();
        if (length > 20) {
            params.height = 200; // larger height for long texts
        } else {
            params.height = 100; // default height for short texts
        }
        setLayoutParams(params);
    }
}
