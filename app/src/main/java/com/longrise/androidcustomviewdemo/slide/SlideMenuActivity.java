package com.longrise.androidcustomviewdemo.slide;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.longrise.androidcustomviewdemo.R;

public class SlideMenuActivity extends AppCompatActivity implements SlideMenuView.OnEditClickListener {
    private SlideMenuView mSlideMenuView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);
        initView();
    }

    private void initView() {
        mSlideMenuView = findViewById(R.id.slide_menu_view);

        mSlideMenuView.setOnEditClickListener(this);
    }

    @Override
    public void onReadClick() {
        Log.i("main", "isOpen-->" + mSlideMenuView.isOpen());
        Toast.makeText(this, "onReadClick", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick() {
        Log.i("main", "isOpen-->" + mSlideMenuView.isOpen());
        Toast.makeText(this, "onDeleteClick", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTopClick() {
        Log.i("main", "isOpen-->" + mSlideMenuView.isOpen());
        Toast.makeText(this, "onTopClick", Toast.LENGTH_SHORT).show();
    }
}
