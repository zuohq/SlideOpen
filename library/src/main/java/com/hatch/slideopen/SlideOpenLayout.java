package com.hatch.slideopen;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Scroller;

/**
 * @author: Created by martin on 2018/8/2.
 */
public class SlideOpenLayout extends ViewGroup implements SlideInterface {

    private static final boolean DEBUG = true;

    private static final String TAG = SlideOpenLayout.class.getSimpleName();

    private static final float FRICTION = 3.5f;

    //是否不允许拖拽
    private boolean mIsUnableToDrag;
    //是否正在拖拽
    private boolean mIsBeingDragged;

    //宽高比
    private float ratio;
    //阻尼系数，值越大越难拖拽
    private float friction;

    private static final int INVALID_POINTER = -1;
    private int mActivePointerId = INVALID_POINTER;

    private static final int SCROLL_STATE_IDLE = 0;
    public static final int DRAG_TO_OPEN = 1;
    public static final int RELEASE_TO_OPEN = 2;

    private int mScrollState = SCROLL_STATE_IDLE;

    private float mLastMotionX;
    private float mLastMotionY;
    private float mInitialMotionX;
    private float mInitialMotionY;

    private View mViewLeft;
    private CustomViewRight mViewRight;

    private Scroller mScroller;
    private int mTouchSlop;

    private OnOpenListener mOnOpenListener;
    private OnScrollListener mOnScrollListener;


    public SlideOpenLayout(Context context) {
        this(context, null);
    }

    public SlideOpenLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideOpenLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mViewRight = new CustomViewRight(context, R.attr.slide_open_style);
        addView(mViewRight);

        mScroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledPagingTouchSlop();

