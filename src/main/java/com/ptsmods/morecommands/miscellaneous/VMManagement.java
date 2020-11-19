package com.ptsmods.morecommands.miscellaneous;

import com.ptsmods.morecommands.MoreCommands;
import sun.management.counter.Counter;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * This class wraps an instance of {@link sun.management.VMManagement
 * VMManagement} to be used by anyone without the use of Reflection.<br>
 * The {@link sun.management.VMManagementImpl VMManagementImpl} class isn't
 * public so that cannot be used.<br>
 * <br>
 * The original {@link sun.management.VMManagement VMMmanagement class} is
 * mainly used by MX beans, so to avoid the use of lots of different MX beans,
 * you can just use this class instead.
 *
 * @author PlanetTeamSpeak
 */
public class VMManagement implements sun.management.VMManagement {

    private static final VMManagement vmm = new VMManagement();
    private static final sun.management.VMManagement actualVmm;

    private VMManagement() {}

    static {
        try {
            Constructor<? extends sun.management.VMManagement> con = (Constructor<? extends sun.management.VMManagement>) Class.forName("sun.management.VMManagementImpl").getDeclaredConstructors()[0];
            con.setAccessible(true);
            actualVmm = con.newInstance();
        } catch (Exception e) {
            MoreCommands.throwWithoutDeclaration(e);
            throw new RuntimeException(e); // Won't be reached, but since the previously used method doesn't declare that it can throw an error, this has to be here.
        }
    }

    public static VMManagement getVMM() {
        return vmm;
    }

    @Override
    public int getAvailableProcessors() {
        return actualVmm.getAvailableProcessors();
    }

    @Override
    public String getBootClassPath() {
        return actualVmm.getBootClassPath();
    }

    @Override
    public long getClassInitializationTime() {
        return actualVmm.getClassInitializationTime();
    }

    @Override
    public long getClassLoadingTime() {
        return actualVmm.getClassLoadingTime();
    }

    @Override
    public String getClassPath() {
        return actualVmm.getClassPath();
    }

    @Override
    public long getClassVerificationTime() {
        return actualVmm.getClassVerificationTime();
    }

    @Override
    public String getCompilerName() {
        return actualVmm.getCompilerName();
    }

    @Override
    public int getDaemonThreadCount() {
        return actualVmm.getDaemonThreadCount();
    }

    @Override
    public long getInitializedClassCount() {
        return actualVmm.getInitializedClassCount();
    }

    @Override
    public List<Counter> getInternalCounters(String arg0) {
        return actualVmm.getInternalCounters(arg0);
    }

    @Override
    public String getLibraryPath() {
        return actualVmm.getLibraryPath();
    }

    @Override
    public int getLiveThreadCount() {
        return actualVmm.getLiveThreadCount();
    }

    @Override
    public int getLoadedClassCount() {
        return actualVmm.getLoadedClassCount();
    }

    @Override
    public long getLoadedClassSize() {
        return actualVmm.getLoadedClassSize();
    }

    @Override
    public String getManagementVersion() {
        return actualVmm.getManagementVersion();
    }

    @Override
    public long getMethodDataSize() {
        return actualVmm.getMethodDataSize();
    }

    @Override
    public String getOsArch() {
        return actualVmm.getOsArch();
    }

    @Override
    public String getOsName() {
        return actualVmm.getOsName();
    }

    @Override
    public String getOsVersion() {
        return actualVmm.getOsVersion();
    }

    @Override
    public int getPeakThreadCount() {
        return actualVmm.getPeakThreadCount();
    }

    @Override
    public long getSafepointCount() {
        return actualVmm.getSafepointCount();
    }

    @Override
    public long getSafepointSyncTime() {
        return actualVmm.getSafepointSyncTime();
    }

    @Override
    public long getStartupTime() {
        return actualVmm.getStartupTime();
    }

    @Override
    public long getTotalApplicationNonStoppedTime() {
        return actualVmm.getTotalApplicationNonStoppedTime();
    }

    @Override
    public long getTotalClassCount() {
        return actualVmm.getTotalClassCount();
    }

    @Override
    public long getTotalCompileTime() {
        return actualVmm.getTotalCompileTime();
    }

    @Override
    public long getTotalSafepointTime() {
        return actualVmm.getTotalSafepointTime();
    }

    @Override
    public long getTotalThreadCount() {
        return actualVmm.getTotalThreadCount();
    }

    @Override
    public long getUnloadedClassCount() {
        return actualVmm.getUnloadedClassCount();
    }

    @Override
    public long getUnloadedClassSize() {
        return actualVmm.getUnloadedClassSize();
    }

    @Override
    public long getUptime() {
        return actualVmm.getUptime();
    }

    @Override
    public boolean getVerboseClass() {
        return actualVmm.getVerboseClass();
    }

    @Override
    public boolean getVerboseGC() {
        return actualVmm.getVerboseGC();
    }

    @Override
    public List<String> getVmArguments() {
        return actualVmm.getVmArguments();
    }

    @Override
    public String getVmId() {
        return actualVmm.getVmId();
    }

    @Override
    public String getVmName() {
        return actualVmm.getVmName();
    }

    @Override
    public String getVmSpecName() {
        return actualVmm.getVmSpecName();
    }

    @Override
    public String getVmSpecVendor() {
        return actualVmm.getVmSpecVendor();
    }

    @Override
    public String getVmSpecVersion() {
        return actualVmm.getVmSpecVersion();
    }

    @Override
    public String getVmVendor() {
        return actualVmm.getVmVendor();
    }

    @Override
    public String getVmVersion() {
        return actualVmm.getVmVersion();
    }

    @Override
    public boolean isBootClassPathSupported() {
        return actualVmm.isBootClassPathSupported();
    }

    @Override
    public boolean isCompilationTimeMonitoringSupported() {
        return actualVmm.isCompilationTimeMonitoringSupported();
    }

    @Override
    public boolean isCurrentThreadCpuTimeSupported() {
        return actualVmm.isCurrentThreadCpuTimeSupported();
    }

    @Override
    public boolean isGcNotificationSupported() {
        return actualVmm.isGcNotificationSupported();
    }

    @Override
    public boolean isObjectMonitorUsageSupported() {
        return actualVmm.isObjectMonitorUsageSupported();
    }

    @Override
    public boolean isOtherThreadCpuTimeSupported() {
        return actualVmm.isOtherThreadCpuTimeSupported();
    }

    @Override
    public boolean isRemoteDiagnosticCommandsSupported() {
        return actualVmm.isRemoteDiagnosticCommandsSupported();
    }

    @Override
    public boolean isSynchronizerUsageSupported() {
        return actualVmm.isSynchronizerUsageSupported();
    }

    @Override
    public boolean isThreadAllocatedMemoryEnabled() {
        return actualVmm.isThreadAllocatedMemoryEnabled();
    }

    @Override
    public boolean isThreadAllocatedMemorySupported() {
        return actualVmm.isThreadAllocatedMemorySupported();
    }

    @Override
    public boolean isThreadContentionMonitoringEnabled() {
        return actualVmm.isThreadContentionMonitoringEnabled();
    }

    @Override
    public boolean isThreadContentionMonitoringSupported() {
        return actualVmm.isThreadContentionMonitoringSupported();
    }

    @Override
    public boolean isThreadCpuTimeEnabled() {
        return actualVmm.isThreadCpuTimeEnabled();
    }

}
