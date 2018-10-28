# rxbukkit
Integrates the bukkit scheduler into RxJava

This is a small library that provides a `Scheduler` for RxJava that can be used to schedule stuff using the Bukkit Scheduler 
(both sync and async).

Just like with any other Scheduler, you just call `observeOn` or similar using this Scheduler and it will be run in that context. 
For Bukkit we can do it like this:

```Java
// with 'this' being the java plugin
final Scheduler bukkitScheduler = BukkitScheduler.createSync(this);
Flowable.just(1)
    .observeOn(bukkitScheduler)
    .map(value -> {
        // This will be on the main server thread
    });
```

## NOTE

This is currently just a little playground project.
