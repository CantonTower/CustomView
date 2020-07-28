package com.longrise.androidcustomviewdemo.inputnumber;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.longrise.androidcustomviewdemo.R;

public class InputNumberView extends LinearLayout {
    private int mCurrentNumber = 0;
    private TextView mtvPlus;
    private EditText metValue;
    private TextView mtvMinus;

    private int mMax;
    private int mMin;
    private int mStep;
    private int mDefaultValue;
    private boolean mDisable;
    private int mBtnBgRes;

    private OnNumberChangeListener mListener;

    public InputNumberView(Context context) {
        this(context, null);
    }

    public InputNumberView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputNumberView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs); // 获取自定义属性
        initView(context); // 初始化布局
        setUpEvent(); // 设置事件
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.InputNumberView);
        mMax = a.getInt(R.styleable.InputNumberView_max, 10);
        mMin = a.getInt(R.styleable.InputNumberView_min, -10);
        mStep = a.getInt(R.styleable.InputNumberView_step, 1);
        mDefaultValue = a.getInt(R.styleable.InputNumberView_defaultValue, 0);
        mDisable = a.getBoolean(R.styleable.InputNumberView_disable, false);
        mBtnBgRes = a.getResourceId(R.styleable.InputNumberView_btnBackground, -1);
        a.recycle(); // 使用完后，记得回收

        mCurrentNumber = mDefaultValue;
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_input_number, this, false);
        this.addView(view);

        mtvPlus = findViewById(R.id.tv_plus);
        metValue = findViewById(R.id.et_value);
        mtvMinus = findViewById(R.id.tv_minus);

        updateText();
        mtvPlus.setEnabled(!mDisable); // 对应selector_input_num_left_bg的state_enabled属性
        mtvMinus.setEnabled(!mDisable); // 对应selector_input_num_right_bg的state_enabled属性
    }

    private void setUpEvent() {
        mtvPlus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mtvMinus.setEnabled(true);
                mCurrentNumber = mCurrentNumber + mStep;
                if (mCurrentNumber > mMax) {
                    mCurrentNumber = mCurrentNumber - mStep;
                    v.setEnabled(false);
                    return;
                }
                updateText();
            }
        });

        mtvMinus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mtvPlus.setEnabled(true);
                mCurrentNumber = mCurrentNumber - mStep;
                if (mCurrentNumber < mMin) {
                    mCurrentNumber = mCurrentNumber + mStep;
                    v.setEnabled(false);
                    return;
                }
                updateText();
            }
        });
    }

    public int getNumber() {
        return mCurrentNumber;
    }

    public void setValue(int value) {
        this.mCurrentNumber = value;
        updateText();
    }

    private void updateText() {
        metValue.setText(String.valueOf(mCurrentNumber));
        callNumberChangeListener(mCurrentNumber);
    }

    public void setOnNumberChangeListener(OnNumberChangeListener listener) {
        this.mListener = listener;
    }

    private void callNumberChangeListener(int value) {
        if (mListener != null) {
            mListener.onNumberChange(value);
        }
    }

    public interface OnNumberChangeListener {
        void onNumberChange(int value);
    }

    public TextView getMtvPlus() {
        return mtvPlus;
    }

    public void setMtvPlus(TextView mtvPlus) {
        this.mtvPlus = mtvPlus;
    }

    public EditText getMetValue() {
        return metValue;
    }

    public void setMetValue(EditText metValue) {
        this.metValue = metValue;
    }

    public TextView getMtvMinus() {
        return mtvMinus;
    }

    public void setMtvMinus(TextView mtvMinus) {
        this.mtvMinus = mtvMinus;
    }

    public int getmMax() {
        return mMax;
    }

    public void setmMax(int mMax) {
        this.mMax = mMax;
    }

    public int getmMin() {
        return mMin;
    }

    public void setmMin(int mMin) {
        this.mMin = mMin;
    }

    public int getmStep() {
        return mStep;
    }

    public void setmStep(int mStep) {
        this.mStep = mStep;
    }

    public int getmDefaultValue() {
        return mDefaultValue;
    }

    public void setmDefaultValue(int defaultValue) {
        this.mDefaultValue = defaultValue;
        mCurrentNumber = defaultValue;
        updateText();
    }

    public boolean ismDisable() {
        return mDisable;
    }

    public void setmDisable(boolean mDisable) {
        this.mDisable = mDisable;
    }

    public int getmBtnBgRes() {
        return mBtnBgRes;
    }

    public void setmBtnBgRes(int mBtnBgRes) {
        this.mBtnBgRes = mBtnBgRes;
    }
}
