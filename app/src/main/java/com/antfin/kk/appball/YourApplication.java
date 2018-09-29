package com.antfin.kk.appball;

import android.app.Application;

import com.antfin.kk.ball.AppBall;

/**
 * DATE        : 2018/9/29
 */
public class YourApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppBall.init(this); // You need add this line
    }
}
