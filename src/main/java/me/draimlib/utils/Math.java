package me.draimlib.utils;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Math{
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }
    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch(NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    private static final NavigableMap<Double, String> suffixes_d = new TreeMap<>();
    static {
        suffixes_d.put(1000.0, "k");
        suffixes_d.put(1000000.0, "M");
        suffixes_d.put(1000000000.0, "G");
        suffixes_d.put(1000000000000.0, "T");
        suffixes_d.put(1000000000000000.0, "P");
        suffixes_d.put(1000000000000000000.0, "E");
    }

    public static String simplify(long value) {
        if (value == Long.MIN_VALUE) return simplify(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + simplify(-value);
        if (value < 1000) return Long.toString(value);

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10);
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        double finalValue = hasDecimal ? (truncated / 10d) : (truncated / 10);
        finalValue = java.lang.Math.round(finalValue * 100.0) / 100.0;
        return finalValue + suffix;
    }
    public static String simplify(double value) {
        if (value == Long.MIN_VALUE) return simplify(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + simplify(-value);
        if (value < 1000) return Double.toString(value);

        Map.Entry<Double, String> e = suffixes_d.floorEntry(value);
        Double divideBy = e.getKey();
        String suffix = e.getValue();

        double truncated = value / (divideBy / 10);
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        double finalValue = hasDecimal ? (truncated / 10d) : (truncated / 10);
        finalValue = java.lang.Math.round(finalValue * 100.0) / 100.0;
        return finalValue + suffix;
    }
}

