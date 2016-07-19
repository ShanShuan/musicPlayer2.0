package cn.wangzifeng.musicplayer.ui;

import java.util.Random;

/**
 * 闅忔満鐢熸垚鍣� * <p/>
 * Created by wangchenlong on 16/1/24.
 */
public class RandomGenerator {
    private static final Random RANDOM = new Random();

    // 鍖洪棿闅忔満
    public float getRandom(float lower, float upper) {
        float min = Math.min(lower, upper);
        float max = Math.max(lower, upper);
        return getRandom(max - min) + min;
    }

    // 涓婄晫闅忔満
    public float getRandom(float upper) {
        return  RANDOM.nextFloat() * upper;
    }

    // 涓婄晫闅忔満
    public int getRandom(int upper) {
        return RANDOM.nextInt(upper);
    }
}

