package com.longrise.androidcustomviewdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.longrise.androidcustomviewdemo.inputnumber.InputNumberActivity;
import com.longrise.androidcustomviewdemo.loginpage.LoginActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mbtnCounter;
    private Button mbtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mbtnCounter = findViewById(R.id.btn_counter);
        mbtnLogin = findViewById(R.id.btn_login);
        mbtnCounter.setOnClickListener(this);
        mbtnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mbtnCounter) {
            startActivity(new Intent(this, InputNumberActivity.class));
        } else if (v == mbtnLogin) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
