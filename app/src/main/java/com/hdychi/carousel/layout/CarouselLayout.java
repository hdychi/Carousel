package com.hdychi.carousel.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.hdychi.carousel.R;

public class CarouselLayout extends ViewGroup {
    private static final int DEFAULT_WIDTH = 50;
    private static final int DEFULT_ITEM_COUNT = 5;
    private static final int TOUCH_SLOP = 50;
    private int visibleItem = 5;

    public CarouselLayout(Context context) {
        super(context);
    }

    public CarouselLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public CarouselLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    public CarouselLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);
    }
    public void init(Context context,AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.CarouselLayout);
        if (typedArray != null) {
            visibleItem = typedArray.getInt(R.styleable.CarouselLayout_visibleItem, DEFULT_ITEM_COUNT);
            typedArray.recycle();
        }
    }

    /**
     * 测量阶段，按照屏幕内可见的子view数量计算子view的最大宽度
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int selfHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int selfHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        int selfWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int selfWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams layoutParams = child.getLayoutParams();
            int childHeightSpec;
            int childWidthSpec;
            switch (layoutParams.height) {
                case LayoutParams.MATCH_PARENT:
                    if (selfHeightMode == MeasureSpec.EXACTLY
                            || selfHeightMode == MeasureSpec.AT_MOST) {
                        childHeightSpec = MeasureSpec.makeMeasureSpec(selfHeightSize,
                                MeasureSpec.EXACTLY);
                    } else {
                        childHeightSpec = MeasureSpec.makeMeasureSpec(selfHeightSize,
                                MeasureSpec.AT_MOST);
                    }
                    break;
                case LayoutParams.WRAP_CONTENT:
                    if (selfHeightMode == MeasureSpec.EXACTLY
                            || selfHeightMode == MeasureSpec.AT_MOST) {
                        childHeightSpec = MeasureSpec.makeMeasureSpec(selfHeightSize,
                                MeasureSpec.AT_MOST);
                    } else {
                        childHeightSpec = MeasureSpec.makeMeasureSpec(selfHeightSize,
                                MeasureSpec.UNSPECIFIED);
                    }
                    break;
                default:
                    childHeightSpec = MeasureSpec.makeMeasureSpec(selfHeightSize, MeasureSpec.EXACTLY);
                    break;
            }
            childWidthSpec = MeasureSpec.makeMeasureSpec(selfWidthSize / visibleItem, MeasureSpec.AT_MOST);
            measureChild(child, childWidthSpec, childHeightSpec);
        }
        int maxHeight = selfHeightSize;
        for (int i = 0;i < getChildCount();i++) {
            maxHeight = Math.max(maxHeight,getChildAt(i).getMeasuredHeight());
        }
        setMeasuredDimension(selfWidthSize,selfHeightMode == MeasureSpec.EXACTLY?
                selfHeightSize:maxHeight);
    }

    /**
     * 从左到右安排子view，每个子 view相当于在一个等分的block里
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childCount = getChildCount();
        int selfWidth = getWidth();
        int selfHeight = getHeight();
        int blockWidth = selfWidth / visibleItem;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            child.layout(i * blockWidth + (blockWidth - childWidth) / 2,
                    (selfHeight - childHeight) / 2,
                    i * blockWidth + (blockWidth - childWidth) / 2 + childWidth,
                    selfHeight - (selfHeight - childHeight) / 2);
        }
    }

    float lastX,lastY;
    float firstX,firstY;
    boolean isMoving;

    /**
     * 监听触摸事件，根据滑动距离滚动view，要滑出子view分布范围时不再滑动
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                firstX = event.getRawX();
                firstY = event.getRawY();
                lastX = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isMoving) {
                    if (Math.abs(event.getRawX() - firstX) > Math.abs(event.getRawY() - firstY)
                            && Math.abs(event.getRawX() - firstX) > TOUCH_SLOP) {
                        isMoving = true;
                    }
                    lastX = event.getRawX();
                }
                if (isMoving) {
                    float offset = event.getRawX() - lastX;
                    if (getScrollX() - offset < 0) {
                        if (offset > 0) {
                            offset = 0;
                        }
                    }
                    if (getScrollX() - offset > getChildCount() * getWidth() / visibleItem - getWidth()) {
                        if (offset < 0) {
                            offset = 0;
                        }
                    }
                    scrollBy(-(int)offset,0);
                }
                lastX = event.getRawX();
                lastY = event.getRawY();
                break;
             case MotionEvent.ACTION_CANCEL:
                 isMoving = false;
                 break;
        }
       return true;
    }
}
