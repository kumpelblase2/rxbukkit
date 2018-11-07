package de.eternalwings.bukkit.rxbukkit;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

public class EventObserver<T extends Event> implements ObservableOnSubscribe<T> {

    private interface CustomConsumer<T> extends Consumer<T>, Listener {
    }

    private final Plugin plugin;
    private final Class<T> eventType;
    private final EventPriority priority;
    private final boolean ignoreCancelled;

    public EventObserver(Plugin plugin, Class<T> eventType) {
        this(plugin, eventType, EventPriority.NORMAL, true);
    }

    public EventObserver(Plugin plugin, Class<T> eventType, EventPriority priority) {
        this(plugin, eventType, priority, false);
    }

    public EventObserver(Plugin plugin, Class<T> eventType, EventPriority priority,
                         boolean ignoreCancelled) {
        this.plugin = plugin;
        this.eventType = eventType;
        this.priority = priority;
        this.ignoreCancelled = ignoreCancelled;
    }

    @Override
    public void subscribe(@NonNull ObservableEmitter<T> emitter) throws Exception {
        CustomConsumer<T> consumer = emitter::onNext;
        Bukkit.getPluginManager().registerEvent(eventType, consumer, priority,
                (listener, event) -> ((CustomConsumer) listener).accept(event), plugin, ignoreCancelled);
        final HandlerList handlers =
                (HandlerList) this.eventType.getDeclaredMethod("getHandlerList").invoke(null);
        emitter.setCancellable(() -> handlers.unregister(consumer));
    }
}
