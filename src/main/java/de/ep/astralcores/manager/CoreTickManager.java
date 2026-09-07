package de.ep.astralcores.manager;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreRegistry;
import de.ep.astralcores.playerdata.PlayerData;
import net.minecraft.server.level.ServerPlayer;

public class CoreTickManager {

    public static void tickPassiveAbility(
            ServerPlayer player,
            PlayerData data
    ) {
        if (data == null || data.getEquippedCore() == null) {
            return;
        }

        Core core = CoreRegistry.get(data.getEquippedCore());
        core.applyPassive(player);
    }

    public static void tick(
            ServerPlayer player,
            PlayerData data
    ) {
        if (data == null || data.getEquippedCore() == null) {
            return;
        }

        Core core = CoreRegistry.get(data.getEquippedCore());
        core.tick(player);
    }
}