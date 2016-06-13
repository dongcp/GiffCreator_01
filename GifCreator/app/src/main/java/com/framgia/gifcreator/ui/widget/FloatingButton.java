package com.framgia.gifcreator.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.framgia.gifcreator.R;

/**
 * Created by yue on 06/06/2016.
 */
public class FloatingButton extends LinearLayout {

    private TextView mFloatingButtonTitle;
    private FloatingActionButton mFab;
    private String mTitle;
    private Drawable mSource;
    private int mTitleSize;

    public FloatingButton(Context context) {
        super(context);
        init(context, null);
    }

    public FloatingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        View.inflate(context, R.layout.floating_button, this);
        findViews();
        TypedArray arr = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FloatingButton, 0, 0);
        mTitle = arr.getString(R.styleable.FloatingButton_fab_title);
        mTitleSize = arr.getDimensionPixelSize(R.styleable.FloatingButton_fab_title_size,
                getResources().getDimensionPixelOffset(R.dimen.common_text_size_2));
        mSource = arr.getDrawable(R.styleable.FloatingButton_android_src);
        mFloatingButtonTitle.setText(mTitle);
        mFloatingButtonTitle.setTextSize(mTitleSize);
        mFab.setImageDrawable(mSource);
        arr.recycle();
    }

    private void findViews() {
        mFloatingButtonTitle = (TextView) findViewById(R.id.floating_button_title);
        mFab = (FloatingActionButton) findViewById(R.id.floating_button);
    }
}
