package com.redefine.welike.common;


import com.redefine.welike.business.startup.management.bean.SplashEntity;

import java.util.List;

/**
 * Created by honglin on 2018/5/25.
 */

public class WeightRandom {

    /**
     *
     * @return int
     */
    public static int PercentageRandom(List<SplashEntity> prizes) {
        int random = 0;
        try {
            double sumWeight = 0;
            //计算总权重
            for (SplashEntity rp_1 : prizes) {
                sumWeight += rp_1.getWeight();
            }
            int randomNumber;
            randomNumber = (int) (Math.random() * sumWeight);
            int d1 = 0;
            int d2 = 0;

            for (int i = 0; i < prizes.size(); i++) {
                d2 += prizes.get(i).getWeight();
                if (i == 0) {
                    d1 = 0;
                } else {
                    d1 += (prizes.get(i - 1).getWeight());
                }
                if (randomNumber >= d1 && randomNumber <= d2) {
                    random = i;
                    break;
                }
            }
        } catch (Exception e) {
//            System.out.println(e.getMessage());
            random = 0;
        }
        return random;
    }


}