        TypedArray a = context.obtainStyledAttributes(attrs
                , R.styleable.SlideOpenLayout, R.attr.slide_open_style, defStyleAttr);
        try {
            ratio = a.getFloat(R.styleable.SlideOpenLayout_so_ratio, 1.5f);
            friction = a.getFloat(R.styleable.SlideOpenLayout_so_friction, FRICTION);
        } finally {
            a.recycle();
        }
    }

    private void measureChild(View child, int widthMode, int widthSize, int heightSize) {
        final int widthSpec = MeasureSpec.makeMeasureSpec(widthSize, widthMode);
        final int heightSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);

        child.measure(widthSpec, heightSpec);

        if (DEBUG) {
            Log.d(TAG, "onMeasure width:"
                    + child.getMeasuredWidth() + ",height:" + child.getMeasuredHeight());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = getDefaultSize(0, widthMeasureSpec);
        int heightSize = (int) (widthSize / ratio);

        measureChild(mViewRight, MeasureSpec.AT_MOST, widthSize, heightSize);

        if (mViewLeft != null) {
            measureChild(mViewLeft, MeasureSpec.EXACTLY, widthSize, heightSize);
        }
        setMeasuredDimension(widthSize, heightSize);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0;
        int top = 0;
        int bottom = getMeasuredHeight();

        if (mViewLeft != null) {
            int childWidth = mViewLeft.getMeasuredWidth();
            mViewLeft.layout(left, top, left + childWidth, bottom);

            left += childWidth;
        }

        mViewRight.layout(left, top, left + mViewRight.getMeasuredWidth(), bottom);
    }

    @Override
    public void computeScroll() {
        if (!mScroller.isFinished() && mScroller.computeScrollOffset()) {
            int oldX = getScrollX();
            int oldY = getScrollY();

            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();

            if (oldX != x || oldY != y) {
                scrollTo(x, y);
            }
            postInvalidate();

            pageScrolled(getScrollX());
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mIsUnableToDrag) {//不允许拖拽则不拦截
            return false;
        }

        final int action = ev.getAction() & MotionEvent.ACTION_MASK;
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            resetTouch();
            return false;
        }

        if (action != MotionEvent.ACTION_DOWN) {
            if (mIsBeingDragged) {
                return true;
            }
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = mInitialMotionX = ev.getX();
                mLastMotionY = mInitialMotionY = ev.getY();
                mActivePointerId = ev.getPointerId(0);
                break;

            case MotionEvent.ACTION_MOVE:
                final int activePointerId = mActivePointerId;
                if (activePointerId == INVALID_POINTER) {
                    break;
                }

                final int pointerIndex = ev.findPointerIndex(activePointerId);
                float x = ev.getX(pointerIndex);
                final float dx = x - mLastMotionX;
                final float xDiff = Math.abs(dx);
                final float y = ev.getY(pointerIndex);
                final float yDiff = Math.abs(y - mInitialMotionY);

                if (xDiff > mTouchSlop && xDiff * 0.5f > yDiff && dx < 0) {//斜率小于1，且手指向左移动
                    mIsBeingDragged = true;
                    requestParentDisallowInterceptTouchEvent(true);
                    mLastMotionX = dx > 0 ? mInitialMotionX + mTouchSlop : mInitialMotionX - mTouchSlop;
                    mLastMotionY = y;
                }

                if (mIsBeingDragged) {
                    performDrag(x);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
        }
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mIsUnableToDrag) {
            return false;
        }

        if (mViewLeft == null) {
            return false;
        }

        int action = ev.getAction();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mScroller.abortAnimation();
                mLastMotionX = mInitialMotionX = ev.getX();
                mLastMotionY = mInitialMotionY = ev.getY();
                mActivePointerId = ev.getPointerId(0);
                break;

            case MotionEvent.ACTION_POINTER_DOWN: {
                final int index = ev.getActionIndex();
                final float x = ev.getX(index);
                mLastMotionX = x;
                mActivePointerId = ev.getPointerId(index);
                break;
            }
            case MotionEvent.ACTION_MOVE:
                if (!mIsBeingDragged) {
                    int pointerIndex = ev.findPointerIndex(mActivePointerId);
                    if (pointerIndex == -1) {
                        resetTouch();
                        break;
                    }
                    final float x = ev.getX(pointerIndex);
                    float dx = x - mLastMotionX;
                    final float xDiff = Math.abs(dx);
                    final float y = ev.getY(pointerIndex);
                    final float yDiff = Math.abs(y - mLastMotionY);

                    if (xDiff > mTouchSlop && xDiff > yDiff && dx < 0) {
                        mIsBeingDragged = true;
                        mLastMotionX = x - mInitialMotionX > 0
                                ? mInitialMotionX + mTouchSlop : mInitialMotionX - mTouchSlop;
                        mLastMotionY = y;
                        requestDisallowInterceptTouchEvent(true);
                    }
                }

                if (mIsBeingDragged) {
                    int activePointerIndex = ev.findPointerIndex(mActivePointerId);
                    float x = ev.getX(activePointerIndex);
                    performDrag(x);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mIsBeingDragged) {
                    smoothScrollTo(0, 0);
                    resetTouch();
                    setScrollState(SCROLL_STATE_IDLE);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mIsBeingDragged) {

                    setScrollState(SCROLL_STATE_IDLE);
                    resetTouch();

                    if (getScrollX() > getRightViewSize() && mOnOpenListener != null) {
                        mOnOpenListener.onOpen();
                        scrollTo(0, 0);
                        pageScrolled(0);
                    } else {
                        smoothScrollTo(0, 0);
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                mLastMotionX = ev.getX(ev.findPointerIndex(mActivePointerId));
                break;
        }
        return true;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionX = ev.getX(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    private void performDrag(float x) {
        final float deltaX = (mLastMotionX - x) / friction;
        mLastMotionX = x;

        final int maximumDragScroll = getMaximumDragScroll();

        float oldScrollX = getScrollX();
        float scrollX = oldScrollX + deltaX;
        scrollX = Math.min(Math.max(0, scrollX), maximumDragScroll);

        mLastMotionX += scrollX - (int) scrollX;
        scrollTo((int) scrollX, getScrollY());
        postInvalidate();

        int size = getRightViewSize();
        if (mScrollState != DRAG_TO_OPEN && scrollX <= size) {
            setScrollState(DRAG_TO_OPEN);
            mViewRight.onDrag();
        } else if (mScrollState == DRAG_TO_OPEN && scrollX > size) {
            setScrollState(RELEASE_TO_OPEN);
            mViewRight.onRelease();
        }

        pageScrolled(getScrollX());
    }

    void smoothScrollTo(int x, int y) {
        int sx = getScrollX();
        int sy = getScrollY();

        int dx = x - sx;
        int dy = y - sy;

        mScroller.startScroll(sx, sy, dx, dy, 600);
        postInvalidate();
    }

    void pageScrolled(int x) {
        if (mOnScrollListener == null) return;

        if (mViewLeft == null) {
            mOnScrollListener.onPageScrolled(0);
            return;
        }
        mOnScrollListener.onPageScrolled(x);
    }

    private int getRightViewSize() {
        return mViewRight.getWidth();
    }

    /***
     *
     * 最大拖拽大小
     *
     * @return
     */
    private int getMaximumDragScroll() {
        return (int) (getResources().getDisplayMetrics().widthPixels / friction);
    }

    void setScrollState(int newState) {
        if (mScrollState == newState) {
            return;
        }
        mScrollState = newState;
    }

    private void requestParentDisallowInterceptTouchEvent(boolean disallowIntercept) {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    private void resetTouch() {
        mActivePointerId = INVALID_POINTER;
        mIsBeingDragged = false;
    }

    @Override
    public void instantiateItem(View viewLeft) {
        if (mViewLeft == viewLeft) {
            return;
        }
        mViewLeft = viewLeft;
        addView(mViewLeft);
    }

    @Override
    public void setUnableToDrag(boolean isUnableToDrag) {
        mIsUnableToDrag = isUnableToDrag;
    }

    public void setOnOpenListener(OnOpenListener listener) {
        this.mOnOpenListener = listener;
    }

    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        this.mOnScrollListener = listener;
    }


    public interface OnScrollListener {

        /**
         * X方向滚动的距离
         *
         * @param scrollX x方向滚动距离
         */
        void onPageScrolled(int scrollX);
    }

    public interface OnOpenListener {

        /***
         * 打开
         */
        void onOpen();
    }
}
