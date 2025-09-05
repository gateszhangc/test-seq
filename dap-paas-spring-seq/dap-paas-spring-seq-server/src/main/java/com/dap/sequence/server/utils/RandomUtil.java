package com.dap.sequence.server.utils;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @description: 生成随机数的工具类
 * @author: zhangce
 * @create: 9/30/2024 1:40 PM
 **/
public class RandomUtil {

    public static final Random random = new SecureRandom();

    public static double getNextDouble(double start, double end) {
        return start+(random.nextDouble()*(end-start));
    }

}
