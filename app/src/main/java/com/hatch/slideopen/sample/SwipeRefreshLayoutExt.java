package com.hatch.slideopen.sample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * @Description:
 * @author: Created by martin on 2018/8/9.
 */
public class SwipeRefreshLayoutExt extends SwipeRefreshLayout {

    private int mTouchSlop;

    private float mLastMotionX;
    private float mLastMotionY;

    public SwipeRefreshLayoutExt(@NonNull Context context) {
        this(context, null);
    }

    public SwipeRefreshLayoutExt(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = ev.getX();
                mLastMotionY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float x = ev.getX();
                float y = ev.getY();
                float xDiff = x - mLastMotionX;
                float yDiff = y - mLastMotionY;
                if (Math.abs(xDiff) > mTouchSlop && Math.abs(xDiff) > Math.abs(yDiff)) {
                    return false;
                }
        }

        return super.onInterceptTouchEvent(ev);
    }
}
