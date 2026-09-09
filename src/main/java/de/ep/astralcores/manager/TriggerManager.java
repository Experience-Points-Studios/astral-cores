package de.ep.astralcores.manager;

import de.ep.astralcores.advancement.trigger.TriggerRegistry;
import net.minecraft.server.level.ServerPlayer;

public final class TriggerManager {


    public static void tick(ServerPlayer player) {
        TriggerRegistry.VOID_SURVIVAL.trigger(player);
        TriggerRegistry.NETHER_TIME.trigger(player);
    }

    public static void onPlayerDisconnect(ServerPlayer player) {
        TriggerRegistry.VOID_SURVIVAL.removePlayer(player.getUUID());
    }
}
