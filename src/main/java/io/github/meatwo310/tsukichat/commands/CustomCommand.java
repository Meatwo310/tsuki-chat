package io.github.meatwo310.tsukichat.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

public class CustomCommand {
    public static int execute(CommandContext<CommandSourceStack> ctx) {
        String arg = ctx.getArgument("arg", String.class);
        return switch (arg) {
            case "neofetch" -> neofetch(ctx);
            default -> {
                ctx.getSource().sendFailure(Component.literal("不明な引数: " + arg));
                yield 0;
            }
        };
    }

    private static int neofetch(CommandContext<CommandSourceStack> ctx) {
        SystemInfo si = new SystemInfo();
        OperatingSystem os = si.getOperatingSystem();
        HardwareAbstractionLayer hal = si.getHardware();

        StringBuilder result = new StringBuilder();

        result.append("§7OS§r: ")
                .append(os.getFamily()).append(" ")
                .append(os.getManufacturer()).append(" ")
                .append(os.getBitness()).append("bit\n");
        result.append("§7Ver§r: ")
                .append(os.getVersionInfo()).append("\n");
        result.append("§7Host§r: ")
                .append(hal.getComputerSystem().getManufacturer()).append(" ")
                .append(hal.getComputerSystem().getModel()).append("\n");
        result.append("§7Board§r: ")
                .append(hal.getComputerSystem().getBaseboard().getManufacturer()).append(" ")
                .append(hal.getComputerSystem().getBaseboard().getModel()).append("\n");
        result.append("§7Uptime§r: ")
                .append(formatUptime(os.getSystemUptime())).append("\n");
        result.append("§7CPU§r: ")
                .append(hal.getProcessor().getProcessorIdentifier().getName()).append(" (")
                .append(hal.getProcessor().getPhysicalProcessorCount()).append(" Cores ")
                .append(hal.getProcessor().getLogicalProcessorCount()).append(" Threads)\n");

        hal.getGraphicsCards().forEach(graphicsCard ->
                result.append("§7GPU§r: ")
                        .append(graphicsCard.getName()).append(" (")
                        .append(humanReadableByteCount(graphicsCard.getVRam())).append(" VRAM)\n")
        );

        long totalmem = hal.getMemory().getTotal();
        long freemem = hal.getMemory().getAvailable();
        long usedmem = totalmem - freemem;
        result.append("§7Memory§r: ")
                .append(humanReadableByteCount(usedmem)).append(" / ")
                .append(humanReadableByteCount(totalmem)).append(" (")
                .append(freemem * 100 / totalmem).append("% free)");

        ctx.getSource().sendSuccess(() -> Component.literal(result.toString()), false);

        return 0;
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
}
