package com.sky.utils;

public class DBDataUtils {
    public static Double nullToZero(Double turnover) {
        turnover = turnover == null ? 0 : turnover;
        return turnover;
    }

    public static Integer nullToZero(Integer turnover) {
        turnover = turnover == null ? 0 : turnover;
        return turnover;
    }

    public static Double calculateRate(Integer first, Integer second) {
        return second == 0 ? 0 : 1.0 * first / second;
    }

    public static Double calculateRate(Double first, Integer second) {
        return second == 0 ? 0 : 1.0 * first / second;
    }
}
