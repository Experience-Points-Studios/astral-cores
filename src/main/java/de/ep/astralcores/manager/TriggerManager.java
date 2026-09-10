package de.ep.astralcores.manager;

import de.ep.astralcores.advancement.trigger.TriggerRegistry;
import net.minecraft.server.level.ServerPlayer;

public final class TriggerManager {


    public static void tick(ServerPlayer player) {
        TriggerRegistry.VOID_SURVIVAL.trigger(player);
        TriggerRegistry.NETHER_TIME.trigger(player);
        TriggerRegistry.TRAVELED_ON_BLOCK.trigger(player);
    }

    public static void onPlayerDisconnect(ServerPlayer player) {
        TriggerRegistry.VOID_SURVIVAL.removePlayer(player.getUUID());
        TriggerRegistry.TRAVELED_ON_BLOCK.removePlayer(player.getUUID());
    }

    public static void onInventoryChange(ServerPlayer player) {
        TriggerRegistry.HAS_ITEM_COUNT.trigger(player);
        TriggerRegistry.COMPASS.trigger(player);
    }
}
