package de.ep.astralcores.core.respawn;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreFactory;
import de.ep.astralcores.core.CoreRegistry;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.respawn.data.CoreRespawnData;
import de.ep.astralcores.manager.AltarManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.*;

// Handles active core respawn timers and their boss bars
public class CoreRespawnManager {

    // Maps every individual respawn timer to its boss bar
    private static final Map<
            CoreRespawnData,
            ServerBossEvent
            > BOSS_BARS =
            new IdentityHashMap<>();

    // Prevents unnecessary boss bar updates more than once per second
    private static long lastBossBarUpdate;

    // Adds a new respawn timer for a core
    public static void addRespawn(
            CoreType type
    ) {
        Core core = CoreRegistry.get(type);

        long startTimestamp =
                System.currentTimeMillis() / 1000L;

        long endTimestamp =
                startTimestamp
                        + core.getRespawnDuration();

        CoreRespawnDataManager data =
                AstralCores.CORE_RESPAWN_DATA;

        if (data == null) {
            return;
        }

        data.addRespawn(
                type,
                startTimestamp,
                endTimestamp
        );
    }

    // Checks all respawn timers every server tick
    public static void tick() {

        CoreRespawnDataManager data =
                AstralCores.CORE_RESPAWN_DATA;

        if (data == null) {
            return;
        }

        long now =
                System.currentTimeMillis() / 1000L;

        List<CoreRespawnData> respawns =
                new ArrayList<>(
                        data.getAllRespawns()
                );

        respawns.sort(
                Comparator.comparingLong(
                        CoreRespawnData::endTimestamp
                )
        );

        for (CoreRespawnData respawn :
                respawns) {

            // Respawn timer has finished
            if (respawn.endTimestamp() <= now) {

                spawnCore(
                        respawn.type()
                );

                data.removeRespawn(
                        respawn
                );

                removeBossBar(
                        respawn
                );

                continue;
            }

            // Timer has not started yet
            if (now < respawn.startTimestamp()) {
                continue;
            }

            ServerBossEvent bossBar =
                    BOSS_BARS.computeIfAbsent(
                            respawn,
                            CoreRespawnManager::createBossBar
                    );

            // Only update boss bars once per second
            if (now != lastBossBarUpdate) {

                bossBar.setProgress(
                        calculateProgress(
                                respawn,
                                now
                        )
                );

                bossBar.setName(
                        createBossBarName(
                                respawn,
                                now
                        )
                );
            }
        }

        lastBossBarUpdate = now;

        cleanupBossBars(
                respawns
        );
    }

    // Creates a boss bar for an individual respawn timer
    private static ServerBossEvent createBossBar(
            CoreRespawnData respawn
    ) {
        Core core = CoreRegistry.get(respawn.type());

        ServerBossEvent bossBar =
                new ServerBossEvent(
                        UUID.randomUUID(),
                        createBossBarName(
                                respawn,
                                System.currentTimeMillis() / 1000L
                        ),
                        core.getBossBarColor(),
                        ServerBossEvent.BossBarOverlay.PROGRESS
                );

        MinecraftServer server =
                AstralCores.getServer();

        if (server == null) {
            return bossBar;
        }

        for (ServerPlayer player :
                server.getPlayerList().getPlayers()) {

            bossBar.addPlayer(player);
        }

        return bossBar;
    }

    // Removes the boss bar belonging to a completed respawn
    private static void removeBossBar(
            CoreRespawnData respawn
    ) {
        ServerBossEvent bossBar =
                BOSS_BARS.remove(respawn);

        if (bossBar != null) {
            bossBar.removeAllPlayers();
        }
    }

    // Removes boss bars for timers that no longer exist
    private static void cleanupBossBars(
            List<CoreRespawnData> respawns
    ) {
        BOSS_BARS.entrySet()
                .removeIf(
                        entry -> {

                            if (respawns.contains(
                                    entry.getKey()
                            )) {
                                return false;
                            }

                            entry.getValue()
                                    .removeAllPlayers();

                            return true;
                        }
                );
    }

