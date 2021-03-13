package com.ptsmods.morecommands.miscellaneous;

import com.sun.management.OperatingSystemMXBean;

import java.io.File;
import java.lang.management.ManagementFactory;

public class UsageMonitorer {
    private UsageMonitorer() {}

    public static String getOSName() {
        return getOSMXB().getName();
    }

    public static String getOSVersion() {
        return getOSMXB().getVersion();
    }

    public static String getOSArch() {
        return getOSMXB().getArch();
    }

    public static int getProcessorCount() {
        return getOSMXB().getAvailableProcessors();
    }

    public static float getSystemCpuLoad() {
        try {
            return (float) getOSMXB().getSystemCpuLoad();
        } catch (Exception e) {
            return -1;
        }
    }

    public static float getAverageSystemCpuLoad() {
        try {
            return (float) getOSMXB().getSystemLoadAverage();
        } catch (Exception e) {
            return -1;
        }
    }

    public static float getProcessCpuLoad() {
        try {
            return (float) getOSMXB().getProcessCpuLoad();
        } catch (Exception e) {
            return -1;
        }
    }

    public static long getProcessRamUsage() {
        return Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory();
    }

    public static long getProcessRamMax() {
        return Runtime.getRuntime().maxMemory();
    }

    public static long getSystemRamUsage() {
        return getSystemRamMax() - getOSMXB().getFreePhysicalMemorySize();
    }

    public static long getSystemRamMax() {
        return getOSMXB().getTotalPhysicalMemorySize();
    }

    public static long getSystemSwapUsage() {
        return getSystemSwapMax() - getOSMXB().getFreeSwapSpaceSize();
    }

    public static long getSystemSwapMax() {
        return getOSMXB().getTotalSwapSpaceSize();
    }

    public static long getTotalSystemRamUsage() {
        return getSystemRamUsage() + getSystemSwapUsage();
    }

    public static long getTotalSystemRamMax() {
        return getSystemRamMax() + getSystemSwapMax();
    }

    public static long getTotalSpace() {
        long totalSpace = 0;
        for (File root : File.listRoots())
            totalSpace += root.getTotalSpace();
        return totalSpace;
    }

    public static long getFreeSpace() {
        long freeSpace = 0;
        for (File root : File.listRoots())
            freeSpace += root.getFreeSpace();
        return freeSpace;
    }

    public static long getUsedSpace() {
        return getTotalSpace() - getFreeSpace();
    }

    public static long getUsableSpace() {
        long usableSpace = 0;
        for (File root : File.listRoots())
            usableSpace += root.getUsableSpace();
        return usableSpace;
    }

    public static int getCurrentLoadedClassCount() {
        return ManagementFactory.getClassLoadingMXBean().getLoadedClassCount();
    }

    public static long getUnloadedClassCount() {
        return ManagementFactory.getClassLoadingMXBean().getUnloadedClassCount();
    }

    public static long getTotalLoadedClassCount() {
        return ManagementFactory.getClassLoadingMXBean().getTotalLoadedClassCount();
    }

    public static int getLiveThreadCount() {
        return ManagementFactory.getThreadMXBean().getThreadCount();
    }

    public static int getDaemonThreadCount() {
        return ManagementFactory.getThreadMXBean().getDaemonThreadCount();
    }

    public static long getTotalThreadCount() {
        return ManagementFactory.getThreadMXBean().getTotalStartedThreadCount();
    }

    public static long getPeakLiveThreadCount() {
        return ManagementFactory.getThreadMXBean().getPeakThreadCount();
    }

    private static OperatingSystemMXBean getOSMXB() {
        return (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

}
