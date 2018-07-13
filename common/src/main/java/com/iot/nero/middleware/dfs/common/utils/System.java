package com.iot.nero.middleware.dfs.common.utils;

import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.*;
import oshi.util.FormatUtil;
import oshi.util.Util;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.iot.nero.middleware.dfs.common.constant.CONSTANT.pInfo;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/14
 * Time   8:25 AM
 */
public class System {
    private static void printComputerSystem(final ComputerSystem computerSystem) {
        pInfo("(System)" + "manufacturer: " + computerSystem.getManufacturer());
        pInfo("(System)" + "model: " + computerSystem.getModel());
        pInfo("(System)" + "serialnumber: " + computerSystem.getSerialNumber());
        final Firmware firmware = computerSystem.getFirmware();
        pInfo("(System)" + "firmware:");
        pInfo("(System)" + "  manufacturer: " + firmware.getManufacturer());
        pInfo("(System)" + "  name: " + firmware.getName());
        pInfo("(System)" + "  description: " + firmware.getDescription());
        pInfo("(System)" + "  version: " + firmware.getVersion());
        pInfo("(System)" + "  release date: " + (firmware.getReleaseDate() == null ? "unknown"
                : firmware.getReleaseDate() == null ? "unknown" : FormatUtil.formatDate(firmware.getReleaseDate())));
        final Baseboard baseboard = computerSystem.getBaseboard();
        pInfo("(System)" + "baseboard:");
        pInfo("(System)" + "  manufacturer: " + baseboard.getManufacturer());
        pInfo("(System)" + "  model: " + baseboard.getModel());
        pInfo("(System)" + "  version: " + baseboard.getVersion());
        pInfo("(System)" + "  serialnumber: " + baseboard.getSerialNumber());
    }

    private static void printProcessor(CentralProcessor processor) {
        pInfo("(System)" + processor);
        pInfo("(System)" + " " + processor.getPhysicalProcessorCount() + " physical CPU(s)");
        pInfo("(System)" + " " + processor.getLogicalProcessorCount() + " logical CPU(s)");
        pInfo("(System)" + "Identifier: " + processor.getIdentifier());
        pInfo("(System)" + "ProcessorID: " + processor.getProcessorID());
    }

    private static void printMemory(GlobalMemory memory) {
        pInfo("(System)" + "UsingMemory: " + FormatUtil.formatBytes(memory.getAvailable()) + "TotalMemory"
                + FormatUtil.formatBytes(memory.getTotal()));
        pInfo("(System)" + "Swap used: " + FormatUtil.formatBytes(memory.getSwapUsed()) + "/"
                + FormatUtil.formatBytes(memory.getSwapTotal()));
    }

    private static void printCpu(CentralProcessor processor) {
        pInfo("(System)" + "Uptime: " + FormatUtil.formatElapsedSecs(processor.getSystemUptime()));
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        pInfo("(System)" + "CPU, IOWait, and IRQ ticks @ 0 sec:" + Arrays.toString(prevTicks));
        // Wait a second...
        Util.sleep(1000);
        long[] ticks = processor.getSystemCpuLoadTicks();
        pInfo("(System)" + "CPU, IOWait, and IRQ ticks @ 1 sec:" + Arrays.toString(ticks));
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long sys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long totalCpu = user + nice + sys + idle + iowait + irq + softirq + steal;
        java.lang.System.out.format("[" + new Date().toString() + "] " +
                        "User: %.1f%% Nice: %.1f%% System: %.1f%% Idle: %.1f%% IOwait: %.1f%% IRQ: %.1f%% SoftIRQ: %.1f%% Steal: %.1f%%%n",
                100d * user / totalCpu, 100d * nice / totalCpu, 100d * sys / totalCpu, 100d * idle / totalCpu,
                100d * iowait / totalCpu, 100d * irq / totalCpu, 100d * softirq / totalCpu, 100d * steal / totalCpu);
        java.lang.System.out.format("[" + new Date().toString() + "] " + "CPU load: %.1f%% (counting ticks)%n", processor.getSystemCpuLoadBetweenTicks() * 100);
        java.lang.System.out.format("[" + new Date().toString() + "] " + "CPU load: %.1f%% (OS MXBean)%n", processor.getSystemCpuLoad() * 100);
        double[] loadAverage = processor.getSystemLoadAverage(3);
        pInfo("(System)" + "CPU load averages:" + (loadAverage[0] < 0 ? " N/A" : String.format(" %.2f", loadAverage[0]))
                + (loadAverage[1] < 0 ? " N/A" : String.format(" %.2f", loadAverage[1]))
                + (loadAverage[2] < 0 ? " N/A" : String.format(" %.2f", loadAverage[2])));
        // per core CPU
        StringBuilder procCpu = new StringBuilder("CPU load per processor:");
        double[] load = processor.getProcessorCpuLoadBetweenTicks();
        for (double avg : load) {
            procCpu.append(String.format(" %.1f%%", avg * 100));
        }
        pInfo("(System)" + procCpu.toString());
    }

