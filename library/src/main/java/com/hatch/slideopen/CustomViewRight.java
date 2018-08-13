package com.hatch.slideopen;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author: Created by martin on 2018/8/2.
 */
public class CustomViewRight extends LinearLayout {

    //拖拽文本
    private String dragText;
    //释放文本
    private String releaseText;

    private Drawable drawable;

    private int mChildMarginLeft;

    private int textColor;
    private int textSize;

    private TextView mLabel;
    private ImageView mIcon;

    private Animation mFlipAnimation;
    private Animation mReverseFlipAnimation;


    public CustomViewRight(Context context, int themeAttr) {
        super(context);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        inflate(context, R.layout.layout_right, this);

        init(context, themeAttr);

        buildAnimation();

        initView();

        setUpView();
    }

    private void init(Context context, int themeAttr) {
        TypedArray a = context.obtainStyledAttributes(null, R.styleable.CustomViewRight, themeAttr, 0);

        try {
            dragText = a.getString(R.styleable.CustomViewRight_so_drag_label);
            releaseText = a.getString(R.styleable.CustomViewRight_so_release_label);

            drawable = a.getDrawable(R.styleable.CustomViewRight_android_src);

            textColor = a.getColor(R.styleable.CustomViewRight_android_textColor, Color.WHITE);
            textSize = a.getDimensionPixelSize(R.styleable.CustomViewRight_android_textSize, 0);

            mChildMarginLeft = a.getDimensionPixelSize(R.styleable.CustomViewRight_so_child_margin_left, 0);
            int paddingLeft = a.getDimensionPixelSize(R.styleable.CustomViewRight_android_paddingLeft, 0);
            int paddingRight = a.getDimensionPixelSize(R.styleable.CustomViewRight_android_paddingRight, 0);

            setPadding(paddingLeft, 0, paddingRight, 0);

        } finally {
            a.recycle();
        }
    }

    private void initView() {
        mLabel = findViewById(R.id.tv_label);
        mIcon = findViewById(R.id.iv_arrow);
    }

    private void setUpView() {
        mLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        mLabel.setTextColor(textColor);
        mLabel.setText(dragText);

        LinearLayout.LayoutParams lp = (LayoutParams) mLabel.getLayoutParams();
        lp.leftMargin = mChildMarginLeft;

        mIcon.setImageDrawable(drawable);
    }

    private void buildAnimation() {
        mFlipAnimation = new RotateAnimation(0, -180
                , RotateAnimation.RELATIVE_TO_SELF, 0.5f
                , RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(300);
        mFlipAnimation.setFillAfter(true);

        mReverseFlipAnimation = new RotateAnimation(-180, 0
                , RotateAnimation.RELATIVE_TO_SELF, 0.5f
                , RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(300);
        mReverseFlipAnimation.setFillAfter(true);
    }

    public void onDrag() {
        if (!mLabel.getText().toString().equals(dragText)) {
            mLabel.setText(dragText);

            mIcon.clearAnimation();
            mIcon.startAnimation(mFlipAnimation);
        }
    }

    public void onRelease() {
        if (!mLabel.getText().toString().equals(releaseText)) {
            mLabel.setText(releaseText);

            mIcon.clearAnimation();
            mIcon.startAnimation(mReverseFlipAnimation);
        }

    }
}
