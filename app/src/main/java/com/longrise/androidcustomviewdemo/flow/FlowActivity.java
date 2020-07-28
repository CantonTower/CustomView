package com.longrise.androidcustomviewdemo.flow;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.longrise.androidcustomviewdemo.R;

import java.util.ArrayList;
import java.util.List;

public class FlowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow);
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

        flowLayout.setOnItemClickListener(new FlowLayout.OnItemClickListaner() {
            @Override
            public void onItemClick(View view, String text) {
                Toast.makeText(FlowActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
