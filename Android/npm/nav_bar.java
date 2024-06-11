package com.tom.npm;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class nav_bar extends LinearLayout {
    private Context con;
    private int inputview_input_icon;
    private String inputview_input_hint;
    private boolean inputview_is_pass;

    private int right_icon;
    private View inflate;
    private ImageView imageView;
    private TextView editText;

    public nav_bar(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public nav_bar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public nav_bar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.con = context; // 初始化 context
        if (attrs == null) {
            return;
        }

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.nav);
        inputview_input_icon = typedArray.getResourceId(R.styleable.nav_icon, R.mipmap.ic_launcher);
        right_icon = typedArray.getResourceId(R.styleable.nav_right_icon, R.mipmap.ic_launcher);
        inputview_input_hint = typedArray.getString(R.styleable.nav_hint);
        float textSize = typedArray.getDimension(R.styleable.nav_hintTextSize, 14);
        typedArray.recycle();

        inflate = LayoutInflater.from(context).inflate(R.layout.nav_bar, this, false);
        editText = inflate.findViewById(R.id.title);
        imageView = inflate.findViewById(R.id.back);
        imageView.setImageResource(inputview_input_icon);
        editText.setText(inputview_input_hint);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        addView(inflate);
    }

    public void setTextSizeInSp(float sizeInSp) {
        if (con != null) { // 添加 null 检查
            float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sizeInSp, con.getResources().getDisplayMetrics());
            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, pixels);
        }
    }
}
