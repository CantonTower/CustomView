package com.longrise.androidcustomviewdemo.inputnumber;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.longrise.androidcustomviewdemo.R;
import com.longrise.androidcustomviewdemo.flow.FlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 计数器页面
 */



public class InputNumberActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_number);
        FlowLayout flowLayout = findViewById(R.id.layout_flow);
        List<String> data = new ArrayList<>();
        data.add("键盘");
        data.add("显示器");
        data.add("鼠标");
        data.add("iPad");
        data.add("air pod");
        data.add("Android手机");
        data.add("mac pro");
        data.add("耳机");
        data.add("春夏秋冬超帅装");
        data.add("男装");
        data.add("女装");
        data.add("键盘");
        data.add("显示器");
        data.add("鼠标");
        data.add("iPad");
        data.add("air pod");
        data.add("Android手机");
        data.add("mac pro");
        data.add("耳机");
        data.add("春夏秋冬超帅装");
        data.add("男装");
        data.add("女装");
        flowLayout.setData(data);
    }
}
