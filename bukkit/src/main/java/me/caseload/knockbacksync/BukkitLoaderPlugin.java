package me.caseload.knockbacksync;

import com.github.retrooper.packetevents.PacketEvents;
import me.caseload.knockbacksync.scheduler.SchedulerAdapter;
import org.bukkit.plugin.java.JavaPlugin;

public final class BukkitLoaderPlugin extends JavaPlugin {

    private final Base core = new BukkitBase(this);

    @Override
    public void onLoad() {
        core.load();
    }

    @Override
    public void onEnable() {
        core.enable();
    }

    @Override
    public void onDisable() {
        // enable() may have aborted before the scheduler/PacketEvents were initialized;
        // guard against nulls so we don't mask the original failure with an NPE on disable.
        SchedulerAdapter scheduler = core.getScheduler();
        if (scheduler != null)
            scheduler.shutdown();

        // Only tear PacketEvents down if we were the ones who built it. In the "lite" build the
        // instance belongs to the standalone packetevents plugin and is shared with GrimAC,
        // InteractiveChat, NotBounties and anything else on the server; terminating it here would
        // break all of them the moment this plugin is disabled or reloaded.
        if (core.ownsPacketEvents() && PacketEvents.getAPI() != null)
            PacketEvents.getAPI().terminate();
    }
}