package de.ep.astralcores.manager;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class NetherTimeManager {

    private static final String TIME_OBJECTIVE =
            "astralcores_nether_time";

    private static final Map<
            UUID,
            Map<Long, ServerBossEvent>
            > BOSS_BARS =
            new HashMap<>();

    private NetherTimeManager() {
    }

    public static int getOrAddEntry(
            ServerPlayer player,
            long requiredTicks
    ) {
        if (player.level().dimension() != Level.NETHER) {
            reset(player);
            return 0;
        }

        MinecraftServer server =
                player.level().getServer();

        if (server == null) {
            return 0;
        }

        Objective objective =
                getTimeObjective(server);

        ScoreAccess score =
                server.getScoreboard()
                        .getOrCreatePlayerScore(
                                player,
                                objective
                        );

        int elapsed =
                Math.max(
                        0,
                        score.get()
                );

        elapsed++;

        score.set(elapsed);

        updateBossBar(
                player,
                requiredTicks,
                elapsed
        );

        return elapsed;
    }

    private static Objective getTimeObjective(
            MinecraftServer server
    ) {
        ServerScoreboard scoreboard =
                server.getScoreboard();

        Objective objective =
                scoreboard.getObjective(
                        TIME_OBJECTIVE
                );

        if (objective == null) {
            objective =
                    scoreboard.addObjective(
                            TIME_OBJECTIVE,
                            ObjectiveCriteria.DUMMY,
                            Component.literal(
                                    "Nether Time"
                            ),
                            ObjectiveCriteria.RenderType.INTEGER,
                            false,
                            null
                    );
        }

        return objective;
    }

    private static void updateBossBar(
            ServerPlayer player,
            long requiredTicks,
            int elapsedTicks
    ) {
        UUID uuid =
                player.getUUID();

        Map<Long, ServerBossEvent> playerBars =
                BOSS_BARS.computeIfAbsent(
                        uuid,
                        ignored -> new HashMap<>()
                );

        ServerBossEvent bossBar =
                playerBars.computeIfAbsent(
                        requiredTicks,
                        ignored -> createBossBar(
                                player,
                                requiredTicks
                        )
                );

        bossBar.addPlayer(player);

        bossBar.setProgress(
                calculateProgress(
                        elapsedTicks,
                        requiredTicks
                )
        );

        bossBar.setName(
                createBossBarName(
                        elapsedTicks,
                        requiredTicks
                )
        );
    }

    private static ServerBossEvent createBossBar(
            ServerPlayer player,
            long requiredTicks
    ) {
        ServerBossEvent bossBar =
                new ServerBossEvent(
                        UUID.randomUUID(),
                        createBossBarName(
                                0,
                                requiredTicks
                        ),
                        ServerBossEvent.BossBarColor.RED,
                        ServerBossEvent.BossBarOverlay.PROGRESS
                );

        bossBar.addPlayer(player);

        return bossBar;
    }

    private static float calculateProgress(
            int elapsedTicks,
            long requiredTicks
    ) {
        if (requiredTicks <= 0) {
            return 0.0F;
        }

        return Math.clamp(
                1.0F -
                        (float) elapsedTicks /
                                (float) requiredTicks,
                0.0F,
                1.0F
        );
    }

    private static Component createBossBarName(
            int elapsedTicks,
            long requiredTicks
    ) {
        long remaining =
                Math.max(
                        0L,
                        requiredTicks - elapsedTicks
                );

        long totalSeconds =
                remaining / 20L;

        long hours =
                totalSeconds / 3600L;

        long minutes =
                (totalSeconds % 3600L) / 60L;

        long seconds =
                totalSeconds % 60L;

        return Component.literal(
                "Nether Time: " +
                        String.format(
                                "%02d:%02d:%02d",
                                hours,
                                minutes,
                                seconds
                        )
        );
    }

    public static int getElapsedTicks(
            ServerPlayer player
    ) {
        MinecraftServer server =
                player.level().getServer();

        if (server == null) {
            return 0;
        }

        Objective objective =
                server.getScoreboard()
                        .getObjective(
                                TIME_OBJECTIVE
                        );

        if (objective == null) {
            return 0;
        }

        ScoreAccess score =
                server.getScoreboard()
                        .getOrCreatePlayerScore(
                                player,
                                objective
                        );

        return Math.max(
                0,
                score.get()
        );
    }

    public static void reset(
            ServerPlayer player
    ) {
        MinecraftServer server =
                player.level().getServer();

        if (server == null) {
            return;
        }

        ServerScoreboard scoreboard =
                server.getScoreboard();

        Objective objective =
                scoreboard.getObjective(
                        TIME_OBJECTIVE
                );

        if (objective != null) {
            scoreboard.resetSinglePlayerScore(
                    player,
                    objective
            );
        }

        removeBossBars(player);
    }

    private static void removeBossBars(
            ServerPlayer player
    ) {
        UUID uuid =
                player.getUUID();

        Map<Long, ServerBossEvent> playerBars =
                BOSS_BARS.remove(uuid);

        if (playerBars == null) {
            return;
        }

        for (ServerBossEvent bossBar :
                playerBars.values()) {

            bossBar.removeAllPlayers();
        }
    }

    public static void removePlayer(
            ServerPlayer player
    ) {
        removeBossBars(player);
    }

    public static void onServerStopping() {
        for (Map<Long, ServerBossEvent> playerBars :
                BOSS_BARS.values()) {

            for (ServerBossEvent bossBar :
                    playerBars.values()) {

                bossBar.removeAllPlayers();
            }
        }

        BOSS_BARS.clear();
    }

    public static void clear(
            MinecraftServer server
    ) {
        ServerScoreboard scoreboard =
                server.getScoreboard();

        Objective objective =
                scoreboard.getObjective(
                        TIME_OBJECTIVE
                );

        if (objective != null) {
            scoreboard.removeObjective(
                    objective
            );
        }

        for (Map<Long, ServerBossEvent> playerBars :
                BOSS_BARS.values()) {

            for (ServerBossEvent bossBar :
                    playerBars.values()) {

                bossBar.removeAllPlayers();
            }
        }

        BOSS_BARS.clear();
    }
}