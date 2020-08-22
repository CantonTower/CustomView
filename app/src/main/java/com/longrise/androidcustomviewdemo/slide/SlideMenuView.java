package com.longrise.androidcustomviewdemo.slide;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;

import com.longrise.androidcustomviewdemo.R;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

public class SlideMenuView extends ViewGroup implements View.OnClickListener {
    private View mContentView;
    private View mEditView;

    private float mDownX;
    private boolean isOpen = false; // 是否已经打开
    private float mInterceptDownX;
    private Direction mCurrentDirect = Direction.NONE;
    private Scroller mScroller;
    private OnEditClickListener mListener;

    private final int DURATION = 700; // 走完的mEditView 4/5宽度所需要的时间
    private final int MIN_DURATION = 300; // 最少的时间

    enum Direction {
        LEFT,RIGHT, NONE,
    }

    public SlideMenuView(Context context) {
        this(context, null);
    }

    public SlideMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SlideMenuView);
        int function = array.getInt(R.styleable.SlideMenuView_function, 0);
        array.recycle();
        mScroller = new Scroller(context);
    }

    /**
     * 当布局加载完成后调用此方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount == 1) {
            mContentView = this.getChildAt(0);
            mEditView = LayoutInflater.from(getContext()).inflate(R.layout.item_slide_action, this, false);
            this.addView(mEditView);
            initEditView();
        } else {
            // 当有多个孩子时，不能使用此滑动菜单控件
        }
    }

    private void initEditView() {
        TextView tvTop = mEditView.findViewById(R.id.tv_top);
        TextView tvDelete = mEditView.findViewById(R.id.tv_delete);
        TextView tvRead = mEditView.findViewById(R.id.tv_read);

        tvTop.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        tvRead.setOnClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        // 测量第一个孩子，也就是内容部分
        LayoutParams contentLayoutParams = mContentView.getLayoutParams();
        int contentHeight = contentLayoutParams.height;
        int contentHeightMeasureSpace;
        if (contentHeight == LayoutParams.MATCH_PARENT) {
            contentHeightMeasureSpace = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        } else if (contentHeight == LayoutParams.WRAP_CONTENT) {
            contentHeightMeasureSpace = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST);
        } else {
            // 指定大小
            contentHeightMeasureSpace = MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY);
        }
        mContentView.measure(widthMeasureSpec, contentHeightMeasureSpace);

        // 拿到内容部分测量之后的宽度
        int contentMeasureHeight = mContentView.getMeasuredHeight();
        int editWidthSize = widthSize * 3 / 4;
        int editWidthMeasureSpace = MeasureSpec.makeMeasureSpec(editWidthSize, MeasureSpec.EXACTLY);
        int editHeightMeasureSpace = MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY);
        mEditView.measure(editWidthMeasureSpace, editHeightMeasureSpace);

        // 设置此容器宽高
        setMeasuredDimension(widthSize + editWidthSize, contentMeasureHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int contentLeft = 0;
        int contentTop = 0;
        int contentRight = contentLeft + mContentView.getMeasuredWidth();
        int contentBottom = contentTop + mContentView.getMeasuredHeight();

        int editLeft = contentRight;
        int editTop = 0;
        int editRight = editLeft + mEditView.getMeasuredWidth();
        int editBottom = editTop + mEditView.getMeasuredHeight();

        mContentView.layout(contentLeft, contentTop, contentRight, contentBottom);
        mEditView.layout(editLeft, editTop, editRight, editBottom);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 选择拦截
        // 如果横向滑动，就拦截，否则就不拦截
        switch (ev.getAction()) {
            case ACTION_DOWN:
                mInterceptDownX = ev.getX();
                Log.i("main", "onInterceptTouchEvent.mInterceptDownX-->" + mInterceptDownX);
                break;
            case ACTION_MOVE:
                float x = ev.getX();
                if (Math.abs(x - mInterceptDownX) > 0) {
                    // 自己消费
                    Log.i("main", "自己消费");
                    return true;
                }
                break;
            case ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case ACTION_DOWN:
                mDownX = event.getX();
                Log.i("main", "onTouchEvent.mDownX-->" + mDownX);
                break;
            case ACTION_MOVE:
                int scrollX = getScrollX(); // 已经滑动的值
                float moveX = event.getX();
                // 每次微小移动的值
                int dx = (int) (moveX - mInterceptDownX);
                Log.i("main", "moveX==>" + moveX);
                Log.i("main", "mDownX==>" + mDownX);
                Log.i("main", "dx==>" + dx);
                if (dx > 0) {
                    mCurrentDirect = Direction.RIGHT;
                } else {
                    mCurrentDirect = Direction.LEFT;
                }
                mInterceptDownX = moveX;
                // 判断边界
                int resultScrollX = scrollX - dx;
                if (resultScrollX <= 0) {
                    // 防止左边划出边界
                    scrollTo(0, 0);
                } else if (resultScrollX > mEditView.getMeasuredWidth()) {
                    // 防止右边划出边界
                    scrollTo(mEditView.getMeasuredWidth(), 0);
                } else {
                    // 滑动
                    scrollBy(-dx, 0);
                }
                break;
            case ACTION_UP:
                float x2 = event.getX();
                float y2 = event.getY();
                // 处理释放以后，是显示还是收缩回去
                // 两个关注点
                // 是否已经打开
                // 方向
                int hasBeanScrollX = getScrollX(); // 已经滑动的值
                int editViewWidth = mEditView.getMeasuredWidth();
                if (isOpen) {
                    // 当前状态打开
                    if (mCurrentDirect == Direction.RIGHT) {
                        // 向右滑动，判断滑动距离
                        if (hasBeanScrollX < editViewWidth* 3 / 4) {
                            close();
                        } else {
                            open();
                        }
                    } else if (mCurrentDirect == Direction.LEFT) {
                        // 向左滑动
                        open();
                    }
                } else {
                    // 当前状态关闭
                    if (mCurrentDirect == Direction.LEFT) {
                        // 向左滑动，判断滑动距离
                        if (hasBeanScrollX > editViewWidth / 4) {
                            open();
                        } else {
                            close();
                        }
                    } else if (mCurrentDirect == Direction.RIGHT) {
                        // 向右滑动
                        close();
                    }
                }
                break;
        }
        return true;
    }

    public void open() {
        // 显示
        // 瞬间移动
        // scrollTo(mEditView.getMeasuredWidth(), 0);
        int dx = mEditView.getMeasuredWidth() - getScrollX();
        int duration = (int) ((dx / (mEditView.getMeasuredWidth() * 4 / 5f)) * DURATION);
        int absDuration = Math.abs(duration);
        if (absDuration < MIN_DURATION) {
            absDuration = MIN_DURATION;
        }
        // 平滑移动
        mScroller.startScroll(getScrollX(), 0, dx, 0, absDuration);
        invalidate();
        isOpen = true;
    }

    public void close() {
        // 隐藏
        // 瞬间移动
        // scrollTo(0, 0);
        int duration = (getScrollX() / (mEditView.getMeasuredWidth() * 4 / 5)) * DURATION;
        int absDuration = Math.abs(duration);
        if (absDuration < MIN_DURATION) {
            absDuration = MIN_DURATION;
        }
        // 平滑移动
        mScroller.startScroll(getScrollX(), 0, -getScrollX(), absDuration);
        invalidate();
        isOpen = false;
    }

    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            invalidate();
        }
    }

    @Override
    public void onClick(View v) {
        close();
        switch (v.getId()) {
            case R.id.tv_top:
                if (mListener != null) {
                    mListener.onTopClick();
                }
                break;
            case R.id.tv_delete:
                if (mListener != null) {
                    mListener.onDeleteClick();
                }
                break;
            case R.id.tv_read:
                if (mListener != null) {
                    mListener.onReadClick();
                }
                break;
        }
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        mListener = listener;
    }

    public interface OnEditClickListener {
        void onReadClick();

        void onDeleteClick();

        void onTopClick();
    }
}
