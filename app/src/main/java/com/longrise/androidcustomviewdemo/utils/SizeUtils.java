package com.longrise.androidcustomviewdemo.utils;


import com.longrise.androidcustomviewdemo.App;

public class SizeUtils {

    public static int dp2px(float dpValue) {
        float scale = App.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
