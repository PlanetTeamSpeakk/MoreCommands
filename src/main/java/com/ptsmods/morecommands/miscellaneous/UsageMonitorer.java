package com.ptsmods.morecommands.miscellaneous;

import com.ptsmods.morecommands.MoreCommands;
import com.sun.management.OperatingSystemMXBean;
import com.sun.management.ThreadMXBean;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.util.HashMap;
import java.util.Map;

public class UsageMonitorer {

    private static final Map<Long, Long> threadInitialCPU = new HashMap<>();
    private static final Map<Long, Float> threadCPUUsage = new HashMap<>();
    private static final long initialUptime = ManagementFactory.getRuntimeMXBean().getUptime();

    private UsageMonitorer() {}

    static {
        MoreCommands.execute(UsageMonitorer::updateThreadCPUUsages);
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

    public static long getCurrentLoadedClassCount() {
        return VMManagement.getVMM().getLoadedClassCount();
    }

    public static long getTotalLoadedClassCount() {
        return VMManagement.getVMM().getTotalClassCount();
    }

    public static float getThreadCPUUsage(Thread thread) {
        return getThreadCPUUsage(thread.getId());
    }

    public static float getThreadCPUUsage(long threadId) {
        if (!threadCPUUsage.containsKey(threadId)) updateThreadCPUUsages();
        Float usage = threadCPUUsage.get(threadId);
        if (usage == null) return -1;
        else return usage;
    }

    public static void updateThreadCPUUsages() {
        ThreadMXBean threadMxBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        OperatingSystemMXBean osMxBean = getOSMXB();
        ThreadInfo[] threadInfos = threadMxBean.dumpAllThreads(false, false);
        for (ThreadInfo info : threadInfos)
            threadInitialCPU.put(info.getThreadId(), threadMxBean.getThreadCpuTime(info.getThreadId()));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long uptime = runtimeMxBean.getUptime();
        Map<Long, Long> threadCurrentCPU = new HashMap<>();
        threadInfos = threadMxBean.dumpAllThreads(false, false);
        for (ThreadInfo info : threadInfos)
            threadCurrentCPU.put(info.getThreadId(), threadMxBean.getThreadCpuTime(info.getThreadId()));
        int nrCPUs = osMxBean.getAvailableProcessors();
        long elapsedTime = uptime - initialUptime;
        for (ThreadInfo info : threadInfos) {
            Long initialCPU = threadInitialCPU.get(info.getThreadId());
            if (initialCPU != null) {
                long elapsedCpu = threadCurrentCPU.get(info.getThreadId()) - initialCPU;
                float cpuUsage = elapsedCpu * 100 / (elapsedTime * 1000000F * nrCPUs);
                threadCPUUsage.put(info.getThreadId(), cpuUsage);
            }
        }
    }

    private static OperatingSystemMXBean getOSMXB() {
        return (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

}
