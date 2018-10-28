package de.eternalwings.bukkit.rxbukkit;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import org.bukkit.plugin.Plugin;

public class BukkitScheduler extends Scheduler {

    private final Plugin plugin;
    private final BukkitSchedulingType schedulingType;

    private BukkitScheduler(Plugin plugin, BukkitSchedulingType schedulingType) {
        this.plugin = plugin;
        this.schedulingType = schedulingType;
    }

    @NonNull
    @Override
    public Worker createWorker() {
        return new BukkitWorker(plugin, this.schedulingType);
    }

    public static Scheduler createSync(Plugin plugin) {
        return new BukkitScheduler(plugin, BukkitSchedulingType.SYNC);
    }

    public static Scheduler createAsync(Plugin plugin) {
        return new BukkitScheduler(plugin, BukkitSchedulingType.ASYNC);
    }
}
