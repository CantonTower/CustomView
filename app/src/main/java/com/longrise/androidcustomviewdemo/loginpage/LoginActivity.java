package com.longrise.androidcustomviewdemo.loginpage;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.longrise.androidcustomviewdemo.R;

/**
 * 登录界面
 */

public class LoginActivity extends AppCompatActivity {
    private LoginPageView mLoginPageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        initView();
        initEvent();
    }

    private void initView() {
        mLoginPageView = findViewById(R.id.view_login_page);
    }

    private void initEvent() {
        mLoginPageView.setOnLoginPageActionListener(new LoginPageView.OnLoginPageActionListener() {
            @Override
            public void onGetVerifyCodeClick(String phoneNum) {

            }

            @Override
            public void onOpenAgreementClick() {

            }

            @Override
            public void onConfirmClick(String verifyCode, String phoneNum) {

            }
        });
    }
}
