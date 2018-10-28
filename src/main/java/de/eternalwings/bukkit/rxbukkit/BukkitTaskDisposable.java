package de.eternalwings.bukkit.rxbukkit;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.ref.WeakReference;
import java.util.Optional;

class BukkitTaskDisposable implements Disposable {

    private final WeakReference<BukkitTask> task;

    BukkitTaskDisposable(@NonNull BukkitTask task) {
        this.task = new WeakReference<>(task);
    }

    @Override
    public void dispose() {
        Optional.ofNullable(this.task.get()).ifPresent(BukkitTask::cancel);
    }

    @Override
    public boolean isDisposed() {
        return Optional.ofNullable(this.task.get()).map(BukkitTask::isCancelled).orElse(true);
    }
}