    private static void printProcesses(OperatingSystem os, GlobalMemory memory) {
        pInfo("(System)" + "Processes: " + os.getProcessCount() + ", Threads: " + os.getThreadCount());
        // Sort by highest CPU
        List<OSProcess> procs = Arrays.asList(os.getProcesses(5, OperatingSystem.ProcessSort.CPU));
        pInfo("(System)" + "   PID  %CPU %MEM       VSZ       RSS Name");
        for (int i = 0; i < procs.size() && i < 5; i++) {
            OSProcess p = procs.get(i);
            java.lang.System.out.format("[" + new Date().toString() + "] " + " %5d %5.1f %4.1f %9s %9s %s%n", p.getProcessID(),
                    100d * (p.getKernelTime() + p.getUserTime()) / p.getUpTime(),
                    100d * p.getResidentSetSize() / memory.getTotal(), FormatUtil.formatBytes(p.getVirtualSize()),
                    FormatUtil.formatBytes(p.getResidentSetSize()), p.getName());
        }
    }

    private static void printSensors(Sensors sensors) {
        pInfo("(System)" + "Sensors:");
        java.lang.System.out.format("[" + new Date().toString() + "] " + " CPU Temperature: %.1f°C%n", sensors.getCpuTemperature());
        pInfo("(System)" + " Fan Speeds: " + Arrays.toString(sensors.getFanSpeeds()));
        java.lang.System.out.format("[" + new Date().toString() + "] " + " CPU Voltage: %.1fV%n", sensors.getCpuVoltage());
    }

    private static void printPowerSources(PowerSource[] powerSources) {
        StringBuilder sb = new StringBuilder("Power: ");
        if (powerSources.length == 0) {
            sb.append("Unknown");
        } else {
            double timeRemaining = powerSources[0].getTimeRemaining();
            if (timeRemaining < -1d) {
                sb.append("Charging");
            } else if (timeRemaining < 0d) {
                sb.append("Calculating time remaining");
            } else {
                sb.append(String.format("%d:%02d remaining", (int) (timeRemaining / 3600),
                        (int) (timeRemaining / 60) % 60));
            }
        }
        for (PowerSource pSource : powerSources) {
            sb.append(String.format("[" + new Date().toString() + "] " + "%n %s @ %.1f%%", pSource.getName(), pSource.getRemainingCapacity() * 100d));
        }
        pInfo("(System)" + sb.toString());
    }

    private static void printDisks(HWDiskStore[] diskStores) {
        pInfo("(System)" + "Disks:");
        for (HWDiskStore disk : diskStores) {
            boolean readwrite = disk.getReads() > 0 || disk.getWrites() > 0;
            java.lang.System.out.format("[" + new Date().toString() + "] " + " %s: (model: %s - S/N: %s) size: %s, reads: %s (%s), writes: %s (%s), xfer: %s ms%n",
                    disk.getName(), disk.getModel(), disk.getSerial(),
                    disk.getSize() > 0 ? FormatUtil.formatBytesDecimal(disk.getSize()) : "?",
                    readwrite ? disk.getReads() : "?", readwrite ? FormatUtil.formatBytes(disk.getReadBytes()) : "?",
                    readwrite ? disk.getWrites() : "?", readwrite ? FormatUtil.formatBytes(disk.getWriteBytes()) : "?",
                    readwrite ? disk.getTransferTime() : "?");
            HWPartition[] partitions = disk.getPartitions();
            if (partitions == null) {
                // TODO Remove when all OS's implemented
                continue;
            }
            for (HWPartition part : partitions) {
                java.lang.System.out.format("[" + new Date().toString() + "] " + " |-- %s: %s (%s) Maj:Min=%d:%d, size: %s%s%n", part.getIdentification(),
                        part.getName(), part.getType(), part.getMajor(), part.getMinor(),
                        FormatUtil.formatBytesDecimal(part.getSize()),
                        part.getMountPoint().isEmpty() ? "" : " @ " + part.getMountPoint());
            }
        }
    }

    private static void printFileSystem(FileSystem fileSystem) {
        pInfo("(System)" + "File System:");
        java.lang.System.out.format("[" + new Date().toString() + "] " + " File Descriptors: %d/%d%n", fileSystem.getOpenFileDescriptors(),
                fileSystem.getMaxFileDescriptors());
        OSFileStore[] fsArray = fileSystem.getFileStores();
        for (OSFileStore fs : fsArray) {
            long usable = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            java.lang.System.out.format("[" + new Date().toString() + "] " +
                            " %s (%s) [%s] %s of %s free (%.1f%%) is %s "
                            + (fs.getLogicalVolume() != null && fs.getLogicalVolume().length() > 0 ? "[%s]" : "%s")
                            + " and is mounted at %s%n",
                    fs.getName(), fs.getDescription().isEmpty() ? "file system" : fs.getDescription(), fs.getType(),
                    FormatUtil.formatBytes(usable), FormatUtil.formatBytes(fs.getTotalSpace()), 100d * usable / total,
                    fs.getVolume(), fs.getLogicalVolume(), fs.getMount());
        }
    }

