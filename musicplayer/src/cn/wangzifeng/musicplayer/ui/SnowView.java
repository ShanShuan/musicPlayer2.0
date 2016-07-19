package cn.wangzifeng.musicplayer.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 雪花视图, DELAY时间重绘, 绘制NUM_SNOWFLAKES个雪花
 * Created by wangchenlong on 16/1/24.
 */
public class SnowView extends View {

    private static final int NUM_SNOWFLAKES = 150; // // 雪花数量
    private static final int DELAY = 5; // 延迟
    private SnowFlake[] mSnowFlakes; // 雪花

    public SnowView(Context context) {
        super(context);
    }

    public SnowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SnowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            initSnow(w, h);
        }
    }

    private void initSnow(int width, int height) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG); // 鎶楅敮榻�        paint.setColor(Color.WHITE); // 鐧借壊闆姳
        paint.setStyle(Paint.Style.FILL); // 濉厖;
        mSnowFlakes = new SnowFlake[NUM_SNOWFLAKES];
        for (int i = 0; i < NUM_SNOWFLAKES; ++i) {
            mSnowFlakes[i] = SnowFlake.create(width, height, paint);
        }
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (SnowFlake s : mSnowFlakes) {
            s.draw(canvas);
        }
        // 闅斾竴娈垫椂闂撮噸缁樹竴娆� 鍔ㄧ敾鏁堟灉
        getHandler().postDelayed(runnable, DELAY);
    }

    // 閲嶇粯绾跨▼
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };
}
