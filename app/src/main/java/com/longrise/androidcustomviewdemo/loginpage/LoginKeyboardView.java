package com.longrise.androidcustomviewdemo.loginpage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.longrise.androidcustomviewdemo.R;

/**
 * 登录页的键盘
 */

public class LoginKeyboardView extends LinearLayout implements View.OnClickListener {
    private OnKeyPressListener mKeyPressListener;

    public LoginKeyboardView(Context context) {
        this(context, null);
    }

    public LoginKeyboardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoginKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.num_key_board, this, false);
        this.addView(view);
        initView();
    }

    private void initView() {
        findViewById(R.id.tv_number_1).setOnClickListener(this);
        findViewById(R.id.tv_number_2).setOnClickListener(this);
        findViewById(R.id.tv_number_3).setOnClickListener(this);
        findViewById(R.id.tv_number_4).setOnClickListener(this);
        findViewById(R.id.tv_number_5).setOnClickListener(this);
        findViewById(R.id.tv_number_6).setOnClickListener(this);
        findViewById(R.id.tv_number_7).setOnClickListener(this);
        findViewById(R.id.tv_number_8).setOnClickListener(this);
        findViewById(R.id.tv_number_9).setOnClickListener(this);
        findViewById(R.id.tv_number_0).setOnClickListener(this);
        findViewById(R.id.tv_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_number_1:
                callNumberPressListener(1);
                break;
            case R.id.tv_number_2:
                callNumberPressListener(2);
                break;
            case R.id.tv_number_3:
                callNumberPressListener(3);
                break;
            case R.id.tv_number_4:
                callNumberPressListener(4);
                break;
            case R.id.tv_number_5:
                callNumberPressListener(5);
                break;
            case R.id.tv_number_6:
                callNumberPressListener(6);
                break;
            case R.id.tv_number_7:
                callNumberPressListener(7);
                break;
            case R.id.tv_number_8:
                callNumberPressListener(8);
                break;
            case R.id.tv_number_9:
                callNumberPressListener(9);
                break;
            case R.id.tv_number_0:
                callNumberPressListener(0);
                break;
            case R.id.tv_back:
                if (mKeyPressListener != null) {
                    mKeyPressListener.onBackPress();
                }
                break;
        }
    }

    public void setOnKeyPressListener(OnKeyPressListener listener) {
        mKeyPressListener = listener;
    }

    private void callNumberPressListener(int number) {
        if (mKeyPressListener != null) {
            mKeyPressListener.onNumberPress(number);
        }
    }

    public interface OnKeyPressListener {

        void onNumberPress(int number);

        void onBackPress();
    }
}
