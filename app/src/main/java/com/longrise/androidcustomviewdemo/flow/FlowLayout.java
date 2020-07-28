package com.longrise.androidcustomviewdemo.flow;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.longrise.androidcustomviewdemo.R;
import com.longrise.androidcustomviewdemo.utils.SizeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义ViewGroup的步骤
 */

/**
 * 1.继承自ViewGroup
 * 2.定义相关属性，并获取相关属性
 * 3.把子View添加进来
 * 4.测量：测量孩子和测量自己
 */

public class FlowLayout extends ViewGroup {
    private int mMaxLines;
    private int mHorizontalMargin;
    private int mVerticalMargin;
    private int mTextMaxLength;
    private int mTextColor;
    private int mBorderColor;
    private int mBorderRadius;
    private OnItemClickListaner mListener;

    private List<String> mData = new ArrayList<>();
    private List<List<View>> mLines = new ArrayList<>();

    private static final int DEFAULT_MAX_LINES = 3; // 默认最大行数
    private static final int DEFAULT_HORIZONTAL_MARGIN = SizeUtils.dp2px(5); // 默认横向item间距
    private static final int DEFAULT_VERTICAL_MARGIN = SizeUtils.dp2px(5); // 默认纵向item间距
    private static final int DEFAULT_TEXT_MAX_LENGTH = SizeUtils.dp2px(30); // 文字最大宽度
    private static final int DEFAULT_TEXT_COLOR = R.color.text_gray; // 默认字体颜色
    private static final int DEFAULT_BORDER_COLOR = R.color.text_gray; // 默认边框颜色
    private static final int DEFAULT_BORDER_RADIUS = SizeUtils.dp2px(5); // 默认边框圆角弧度


    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        mMaxLines = a.getInt(R.styleable.FlowLayout_maxLine, DEFAULT_MAX_LINES);
        mHorizontalMargin = (int) a.getDimension(R.styleable.FlowLayout_itemHorizontalMargin, DEFAULT_HORIZONTAL_MARGIN);
        mVerticalMargin = (int) a.getDimension(R.styleable.FlowLayout_itemVerticalMargin, DEFAULT_VERTICAL_MARGIN);
        mTextMaxLength = (int) a.getDimension(R.styleable.FlowLayout_textMaxLength, DEFAULT_TEXT_MAX_LENGTH);
        mTextColor = a.getColor(R.styleable.FlowLayout_textColor, getResources().getColor(DEFAULT_TEXT_COLOR));
        mBorderColor = a.getColor(R.styleable.FlowLayout_borderColor, getResources().getColor(DEFAULT_BORDER_COLOR));
        mBorderRadius = (int) a.getDimension(R.styleable.FlowLayout_borderRadius, DEFAULT_BORDER_RADIUS);
        a.recycle();
    }

    /**
     * widthMeasureSpec和heightMeasureSpec这两个值来自于父控件，包含值和模式
     * int类型-->4个字节(byte)-->4*6个位(bit)-->32位
     * int的前两位是模式，后面的30位是值
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        if (childCount == 0) {
            return;
        }

        int parentWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        int childWidthSpace = MeasureSpec.makeMeasureSpec(parentWidthSize, MeasureSpec.AT_MOST);
        int childHeightSpace = MeasureSpec.makeMeasureSpec(parentHeightSize, MeasureSpec.AT_MOST);

        mLines.clear();
        List<View> line = new ArrayList<>();
        mLines.add(line);
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != VISIBLE) {
                continue;
            }
            measureChild(child, childWidthSpace, childHeightSpace); // 测量孩子
            if (line.size() == 0) {
                // 可以添加
                line.add(child);
            } else {
                // 判断是否可以添加到当前行
                boolean canBeAdd = checkChildCanBeAdd(line, child, parentWidthSize);
                if (canBeAdd) {
                    line.add(child);
                } else {
                    line = new ArrayList<>();
                    line.add(child);
                    mLines.add(line);
                }
            }
        }

        // 根据尺寸计算所有行高
        View child = getChildAt(0);
        int childHeight = child.getMeasuredHeight();
        int parentHeightTargetSize = (childHeight + mVerticalMargin) * mLines.size() + mVerticalMargin;

        // 设置此容器宽高
        setMeasuredDimension(parentWidthSize, parentHeightTargetSize);
    }

    private boolean checkChildCanBeAdd(List<View> line, View child, int parentWidthSize) {
        int measureWidth = child.getMeasuredWidth();
        int totalWidth = 0;
        for (View view : line) {
            totalWidth = (totalWidth + view.getMeasuredWidth() + mHorizontalMargin);
        }
        totalWidth = (totalWidth + measureWidth + mHorizontalMargin);
        return parentWidthSize >= totalWidth;
    }

    /**
     * onLayout方法会执行多次，这一点需要注意
     *
     * getMeasureWidth方法会测量出此view的宽度
     * getRight方法会得到此view的右坐标
     *
     * view.layout方法执行前：
     * --view.getMeasureWidth是不变的，它表示此view的宽度
     * --view.getRight方法和view.getLeft方法的值是一样的，因为此view还没有放置
     *
     * view.layout方法执行后
     * --view.getMeasureWidth是不变的，它表示此view的宽度
     * --view.getRight方法等于view.getLeft + view.getMeasureWidth，此时view已经放置
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        View child1 = getChildAt(0);
//        child1.layout(0, 0, child1.getMeasuredWidth(), child1.getMeasuredHeight());
//        View child2 = getChildAt(1);
//        child2.layout(child1.getRight(), 0, child1.getRight() + child2.getMeasuredWidth(), child2.getMeasuredHeight());
        int currentLeft = mHorizontalMargin;
        int currentTop = mVerticalMargin;
        int currentRight = 0;
        int currentBottom = 0;
        int viewHeight = getChildAt(0).getMeasuredHeight();
        for (List<View> line : mLines) {
            currentBottom = currentTop + viewHeight;
            for (View view : line) {
                currentRight = currentLeft + view.getMeasuredWidth();
                view.layout(currentLeft, currentTop, currentRight, currentBottom);
                currentLeft = currentRight + mHorizontalMargin;
            }
            currentTop = currentBottom + mVerticalMargin;
            currentLeft = mHorizontalMargin;
        }
    }

    public void setData(List<String> data) {
        mData.clear();
        mData.addAll(data);
        setUpChildren(); // 根据数据创建子View，并且添加进来
    }

    private void setUpChildren() {
        removeAllViews(); // 先清空原来的内容
        // 添加子view
        for (String textStr : mData) {
            TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_flow_text, this, false);
            textView.setText(textStr);
            addView(textView);
            final String text =textStr;
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    callListener(v, text);
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListaner listener) {
        mListener = listener;
    }

    public void callListener(View view, String text) {
        if (mListener != null) {
            mListener.onItemClick(view, text);
        }
    }

    public interface OnItemClickListaner {
        void onItemClick(View view, String text);
    }
}
