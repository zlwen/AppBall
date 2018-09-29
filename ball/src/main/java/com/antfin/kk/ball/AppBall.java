package com.antfin.kk.ball;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * DESCRIPTION : simple app scope float ball, no permission required.
 * DATE        : 2018/9/29
 */
public class AppBall {
    private static final String FLAG = "BALL";
    private static final float[] LAST_POSITION = new float[2];

    public static class Config {
        public static final int DEFAULT_ICON_SIZE = 200;
        public static final int DEFAULT_TEXT_SIZE = 75;
        public static final int DEFAULT_TEXT_COLOR = Color.WHITE;
        public static final String DEFAULT_TEXT = "Go";
        public int iconSize;
        public int textSize;
        public int textColor;
        public String text;
        public Drawable icon;
        public View.OnClickListener clickListener;

        public static Config DEFAULT = new Config() {
            {
                iconSize = DEFAULT_ICON_SIZE;
                textSize = DEFAULT_TEXT_SIZE;
                textColor = DEFAULT_TEXT_COLOR;
                text = DEFAULT_TEXT;
                clickListener = (v) -> {
                    Toast.makeText(v.getContext(), "ball click", Toast.LENGTH_SHORT).show();
                };
            }
        };
    }

    public static void init(Application app) {
        init(app, Config.DEFAULT);
    }

    public static void init(Application app, Config config) {
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            private boolean hasBall(Activity activity) {
                FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView();
                int childCount = decorView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View view = decorView.getChildAt(i);
                    Object tag = view.getTag();
                    if (tag instanceof String) {
                        if (FLAG.equals(tag)) {
                            view.setX(LAST_POSITION[0]);
                            view.setY(LAST_POSITION[1]);
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (hasBall(activity)) {
                    return;
                }
                FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView();
                BallView ball = new BallView(activity, config);
                decorView.addView(ball);
                ball.setX(LAST_POSITION[0]);
                ball.setY(LAST_POSITION[1]);
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    private static class MovableViewTouchDelegate extends TouchDelegate {
        private View delegateView;
        private float dX;
        private float dY;
        private int[] SCREEN_SIZE = new int[2];

        public MovableViewTouchDelegate(View delegateView) {
            super(new Rect(), delegateView);
            this.delegateView = delegateView;
            Context context = delegateView.getContext();
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics metrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(metrics);
            SCREEN_SIZE[0] = metrics.widthPixels;
            SCREEN_SIZE[1] = metrics.heightPixels;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x, y;
            float maxX = SCREEN_SIZE[0] - delegateView.getWidth();
            float maxY = SCREEN_SIZE[1] - delegateView.getHeight();
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    dX = delegateView.getX() - event.getRawX();
                    dY = delegateView.getY() - event.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    x = event.getRawX() + dX;
                    y = event.getRawY() + dY;
                    x = x < 0 ? 0 : x > maxX ? maxX : x;
                    y = y < 0 ? 0 : y > maxY ? maxY : y;
                    delegateView.setX(x);
                    delegateView.setY(y);
                    break;

                case MotionEvent.ACTION_UP:
                    x = delegateView.getX();
                    y = delegateView.getY();
                    LAST_POSITION[0] = x;
                    LAST_POSITION[1] = y;
                    break;
            }
            return false;
        }
    }

    private static class BallView extends ImageView {
        private Paint paint;
        private float textBaseY;
        private int textSize;
        private int textColor;
        private String text;

        public BallView(Context context, Config config) {
            super(context);
            this.text = config.text;
            this.textSize = config.textSize;
            this.textColor = config.textColor;
            this.setLayoutParams(new FrameLayout.LayoutParams(config.iconSize, config.iconSize));
            this.setTag(FLAG);
            this.setTouchDelegate(new MovableViewTouchDelegate(this));
            this.setOnClickListener(config.clickListener);
            if (config.icon != null) {
                this.setBackground(config.icon);
            } else {
                Resources resources = getContext().getResources();
                Drawable drawable = resources.getDrawable(R.drawable.icon);
                this.setBackground(drawable);
            }
            init();
        }

        private void init() {
            paint = new Paint();
            paint.setFakeBoldText(true);
            paint.setFilterBitmap(false);
            paint.setAntiAlias(true);
            paint.setTextSize(textSize);
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            textBaseY = (fontMetrics.bottom - fontMetrics.ascent) / 2 - fontMetrics.bottom;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (TextUtils.isEmpty(text)) {
                return;
            }
            paint.setColor(textColor < 0 ? Color.WHITE : textColor);
            paint.setTextSize(textSize < 0 ? Config.DEFAULT_TEXT_SIZE : textSize);
            float tmpWidth = paint.measureText(text);
            canvas.drawText(text, (getWidth() - tmpWidth) / 2, getHeight() / 2 + textBaseY, paint);
        }
    }
}
