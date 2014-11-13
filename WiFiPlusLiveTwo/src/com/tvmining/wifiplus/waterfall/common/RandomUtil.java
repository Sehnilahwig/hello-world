package com.tvmining.wifiplus.waterfall.common;

import java.util.Random;

/**
 * @author : 桥下一粒砂
 * @email  : chenyoca@gmail.com
 * @date   : 2012-11-14
 * @desc   : 随机数据生成工具
 */
public class RandomUtil {
	
	public static String rand(int length) {
        StringBuffer buffer = new StringBuffer();
        for (int t = 1; t < length; t++) {
            long time = System.currentTimeMillis() + t;
            if (time % 3 == 0) {
                buffer.append((char) time % 9);
            } else if (time % 3 == 1) {
                buffer.append((char) (65 + time % 26));
            } else {
                buffer.append((char) (97 + time % 26));
            }
        }
        return buffer.toString();
    }
	
	public static int limitInt(int limit){
		return Math.abs(new Random().nextInt(limit));
	}
	
}
