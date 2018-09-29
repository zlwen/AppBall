package com.antfin.kk.appball;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * DATE        : 2018/9/29
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = new FrameLayout(this);
        Button button = new Button(this);
        button.setText("Go SecondActivity");
        button.setPadding(20, 20, 20, 20);
        button.setOnClickListener((v) -> {
            Intent intent = new Intent();
            intent.setClassName(this, SecondActivity.class.getName());
            startActivity(intent);
        });
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        frameLayout.addView(button, layoutParams);
        setContentView(frameLayout);
    }
}
