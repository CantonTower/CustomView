package com.longrise.androidcustomviewdemo.loginpage;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.longrise.androidcustomviewdemo.App;
import com.longrise.androidcustomviewdemo.R;

/**
 * 登录页面(包含LoginKeyBoardView)
 */

/**
 * 条件(点击获取验证码) --> 手机号码正确
 * 条件(点击登录) --> 手机号码正确 + 验证码格式正确 + 同意了协议
 */

public class LoginPageView extends FrameLayout {
    private int mMainColor;
    private int mVerifyCodeSize;
    private View mView;
    private CheckBox mCheckBoxIsConfirm;
    private LoginKeyboardView mKeyBoardView;
    private EditText metPhoneNumInp;
    private EditText metVerifyInp;
    private TextView mtvGetVerifyCode;
    private TextView mtvLogin;
    private OnLoginPageActionListener mListener;
    private boolean mIsPhoneNumOk = false; // 手机号码是否正确
    private boolean mIsAgreementOk = false; // 是否同意协议
    private boolean mIsVerifyCodeOk = false; // 验证码格式是否正确

    private int mTotalDuration = 60 * 1000; // 倒计时总时长
    private int mDTime = 1000; // 倒计时每次计数间隔时长
    private int mRestTime = 0; // 当前的倒计时时间
    private boolean mIsCountDowning = false; // 是否正处于倒计时的状态
    private Handler mHandler;

    //手机号码的规则
    public static final String REGEX_MOBILE_EXACT = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|(147))\\d{8}$";

    public LoginPageView(@NonNull Context context) {
        this(context, null);
    }

    public LoginPageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoginPageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initView(context);
        disableEdtFocus2keyBoard();
        initEvent();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoginPageView);
        mMainColor = a.getColor(R.styleable.LoginPageView_mainColor, -1);
        mVerifyCodeSize = a.getInt(R.styleable.LoginPageView_verifyCodeSize, 8);
        a.recycle();
    }

    private void initView(Context context) {
        mView = LayoutInflater.from(context).inflate(R.layout.view_login_page, this, true);
        mCheckBoxIsConfirm = findViewById(R.id.checkbox_report);
        mKeyBoardView = findViewById(R.id.view_key_board);
        metPhoneNumInp = findViewById(R.id.et_input_phone_number);
        metVerifyInp = findViewById(R.id.et_input_verify_code);
        mtvGetVerifyCode = findViewById(R.id.tv_get_verify_code);
        mtvLogin = findViewById(R.id.tv_login);
        if (mMainColor != -1) {
            mCheckBoxIsConfirm.setTextColor(mMainColor);
        }
        metPhoneNumInp.requestFocus();
        updateAllBtnState();
    }

    private void disableEdtFocus2keyBoard() {
        metPhoneNumInp.setShowSoftInputOnFocus(false);
        metVerifyInp.setShowSoftInputOnFocus(false);
    }

    private void initEvent() {
        mKeyBoardView.setOnKeyPressListener(new LoginKeyboardView.OnKeyPressListener() {
            @Override
            public void onNumberPress(int number) {
                // 数字被点击
                EditText focusEdt = getFocusEdt();
                if (focusEdt != null) {
                    Editable text = focusEdt.getText();
                    int endIndex = focusEdt.getSelectionEnd();
                    text.insert(endIndex, String.valueOf(number));
                }
            }

            @Override
            public void onBackPress() {
                // 返回键被点击
                EditText focusEdt = getFocusEdt();
                if (focusEdt != null) {
                    Editable text = focusEdt.getText();
                    int endIndex = focusEdt.getSelectionEnd();
                    if (endIndex != 0) {
                        text.delete(endIndex - 1, endIndex);
                    }
                }
            }
        });

        metPhoneNumInp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phoneNum = s.toString();
                boolean matches = phoneNum.matches(REGEX_MOBILE_EXACT);
                mIsPhoneNumOk = phoneNum.length() == 11 && matches;
                updateAllBtnState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        metVerifyInp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mIsVerifyCodeOk = s.toString().length() == 4;
                updateAllBtnState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mCheckBoxIsConfirm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsAgreementOk = isChecked;
                updateAllBtnState();
            }
        });

        mtvGetVerifyCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onGetVerifyCodeClick(getPhoneNum());
                }
                mRestTime = mTotalDuration;
                /**
                 * 此处开始计时
                 *
                 * 也可以使用CountDownTimer类去倒计时，这个类专门用于倒计时
                 */
                startCountDown();
            }
        });

        mtvLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onConfirmClick(getVerifyCode(), getPhoneNum());
                }
            }
        });
    }

    private void updateAllBtnState() {
        if (!mIsCountDowning) {
            mtvGetVerifyCode.setEnabled(mIsPhoneNumOk);
        }
        mCheckBoxIsConfirm.setEnabled(mIsPhoneNumOk && mIsVerifyCodeOk);
        mtvLogin.setEnabled(mIsPhoneNumOk && mIsVerifyCodeOk && mIsAgreementOk);
    }

    private void startCountDown() {
        mHandler = App.getHandler();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mRestTime = mRestTime - mDTime;
                if (mRestTime > 0) {
                    mHandler.postDelayed(this, mDTime);
                    mtvGetVerifyCode.setText("(" + mRestTime / 1000 + ")秒");
                    mtvGetVerifyCode.setEnabled(false);
                    mIsCountDowning = true;
                } else {
                    mtvGetVerifyCode.setText("获取验证码");
                    mtvGetVerifyCode.setEnabled(true);
                    mIsCountDowning = false;
                }
            }
        });
    }

    /**
     * 获取当前有焦点的输入框
     *
     * 使用要注意判空
     *
     * @return null or editText instance
     */
    private EditText getFocusEdt() {
        View view = findFocus();
        if (view instanceof EditText) {
            return ((EditText) view);
        }
        return null;
    }

    private String getPhoneNum() {
        return metPhoneNumInp.getText().toString().trim();
    }

    private String getVerifyCode() {
        return metVerifyInp.getText().toString().trim();
    }

    public int getmMainColor() {
        return mMainColor;
    }

    public void setmMainColor(int mMainColor) {
        this.mMainColor = mMainColor;
    }

    public int getmVerifyCodeSize() {
        return mVerifyCodeSize;
    }

    public void setmVerifyCodeSize(int mVerifyCodeSize) {
        this.mVerifyCodeSize = mVerifyCodeSize;
    }

    public void setOnLoginPageActionListener(OnLoginPageActionListener listener) {
        mListener = listener;
    }

    public interface OnLoginPageActionListener {

        void onGetVerifyCodeClick(String phoneNum); // 点击获取验证码

        void onOpenAgreementClick(); // 点击同意(协议)

        void onConfirmClick(String verifyCode, String phoneNum); // 点击登录按钮

    }
}