    // Calculates the remaining boss bar progress
    private static float calculateProgress(
            CoreRespawnData respawn,
            long now
    ) {
        long start =
                respawn.startTimestamp();

        long end =
                respawn.endTimestamp();

        long duration =
                end - start;

        if (duration <= 0) {
            return 0.0F;
        }

        long remaining =
                end - now;

        return Math.clamp(
                (float) remaining / duration,
                0.0F,
                1.0F
        );
    }

    // Creates the text displayed by the boss bar
    private static Component createBossBarName(
            CoreRespawnData respawn,
            long now
    ) {
        long remaining =
                Math.max(
                        0L,
                        respawn.endTimestamp() - now
                );

        Core core = CoreRegistry.get(respawn.type());

        Vec3 corePos = AltarManager.getCoreRespawnPos(
                AstralCores.CORE_RESPAWN_DATA.getAltar()
        );

        return Component.literal(
                core.getName().getString())
                .append(" respawning in ")
                .append(formatTime(remaining))
                .append(" at ")
                .append(String.valueOf((int) corePos.x))
                .append(", ")
                .append(String.valueOf((int) corePos.y))
                .append(", ")
                .append(String.valueOf((int) corePos.z)
        );
    }

    // Formats remaining seconds into a readable time string
    private static String formatTime(
            long seconds
    ) {
        long days =
                seconds / 86400L;

        seconds %= 86400L;

        long hours =
                seconds / 3600L;

        seconds %= 3600L;

        long minutes =
                seconds / 60L;

        seconds %= 60L;

        StringBuilder result =
                new StringBuilder();

        if (days != 0) {
            result.append(days)
                    .append("d ");
        }

        if (hours != 0) {
            result.append(hours)
                    .append("h ");
        }

        if (minutes != 0) {
            result.append(minutes)
                    .append("m ");
        }

        if (seconds != 0 || result.isEmpty()) {
            result.append(seconds)
                    .append("s");
        }

        return result.toString().trim();
    }

    // Spawns the core when its respawn timer finishes
    private static void spawnCore(
            CoreType type
    ) {
        CoreRespawnDataManager data =
                AstralCores.CORE_RESPAWN_DATA;

        if (!data.altarExists()) {
            AstralCores.LOGGER.warn(
                    "Cannot respawn {}: no altar exists.",
                    type.name()
            );

            return;
        }

        ServerLevel level = AstralCores.getServer().overworld();
        Vec3 corePos = AltarManager.getCoreRespawnPos(data.getAltar());
        Core core = CoreRegistry.get(type);

        ItemStack coreStack = CoreFactory.createStack(core);
        ItemEntity coreEntity = new ItemEntity(level, corePos.x, corePos.y, corePos.z, coreStack);
        coreEntity.setDeltaMovement(Vec3.ZERO);

        level.addFreshEntity(coreEntity);

        AstralCores.LOGGER.info(
                "Respawning core: {}",
                type.name()
        );

        Component title = Component.literal(
                core.getName().getString())
                .append(" has respawned at ")
                .append(String.valueOf((int) corePos.x))
                .append(", ")
                .append(String.valueOf((int) corePos.y))
                .append(", ")
                .append(String.valueOf((int) corePos.z))
                .withStyle(core.getName().getStyle());

        for (ServerPlayer player : AstralCores.getServer().getPlayerList().getPlayers()) {

            player.connection.send(
                    new ClientboundSetTitleTextPacket(Component.empty())
            );

            player.connection.send(
                    new ClientboundSetSubtitleTextPacket(title)
            );
        }
    }

    // Adds a newly joined player to every active boss bar
    public static void addPlayer(
            ServerPlayer player
    ) {
        for (ServerBossEvent bossBar :
                BOSS_BARS.values()) {

            bossBar.addPlayer(player);
        }
    }

    // Removes a disconnected player from every active boss bar
    public static void removePlayer(
            ServerPlayer player
    ) {
        for (ServerBossEvent bossBar :
                BOSS_BARS.values()) {

            bossBar.removePlayer(player);
        }
    }

    // Removes all active boss bars
    public static void clear() {
        for (ServerBossEvent bossBar :
                BOSS_BARS.values()) {

            bossBar.removeAllPlayers();
        }

        BOSS_BARS.clear();
    }
}