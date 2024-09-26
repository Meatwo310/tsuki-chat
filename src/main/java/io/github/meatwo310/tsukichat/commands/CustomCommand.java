package io.github.meatwo310.tsukichat.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.versions.mcp.MCPVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import oshi.SystemInfo;
import oshi.hardware.Baseboard;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.util.Arrays;

public class CustomCommand {
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_ARG = new DynamicCommandExceptionType(arg ->
            Component.literal("不明な引数: " + arg)
    );

    public static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String arg = ctx.getArgument("arg", String.class);
        return switch (arg) {
            case "fetch", "neofetch" -> {
                ctx.getSource().sendSuccess(() -> TsukiChatCommand.getComponent(neofetch()), false);
                yield 1;
            }
            default -> throw ERROR_UNKNOWN_ARG.create(arg);
        };
    }

    public static String neofetch() {
        SystemInfo si = new SystemInfo();
        OperatingSystem os = si.getOperatingSystem();
        HardwareAbstractionLayer hardware = si.getHardware();
        ComputerSystem hardwareSystem = hardware.getComputerSystem();
        Baseboard baseboard = hardwareSystem.getBaseboard();
        CentralProcessor processor = hardware.getProcessor();

        StringBuilder result = new StringBuilder("§8===========§r §cn§6e§eo§af§3e§9t§5c§ch§r §8===========§r");

        // Disk Usage
        addInfo(result, "Disk Usage");
        hardware.getDiskStores().forEach(disk -> {
            addInfo(result, disk.getName(), disk.getModel());
            disk.getPartitions().forEach(partition -> {
                String mountPoint = partition.getMountPoint();
                OSFileStore osFileStore = os.getFileSystem().getFileStores().stream()
                        .filter(fs -> fs.getMount().equals(mountPoint))
                        .findFirst().orElse(null);
                long used = osFileStore == null ? 0 : osFileStore.getTotalSpace() - osFileStore.getUsableSpace();
                addInfo(result, "|-" + partition.getIdentification(),
                        "[" + partition.getType() + "]",
                        osFileStore == null ? "unknown" : humanReadableByteCount(used),
                        "/",
                        osFileStore == null ? humanReadableByteCount(partition.getSize()) :
                                String.format("%s (%.1f%% used) %s",
                                        humanReadableByteCount(osFileStore.getTotalSpace()),
                                        used * 100.0 / osFileStore.getTotalSpace(),
                                        mountPoint.replaceAll("\\\\040", " ")
                                )
                );
            });
        });

        result.append("\n§8= = = = = = = = = = = = = = = = = = = = = = =§r");

        // Various Info
        addInfo(result, "OS", os.getManufacturer(), os.getFamily(), os.getVersionInfo().toString(), System.getProperty("os.arch"));
        addInfo(result, "Host", os.getNetworkParams().getHostName());
        addInfo(result, "Model", hardwareSystem.getManufacturer(), hardwareSystem.getModel());
        addInfo(result, "Baseboard", baseboard.getManufacturer(), baseboard.getModel());
        addInfo(result, "Uptime", formatUptime(os.getSystemUptime()));
        addFormattedInfo(result, "CPU", "%s (%d cores, %d threads)",
                processor.getProcessorIdentifier().getName(),
                processor.getPhysicalProcessorCount(),
                processor.getLogicalProcessorCount()
        );

        // Get CPU Usage of:
        // // - this process
        // - system
        // - each core

        long[] oldTicks = processor.getSystemCpuLoadTicks();
        long[][] oldProcTicks = processor.getProcessorCpuLoadTicks();
        Util.sleep(400);
        double systemCpuUsage = processor.getSystemCpuLoadBetweenTicks(oldTicks);
        double[] processorCpuUsages = processor.getProcessorCpuLoadBetweenTicks(oldProcTicks);

        addInfo(result, "CPU Usage",
//                String.format("thr %.1f%%;"),
                String.format("sys %.1f%%;", systemCpuUsage * 100),
                "cores: " + String.join(" ", Arrays.stream(processorCpuUsages)
                        .mapToObj(d -> String.format("%.0f%%", d * 100))
                        .toArray(String[]::new)
                )
        );


        // GPU
        hardware.getGraphicsCards().forEach(graphicsCard -> addInfo(
                result, "GPU", graphicsCard.getName(),
                "(" + humanReadableByteCount(graphicsCard.getVRam()) + " VRAM)"
        ));

        // Minecraft
        if (ModList.get() != null) addInfo(result, "Game",
                "Minecraft %s with".formatted(MCPVersion.getMCVersion()),
                "Forge %s [%s]".formatted(
                        ForgeVersion.getVersion(),
                        ModList.get().getModContainerById(ForgeVersion.MOD_ID)
                            .map(ModContainer::getModInfo)
                            .map(IModInfo::getDisplayName)
                            .orElse("Forge")
                )
        );

        addFormattedInfo(result, "Java", "%s; %s %s (%s) [%s]",
                System.getProperty("java.version"),
                System.getProperty("java.vm.name"),
                System.getProperty("java.vm.version"),
                System.getProperty("java.vm.info"),
                System.getProperty("java.vendor")
        );

        // JVM Memory
        long jvmAllocated = Runtime.getRuntime().totalMemory();
        long jvmFree = Runtime.getRuntime().freeMemory();
        long jvmUsed = jvmAllocated - jvmFree;
        long jvmAllocatedMax = Runtime.getRuntime().maxMemory();
        addInfo(result, "JVM Memory", String.format("%s / %s (%s used) [allocated: %s]",
                humanReadableByteCount(jvmUsed),
                humanReadableByteCount(jvmAllocatedMax),
                jvmUsed * 100 / jvmAllocatedMax + "%",
                humanReadableByteCount(jvmAllocated)
        ));

        // System Memory
        long systotalmem = hardware.getMemory().getTotal();
        long sysfreemem = hardware.getMemory().getAvailable();
        long sysusedmem = systotalmem - sysfreemem;
        addInfo(result, "System Memory",
                humanReadableByteCount(sysusedmem), "/", humanReadableByteCount(systotalmem),
                "(" + sysusedmem * 100 / systotalmem + "% used)"
        );

        // Get System Swap Usage
        long swapTotal = hardware.getMemory().getVirtualMemory().getSwapTotal();
        long swapUsed = hardware.getMemory().getVirtualMemory().getSwapUsed();
        if (swapTotal <= 0) {
            addInfo(result, "System Swap", "not available");
        } else {
            addInfo(result, "System Swap",
                    humanReadableByteCount(swapUsed), "/", humanReadableByteCount(swapTotal),
                    "(" + swapUsed * 100 / swapTotal + "% used)"
            );
        }

        // Get Virtual Memory Usage
//        long virtualMemTotal = hardware.getMemory().getVirtualMemory().getVirtualMax();
//        long virtualMemUsed = hardware.getMemory().getVirtualMemory().getVirtualInUse();
//        addInfo(result, "Virtual Memory",
//                humanReadableByteCount(virtualMemUsed), "/", humanReadableByteCount(virtualMemTotal),
//                "(" + virtualMemUsed * 100 / virtualMemTotal + "% used)"
//        );

        result.append("\n§8========================================§r");

        return result.toString();
    }

    private static void addInfo(StringBuilder result, String title, String... info) {
        if (!result.isEmpty()) result.append("\n");
        result.append("§7").append(title).append(":§r ");
        for (String s : info) {
            result.append(s).append(" ");
        }
        result.deleteCharAt(result.length() - 1);
    }

    private static void addFormattedInfo(@NotNull StringBuilder result, @NotNull String title, @NotNull String format, @Nullable Object... args) {
        if (!result.isEmpty()) result.append("\n");
        result.append("§7").append(title).append(":§r ");
        result.append(String.format(format, args));
    }

    private static String formatUptime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        return String.format("%dh %dm", hours, minutes);
    }

    private static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp - 1) + "i";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static void main(String[] args) {
        System.out.println(CustomCommand.neofetch().replaceAll("§.", ""));
    }
}
