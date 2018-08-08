package com.hdychi.carousel.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.hdychi.carousel.R;

public class CarouselLayout extends ViewGroup {
    private static final int DEFAULT_WIDTH = 50;
    private static final int DEFULT_ITEM_COUNT = 5;
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
}
