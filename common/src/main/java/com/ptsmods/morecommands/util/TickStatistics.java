package com.ptsmods.morecommands.util;

import com.ptsmods.morecommands.miscellaneous.RollingAverage;
import dev.architectury.event.events.common.TickEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

// From Lucko's Spark
// https://github.com/lucko/spark/blob/master/spark-common/src/main/java/me/lucko/spark/common/monitor/tick/TickStatistics.java
/**
 * Calculates the servers TPS (ticks per second) rate.
 *
 * <p>The code use to calculate the TPS is the same as the code used by the Minecraft server itself.
 * This means that this class will output values the same as the /tps command.</p>
 *
 * <p>We calculate our own values instead of pulling them from the server for two reasons. Firstly,
 * it's easier - pulling from the server requires reflection code on each of the platforms, we'd
 * rather avoid that. Secondly, it allows us to generate rolling averages over a shorter period of
 * time.</p>
 */
public class TickStatistics {
    private static final long SEC_IN_NANO = TimeUnit.SECONDS.toNanos(1);
    private static final int TPS = 20;
    private static final int TPS_SAMPLE_INTERVAL = 20;
    private static final BigDecimal TPS_BASE = new BigDecimal(SEC_IN_NANO).multiply(new BigDecimal(TPS_SAMPLE_INTERVAL));

    private final TpsRollingAverage tps5Sec = new TpsRollingAverage(5);
    private final TpsRollingAverage tps10Sec = new TpsRollingAverage(10);
    private final TpsRollingAverage tps1Min = new TpsRollingAverage(60);
    private final TpsRollingAverage tps5Min = new TpsRollingAverage(60 * 5);
    private final TpsRollingAverage tps15Min = new TpsRollingAverage(60 * 15);
    private final TpsRollingAverage[] tpsAverages = {this.tps5Sec, this.tps10Sec, this.tps1Min, this.tps5Min, this.tps15Min};

    private final RollingAverage tickDuration10Sec = new RollingAverage(TPS * 10);
    private final RollingAverage tickDuration1Min = new RollingAverage(TPS * 60);
    private final RollingAverage[] tickDurationAverages = {this.tickDuration10Sec, this.tickDuration1Min};

    private long last = 0;

    public TickStatistics() {
        AtomicInteger tick = new AtomicInteger();
        AtomicLong start = new AtomicLong();
        TickEvent.SERVER_PRE.register(server -> {
            onTick(tick.getAndIncrement());
            start.set(System.nanoTime());
        });
        TickEvent.SERVER_POST.register(server -> onTick((System.nanoTime() - start.get()) / 1000000d));
    }

    public void onTick(int currentTick) {
        if (currentTick % TPS_SAMPLE_INTERVAL != 0) {
            return;
        }

        long now = System.nanoTime();

        if (this.last == 0) {
            this.last = now;
            return;
        }

        long diff = now - this.last;
        BigDecimal currentTps = TPS_BASE.divide(new BigDecimal(diff), 30, RoundingMode.HALF_UP);
        BigDecimal total = currentTps.multiply(new BigDecimal(diff));

        for (TpsRollingAverage rollingAverage : this.tpsAverages) {
            rollingAverage.add(currentTps, diff, total);
        }

        this.last = now;
    }

    public void onTick(double duration) {
        BigDecimal decimal = new BigDecimal(duration);
        for (RollingAverage rollingAverage : this.tickDurationAverages) {
            rollingAverage.add(decimal);
        }
    }

    public double tps5Sec() {
        return this.tps5Sec.getAverage();
    }

    public double tps10Sec() {
        return this.tps10Sec.getAverage();
    }

    public double tps1Min() {
        return this.tps1Min.getAverage();
    }

    public double tps5Min() {
        return this.tps5Min.getAverage();
    }

    public double tps15Min() {
        return this.tps15Min.getAverage();
    }

    public RollingAverage duration10Sec() {
        return this.tickDuration10Sec;
    }

    public RollingAverage duration1Min() {
        return this.tickDuration1Min;
    }


    /**
     * Rolling average calculator.
     *
     * <p>This code is taken from PaperMC/Paper, licensed under MIT.</p>
     *
     * @author aikar (PaperMC) <a href="https://github.com/PaperMC/Paper/blob/master/Spigot-Server-Patches/0021-Further-improve-server-tick-loop.patch">source</a>
     */
    public static final class TpsRollingAverage {
        private final int size;
        private long time;
        private BigDecimal total;
        private int index = 0;
        private final BigDecimal[] samples;
        private final long[] times;

        TpsRollingAverage(int size) {
            this.size = size;
            this.time = size * SEC_IN_NANO;
            this.total = new BigDecimal(TPS).multiply(new BigDecimal(SEC_IN_NANO)).multiply(new BigDecimal(size));
            this.samples = new BigDecimal[size];
            this.times = new long[size];
            for (int i = 0; i < size; i++) {
                this.samples[i] = new BigDecimal(TPS);
                this.times[i] = SEC_IN_NANO;
            }
        }

        public void add(BigDecimal x, long t, BigDecimal total) {
            this.time -= this.times[this.index];
            this.total = this.total.subtract(this.samples[this.index].multiply(new BigDecimal(this.times[this.index])));
            this.samples[this.index] = x;
            this.times[this.index] = t;
            this.time += t;
            this.total = this.total.add(total);
            if (++this.index == this.size) {
                this.index = 0;
            }
        }

        public double getAverage() {
            return this.total.divide(new BigDecimal(this.time), 30, RoundingMode.HALF_UP).doubleValue();
        }
    }
}
