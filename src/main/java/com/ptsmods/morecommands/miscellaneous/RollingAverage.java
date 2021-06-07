package com.ptsmods.morecommands.miscellaneous;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

// From Lucko's Spark
// https://github.com/lucko/spark/blob/master/spark-common/src/main/java/me/lucko/spark/common/util/RollingAverage.java
public class RollingAverage {
    private final Queue<BigDecimal> samples;
    private final int windowSize;
    private BigDecimal total = BigDecimal.ZERO;

    public RollingAverage(int windowSize) {
        this.windowSize = windowSize;
        this.samples = new ArrayDeque<>(this.windowSize + 1);
    }

    public void add(BigDecimal num) {
        synchronized (this) {
            this.total = this.total.add(num);
            this.samples.add(num);
            if (this.samples.size() > this.windowSize) {
                this.total = this.total.subtract(this.samples.remove());
            }
        }
    }

    public double mean() {
        synchronized (this) {
            if (this.samples.isEmpty()) {
                return 0;
            }
            BigDecimal divisor = BigDecimal.valueOf(this.samples.size());
            return this.total.divide(divisor, 30, RoundingMode.HALF_UP).doubleValue();
        }
    }

    public double median() {
        return percentile(0.50d);
    }

    public double max() {
        synchronized (this) {
            BigDecimal max = null;
            for (BigDecimal sample : this.samples) {
                if (max == null || sample.compareTo(max) > 0) {
                    max = sample;
                }
            }
            return max == null ? 0 : max.doubleValue();
        }
    }

    public double min() {
        synchronized (this) {
            BigDecimal min = null;
            for (BigDecimal sample : this.samples) {
                if (min == null || sample.compareTo(min) < 0) {
                    min = sample;
                }
            }
            return min == null ? 0 : min.doubleValue();
        }
    }

    public double percentile(double percentile) {
        if (percentile < 0 || percentile > 1) {
            throw new IllegalArgumentException("Invalid percentile " + percentile);
        }

        BigDecimal[] sortedSamples;
        synchronized (this) {
            if (this.samples.isEmpty()) {
                return 0;
            }
            sortedSamples = this.samples.toArray(new BigDecimal[0]);
        }
        Arrays.sort(sortedSamples);

        int rank = (int) Math.ceil(percentile * (sortedSamples.length - 1));
        return sortedSamples[rank].doubleValue();
    }

}
