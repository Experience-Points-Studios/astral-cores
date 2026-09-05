package de.ep.astralcores.manager;

import de.ep.astralcores.advancement.criterion.CriterionRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class NetherTimeManager {

    private static final int REQUIRED_TIME =
            12 * 60 * 60 * 20;

    private static final String TIME_OBJECTIVE =
            "astralcores_nether_time";

    private static final String START_OBJECTIVE =
            "astralcores_nether_start";

    private static final Map<UUID, ServerBossEvent> BOSS_BARS =
            new HashMap<>();

    private static final Set<UUID> COMPLETED =
            new HashSet<>();

    public static final int REQUIRED_TICKS =
            12 * 60 * 60 * 20;

    private static boolean initialized = false;

    public static void init(
            MinecraftServer server
    ) {

        if (initialized) {
            return;
        }

        Objective objective =
                getObjective(server);

        for (ServerPlayer player :
                server.getPlayerList().getPlayers()) {

            int ticks =
                    getTime(
                            server,
                            objective,
                            player
                    );

            if (ticks >= REQUIRED_TIME) {
                COMPLETED.add(
                        player.getUUID()
                );
            }
        }

        initialized = true;
    }

    public static void tick(
            MinecraftServer server
    ) {

        init(server);

        Objective timeObjective =
                getObjective(server);

        Objective startObjective =
                getStartObjective(server);

        for (ServerPlayer player :
                server.getPlayerList().getPlayers()) {

            UUID uuid =
                    player.getUUID();

            if (COMPLETED.contains(uuid)) {

                ServerBossEvent bossBar =
                        BOSS_BARS.get(uuid);

                if (bossBar != null) {
                    bossBar.removePlayer(player);
                }

                continue;
            }

            if (player.level().dimension() != Level.NETHER) {

                clearPlayer(
                        server,
                        player,
                        timeObjective,
                        startObjective
                );

                removeBossBar(uuid);

                continue;
            }

            int elapsed =
                    getTime(
                            server,
                            timeObjective,
                            player
                    );

            int startAmount =
                    getTime(
                            server,
                            startObjective,
                            player
                    );

            if (startAmount == 0) {

                setTime(
                        server,
                        startObjective,
                        player,
                        elapsed
                );
            }

            elapsed =
                    Math.min(
                            REQUIRED_TIME,
                            elapsed + 1
                    );

            setTime(
                    server,
                    timeObjective,
                    player,
                    elapsed
            );

            if (elapsed >= REQUIRED_TIME) {

                COMPLETED.add(uuid);

                CriterionRegistry.NETHER_TIME.trigger(
                        player,
                        elapsed
                );

                removeBossBar(uuid);

                continue;
            }

            ServerBossEvent bossBar =
                    BOSS_BARS.computeIfAbsent(
                            uuid,
                            id -> createBossBar()
                    );

            bossBar.addPlayer(player);

            bossBar.setProgress(
                    calculateProgress(elapsed)
            );

            bossBar.setName(
                    createBossBarName(elapsed)
            );
        }
    }

    private static Objective getObjective(
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

    private static Objective getStartObjective(
            MinecraftServer server
    ) {

        ServerScoreboard scoreboard =
                server.getScoreboard();

        Objective objective =
                scoreboard.getObjective(
                        START_OBJECTIVE
                );

        if (objective == null) {

            objective =
                    scoreboard.addObjective(
                            START_OBJECTIVE,
                            ObjectiveCriteria.DUMMY,
                            Component.literal(
                                    "Nether Start"
                            ),
                            ObjectiveCriteria.RenderType.INTEGER,
                            false,
                            null
                    );
        }

        return objective;
    }

    private static int getTime(
            MinecraftServer server,
            Objective objective,
            ServerPlayer player
    ) {

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

    private static void setTime(
            MinecraftServer server,
            Objective objective,
            ServerPlayer player,
            int ticks
    ) {

        ScoreAccess score =
                server.getScoreboard()
                        .getOrCreatePlayerScore(
                                player,
                                objective
                        );

        score.set(
                Math.max(
                        0,
                        ticks
                )
        );
    }

    private static void clearPlayer(
            MinecraftServer server,
            ServerPlayer player,
            Objective timeObjective,
            Objective startObjective
    ) {

        ServerScoreboard scoreboard =
                server.getScoreboard();

        scoreboard.resetSinglePlayerScore(
                player,
                timeObjective
        );

        scoreboard.resetSinglePlayerScore(
                player,
                startObjective
        );

        COMPLETED.remove(
                player.getUUID()
        );
    }

    private static ServerBossEvent createBossBar() {

        return new ServerBossEvent(
                UUID.randomUUID(),
                Component.literal(
                        "Nether Time"
                ),
                ServerBossEvent.BossBarColor.RED,
                ServerBossEvent.BossBarOverlay.PROGRESS
        );
    }

    private static float calculateProgress(
            int elapsed
    ) {

        return Math.clamp(
                1.0F -
                        (float) elapsed /
                                REQUIRED_TIME,
                0.0F,
                1.0F
        );
    }

    private static Component createBossBarName(
            int elapsed
    ) {

        int remaining =
                Math.max(
                        0,
                        REQUIRED_TIME - elapsed
                );

        int totalSeconds =
                remaining / 20;

        int hours =
                totalSeconds / 3600;

        int minutes =
                (totalSeconds % 3600) / 60;

        int seconds =
                totalSeconds % 60;

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

    private static void removeBossBar(
            UUID uuid
    ) {

        ServerBossEvent bossBar =
                BOSS_BARS.remove(uuid);

        if (bossBar != null) {
            bossBar.removeAllPlayers();
        }
    }

    public static void removePlayer(
            ServerPlayer player
    ) {

        removeBossBar(
                player.getUUID()
        );
    }

    public static void onServerStopping(
            MinecraftServer server
    ) {

        for (ServerBossEvent bossBar :
                BOSS_BARS.values()) {

            bossBar.removeAllPlayers();
        }

        BOSS_BARS.clear();

        COMPLETED.clear();

        initialized = false;
    }

    public static void clear(
            MinecraftServer server
    ) {

        Scoreboard scoreboard =
                server.getScoreboard();

        Objective timeObjective =
                scoreboard.getObjective(
                        TIME_OBJECTIVE
                );

        Objective startObjective =
                scoreboard.getObjective(
                        START_OBJECTIVE
                );

        if (timeObjective != null) {
            scoreboard.removeObjective(
                    timeObjective
            );
        }

        if (startObjective != null) {
            scoreboard.removeObjective(
                    startObjective
            );
        }

        COMPLETED.clear();

        for (ServerBossEvent bossBar :
                BOSS_BARS.values()) {

            bossBar.removeAllPlayers();
        }

        BOSS_BARS.clear();

        initialized = false;
    }
}
