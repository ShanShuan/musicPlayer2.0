package cn.wangzifeng.musicplayer.ui;

import android.R.color;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;


/**
 * 闆姳鐨勭被, 绉诲姩, 绉诲嚭灞忓箷浼氶噸鏂拌缃綅缃�
 * <p/>
 * Created by wangchenlong on 16/1/24.
 */
public class SnowFlake {
	 // 雪花的角度
	private static final float ANGE_RANGE = 0.1f; // 角度范围
    private static final float HALF_ANGLE_RANGE = ANGE_RANGE / 2f;  // 一般的角度 
    private static final float HALF_PI = (float) Math.PI / 2f; // 半PI
    private static final float ANGLE_SEED = 25f; // 角度随机种子
    private static final float ANGLE_DIVISOR = 10000f; // 角度的分母
 // 雪花的移动速度
    private static final float INCREMENT_LOWER = 1f;
    private static final float INCREMENT_UPPER = 4f;

    // 雪花的大小  
    private static final float FLAKE_SIZE_LOWER = 3f;
    private static final float FLAKE_SIZE_UPPER = 20f;

    private final RandomGenerator mRandom; // 随机控制器   
    private final Point mPosition; // 雪花位置
    private float mAngle; // 角度
    private final float mIncrement; // 雪花的速度   
    private final float mFlakeSize; // 雪花的大小    
    private final Paint mPaint; // 画笔

    private SnowFlake(RandomGenerator random, Point position, float angle, float increment, float flakeSize, Paint paint) {
        mRandom = random;
        mPosition = position;
        mIncrement = increment;
        mFlakeSize = flakeSize;
        mPaint = paint;
        mAngle = angle;
    }

    public static SnowFlake create(int width, int height, Paint paint) {
        RandomGenerator random = new RandomGenerator();
        int x = random.getRandom(width);
        int y = random.getRandom(height);
        Point position = new Point(x, y);
        float angle = random.getRandom(ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE;
        float increment = random.getRandom(INCREMENT_LOWER, INCREMENT_UPPER);
        float flakeSize = random.getRandom(FLAKE_SIZE_LOWER, FLAKE_SIZE_UPPER);
        return new SnowFlake(random, position, angle, increment, flakeSize, paint);
    }

    // 缁樺埗闆姳
    public void draw(Canvas canvas) {
    	mPaint.setColor(Color.WHITE);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        move(width, height);
        canvas.drawCircle(mPosition.x, mPosition.y, mFlakeSize, mPaint);
    }

    // 绉诲姩闆姳
    private void move(int width, int height) {
        double x = mPosition.x + (mIncrement * Math.cos(mAngle));
        double y = mPosition.y + (mIncrement * Math.sin(mAngle));

        mAngle += mRandom.getRandom(-ANGLE_SEED, ANGLE_SEED) / ANGLE_DIVISOR; // 闅忔満鏅冨姩

        mPosition.set((int) x, (int) y);

        // 绉婚櫎灞忓箷, 閲嶆柊寮�
        if (!isInside(width, height)) {
            reset(width);
        }
    }

    // 鍒ゆ柇鏄惁鍦ㄥ叾涓�    
    private boolean isInside(int width, int height) {
        int x = mPosition.x;
        int y = mPosition.y;
        return x >= -mFlakeSize - 1 && x + mFlakeSize <= width && y >= -mFlakeSize - 1 && y - mFlakeSize < height;
    }

    // 閲嶇疆闆姳
    private void reset(int width) {
        mPosition.x = mRandom.getRandom(width);
        mPosition.y = (int) (-mFlakeSize - 1); // 鏈�笂闈�        mAngle = mRandom.getRandom(ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE;
    }
}