    private static void printNetworkInterfaces(NetworkIF[] networkIFs) {
        pInfo("(System)" + "Network interfaces:");
        for (NetworkIF net : networkIFs) {
            java.lang.System.out.format("[" + new Date().toString() + "] " + " Name: %s (%s)%n", net.getName(), net.getDisplayName());
            java.lang.System.out.format("[" + new Date().toString() + "] " + "   MAC Address: %s %n", net.getMacaddr());
            java.lang.System.out.format("[" + new Date().toString() + "] " + "   MTU: %s, Speed: %s %n", net.getMTU(), FormatUtil.formatValue(net.getSpeed(), "bps"));
            java.lang.System.out.format("[" + new Date().toString() + "] " + "   IPv4: %s %n", Arrays.toString(net.getIPv4addr()));
            java.lang.System.out.format("[" + new Date().toString() + "] " + "   IPv6: %s %n", Arrays.toString(net.getIPv6addr()));
            boolean hasData = net.getBytesRecv() > 0 || net.getBytesSent() > 0 || net.getPacketsRecv() > 0
                    || net.getPacketsSent() > 0;
            java.lang.System.out.format("[" + new Date().toString() + "] " + "   Traffic: received %s/%s%s; transmitted %s/%s%s %n",
                    hasData ? net.getPacketsRecv() + " packets" : "?",
                    hasData ? FormatUtil.formatBytes(net.getBytesRecv()) : "?",
                    hasData ? " (" + net.getInErrors() + " err)" : "",
                    hasData ? net.getPacketsSent() + " packets" : "?",
                    hasData ? FormatUtil.formatBytes(net.getBytesSent()) : "?",
                    hasData ? " (" + net.getOutErrors() + " err)" : "");
        }
    }

    private static void printNetworkParameters(NetworkParams networkParams) {
        pInfo("(System)" + "Network parameters:");
        java.lang.System.out.format("[" + new Date().toString() + "] " + " Host name: %s%n", networkParams.getHostName());
        java.lang.System.out.format("[" + new Date().toString() + "] " + " Domain name: %s%n", networkParams.getDomainName());
        java.lang.System.out.format("[" + new Date().toString() + "] " + " DNS servers: %s%n", Arrays.toString(networkParams.getDnsServers()));
        java.lang.System.out.format("[" + new Date().toString() + "] " + " IPv4 Gateway: %s%n", networkParams.getIpv4DefaultGateway());
        java.lang.System.out.format("[" + new Date().toString() + "] " + " IPv6 Gateway: %s%n", networkParams.getIpv6DefaultGateway());
    }

    private static void printDisplays(Display[] displays) {
        pInfo("(System)" + "Displays:");
        int i = 0;
        for (Display display : displays) {
            pInfo("(System)" + " Display " + i + ":");
            pInfo("(System)" + display.toString());
            i++;
        }
    }

    private static void printUsbDevices(UsbDevice[] usbDevices) {
        pInfo("(System)" + "USB Devices:");
        for (UsbDevice usbDevice : usbDevices) {
            pInfo("(System)" + usbDevice.toString());
        }
    }

    public static void checkSystem() {
        pInfo("(System)" + "Initializing System...");
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();

        pInfo("(System)" + "Checking computer system...");
        printComputerSystem(hal.getComputerSystem());
        pInfo("(System)" + "Checking Processor...");
        printProcessor(hal.getProcessor());
        pInfo("(System)" + "Checking Memory...");
        printMemory(hal.getMemory());
        pInfo("(System)" + "Checking CPU...");
        printCpu(hal.getProcessor());
        pInfo("(System)" + "Checking Processes...");
        printProcesses(os, hal.getMemory());
        pInfo("(System)" + "Checking Sensors...");
        printSensors(hal.getSensors());
        pInfo("(System)" + "Checking Power sources...");
        printPowerSources(hal.getPowerSources());
        pInfo("(System)" + "Checking Disks...");
        printDisks(hal.getDiskStores());
        pInfo("(System)" + "Checking File System...");
        printFileSystem(os.getFileSystem());
        pInfo("(System)" + "Checking Network interfaces...");
        printNetworkInterfaces(hal.getNetworkIFs());
        pInfo("(System)" + "Checking Network parameterss...");
        printNetworkParameters(os.getNetworkParams());
        // hardware: displays
        pInfo("(System)" + "Checking Displays...");
        printDisplays(hal.getDisplays());
        // hardware: USB devices
        pInfo("(System)" + "Checking USB Devices...");
        printUsbDevices(hal.getUsbDevices(true));
    }
}
