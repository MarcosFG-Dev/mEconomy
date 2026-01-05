package com.marcosfg.meconomy.utils;

import com.marcosfg.meconomy.Main;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class FormatUtils {

    private static final NavigableMap<Double, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000d, "k");
        suffixes.put(1_000_000d, "M");
        suffixes.put(1_000_000_000d, "B");
        suffixes.put(1_000_000_000_000d, "T");
        suffixes.put(1_000_000_000_000_000d, "Q");
    }

    public static String format(double value, Main plugin) {
        if (plugin.getConfig().getBoolean("settings.format.use-k-notation")) {
            if (value == Long.MIN_VALUE)
                return format(Long.MIN_VALUE + 1, plugin);
            if (value < 0)
                return "-" + format(-value, plugin);
            if (value < 1000)
                return new DecimalFormat("#,##0.00").format(value);

            Map.Entry<Double, String> e = suffixes.floorEntry(value);
            Double divideBy = e.getKey();
            String suffix = e.getValue();

            double formattedNumber = value / divideBy;
            return new DecimalFormat("#.##").format(formattedNumber) + suffix;
        } else {
            return new DecimalFormat("#,##0.00").format(value);
        }
    }
}
