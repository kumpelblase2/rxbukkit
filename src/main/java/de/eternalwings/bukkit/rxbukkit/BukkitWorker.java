package de.eternalwings.bukkit.rxbukkit;

import io.reactivex.Scheduler.Worker;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

class BukkitWorker extends Worker {

    private final Object lock = new Object();

    private final BukkitSchedulingType schedulingType;
    private final Plugin pluginReference;
    private final Set<Integer> scheduled = new HashSet<>();
    private volatile boolean disposed = false;

    BukkitWorker(@NonNull Plugin pluginReference, @NonNull BukkitSchedulingType schedulingType) {
        this.schedulingType = schedulingType;
        this.pluginReference = pluginReference;
    }

    @NonNull
    @Override
    public Disposable schedule(@NonNull Runnable run, long delay, @NonNull TimeUnit unit) {
        if(this.isDisposed()) {
            return EmptyDisposable.INSTANCE;
        }

        final CompletableFuture<Void> future = new CompletableFuture<>();
        final BukkitTask task = this.create(() -> {
            run.run();
            future.complete(null);
        }, unit.toMillis(delay));
        final BukkitTaskDisposable wrapper = new BukkitTaskDisposable(task);
        future.thenRun(() -> this.removeFinished(task.getTaskId()));
        synchronized(this.lock) {
            this.scheduled.add(task.getTaskId());
        }
        return wrapper;
    }

    @Override
    public void dispose() {
        this.disposed = true;
        synchronized(this.lock) {
            this.scheduled.forEach(taskId -> Bukkit.getScheduler().cancelTask(taskId));
            this.scheduled.clear();
        }
    }

    @Override
    public boolean isDisposed() {
        return this.disposed;
    }

    private void removeFinished(int taskId) {
        synchronized(this.lock) {
            this.scheduled.remove(taskId);
        }
    }

    private BukkitTask create(Runnable run, long delayInMillis) {
        switch(this.schedulingType) {
            case ASYNC:
                return Bukkit.getScheduler().runTaskLaterAsynchronously(this.pluginReference, run, delayInMillis);
            case SYNC:
                return Bukkit.getScheduler().runTaskLater(this.pluginReference, run, delayInMillis);
            default:
                throw new IllegalStateException("Tried to create task with unknown scheduling type: " + this.schedulingType);
        }
    }

}
