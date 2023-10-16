package Funssion.Inforum.domain.score;

import Funssion.Inforum.common.exception.notfound.RankNotFoundException;

public enum Rank {
    BRONZE_5(100, Bronze.INTERVAL),
    BRONZE_4(BRONZE_5.getMax() + Bronze.INTERVAL, Bronze.INTERVAL),
    BRONZE_3(BRONZE_4.getMax() + Bronze.INTERVAL, Bronze.INTERVAL),
    BRONZE_2(BRONZE_3.getMax() + Bronze.INTERVAL, Bronze.INTERVAL),
    BRONZE_1(BRONZE_2.getMax() + Bronze.INTERVAL, Bronze.INTERVAL),
    SILVER_5(BRONZE_1.getMax() + Silver.INTERVAL, Silver.INTERVAL),
    SILVER_4(SILVER_5.getMax() + Silver.INTERVAL, Silver.INTERVAL),
    SILVER_3(SILVER_4.getMax() + Silver.INTERVAL, Silver.INTERVAL),
    SILVER_2(SILVER_3.getMax() + Silver.INTERVAL, Silver.INTERVAL),
    SILVER_1(SILVER_2.getMax() + Silver.INTERVAL, Silver.INTERVAL),
    GOLD_5(SILVER_1.getMax() + Gold.INTERVAL, Gold.INTERVAL),
    GOLD_4(GOLD_5.getMax() + Gold.INTERVAL, Gold.INTERVAL),
    GOLD_3(GOLD_4.getMax() + Gold.INTERVAL, Gold.INTERVAL),
    GOLD_2(GOLD_3.getMax() + Gold.INTERVAL, Gold.INTERVAL),
    GOLD_1(GOLD_2.getMax() + Gold.INTERVAL, Gold.INTERVAL),
    PLATINUM_5(GOLD_1.getMax() + Platinum.INTERVAL, Platinum.INTERVAL),
    PLATINUM_4(PLATINUM_5.getMax() + Platinum.INTERVAL, Platinum.INTERVAL),
    PLATINUM_3(PLATINUM_4.getMax() + Platinum.INTERVAL, Platinum.INTERVAL),
    PLATINUM_2(PLATINUM_3.getMax() + Platinum.INTERVAL, Platinum.INTERVAL),
    PLATINUM_1(PLATINUM_2.getMax() + Platinum.INTERVAL, Platinum.INTERVAL),
    DIAMOND_5(PLATINUM_1.getMax() + Diamond.INTERVAL, Diamond.INTERVAL),
    DIAMOND_4(DIAMOND_5.getMax() + Diamond.INTERVAL, Diamond.INTERVAL),
    DIAMOND_3(DIAMOND_4.getMax() + Diamond.INTERVAL, Diamond.INTERVAL),
    DIAMOND_2(DIAMOND_3.getMax() + Diamond.INTERVAL, Diamond.INTERVAL),
    DIAMOND_1(DIAMOND_2.getMax() + Diamond.INTERVAL, Diamond.INTERVAL),
    INFINITY_5(DIAMOND_1.getMax() + Infinity.INTERVAL, Infinity.INTERVAL),
    INFINITY_4(INFINITY_5.getMax() + Infinity.INTERVAL, Infinity.INTERVAL),
    INFINITY_3(INFINITY_4.getMax() + Infinity.INTERVAL, Infinity.INTERVAL),
    INFINITY_2(INFINITY_3.getMax() + Infinity.INTERVAL, Infinity.INTERVAL),
    INFINITY_1(Long.MAX_VALUE, Infinity.INTERVAL);

    private final long max;
    private final int interval;

    Rank(long max, int interval) {
        this.max = max;
        this.interval = interval;
    }

    public long getMax() {
        return max;
    }

    public int getInterval() {
        return interval;
    }

    public static class Bronze {
        public static final int INTERVAL = 100;
    }

    public static class Silver {
        public static final int INTERVAL = 200;
    }

    public static class Gold {
        public static final int INTERVAL = 400;
    }

    public static class Platinum {
        public static final int INTERVAL = 800;
    }

    public static class Diamond {
        public static final int INTERVAL = 1600;
    }

    public static class Infinity {
        public static final int INTERVAL = 3200;
    }

    public static Rank getRankByScore(Long score){
        for (Rank rank : Rank.values()) {
            if(rank.getMax()-rank.getInterval() <= score && score < rank.getMax())
                return rank;
        }
        throw new RankNotFoundException("점수 {" + score + "} 에 해당하는 Rank가 존재하지 않습니다.");
    }
}
