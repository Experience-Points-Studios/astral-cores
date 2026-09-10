package de.ep.astralcores.advancement.trigger.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TraveledOnBlockTrigger
        extends SimpleCriterionTrigger<TraveledOnBlockTrigger.Conditions> {

    /**
     * Stores the last position and accumulated distance for every player.
     */
    private final Map<UUID, PlayerData> players = new HashMap<>();

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    /**
     * Call this once every server tick for every player.
     */
    public void tick(ServerPlayer player) {
        UUID uuid = player.getUUID();

        Vec3 currentPosition = player.position();

        PlayerData oldData = players.get(uuid);

        /*
         * First tick for this player.
         * Just initialize their position.
         */
        if (oldData == null) {
            players.put(uuid, new PlayerData(currentPosition, 0.0));
            return;
        }

        Vec3 previousPosition = oldData.position();

        /*
         * Always update the position, even if we don't count
         * the movement.
         */
        double dx = currentPosition.x - previousPosition.x;
        double dz = currentPosition.z - previousPosition.z;

        double horizontalDistance = Math.sqrt(
                dx * dx + dz * dz
        );

        double accumulatedDistance = oldData.distance();

        /*
         * Don't count spectators.
         */
        if (player.isSpectator()) {
            players.put(
                    uuid,
                    new PlayerData(currentPosition, accumulatedDistance)
            );
            return;
        }

        /*
         * Don't count flying / elytra movement.
         *
         * When riding a horse, check the vehicle instead because
         * the horse is the entity actually touching the ground.
         */
        var entity = player.getVehicle() != null
                ? player.getVehicle()
                : player;

        if (!entity.onGround()) {
            players.put(
                    uuid,
                    new PlayerData(currentPosition, accumulatedDistance)
            );
            return;
        }

        /*
         * Don't count if there was no horizontal movement.
         */
        if (horizontalDistance <= 0.0) {
            players.put(
                    uuid,
                    new PlayerData(currentPosition, accumulatedDistance)
            );
            return;
        }

        /*
         * Block directly underneath the entity.
         */
        BlockPos blockPos = entity.blockPosition().below();
        BlockState blockState = player.level().getBlockState(blockPos);

        /*
         * We accumulate distance here.
         *
         * The actual block requirement is checked by the
         * advancement's Conditions below.
         */
        accumulatedDistance += horizontalDistance;

        players.put(
                uuid,
                new PlayerData(currentPosition, accumulatedDistance)
        );

        /*
         * Test all advancement criteria registered for this trigger.
         */
        double finalDistance = accumulatedDistance;

        this.trigger(player, conditions ->
                conditions.requirementsMet(
                        player,
                        blockState,
                        finalDistance
                )
        );
    }

    /**
     * Remove a player from the tracking map.
     */
    public void removePlayer(UUID uuid) {
        players.remove(uuid);
    }

    /**
     * Reset accumulated distance for a player.
     */
    public void resetPlayer(UUID uuid) {
        PlayerData data = players.get(uuid);

        if (data != null) {
            players.put(
                    uuid,
                    new PlayerData(data.position(), 0.0)
            );
        }
    }

    /**
     * Get the currently accumulated distance.
     */
    public double getDistance(UUID uuid) {
        PlayerData data = players.get(uuid);

        return data == null
                ? 0.0
                : data.distance();
    }

    /**
     * Per-player tracking data.
     */
    private record PlayerData(
            Vec3 position,
            double distance
    ) {
    }

    /**
     * Advancement criterion conditions.
     */
    public record Conditions(
            Optional<ContextAwarePredicate> playerPredicate,
            Block block,
            double distance
    ) implements SimpleCriterionTrigger.SimpleInstance {

        /**
         * Codec for:
         *
         * {
         *   "player": ...,
         *   "block": "minecraft:ice",
         *   "distance": 100.0
         * }
         */
        public static final Codec<Conditions> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(

                        ContextAwarePredicate.CODEC
                                .optionalFieldOf("player")
                                .forGetter(Conditions::player),

                        BuiltInRegistries.BLOCK
                                .byNameCodec()
                                .fieldOf("block")
                                .forGetter(
                                        conditions -> conditions.block()
                                ),

                        Codec.DOUBLE
                                .fieldOf("distance")
                                .forGetter(
                                        Conditions::distance
                                )

                ).apply(instance, Conditions::new));

        @Override
        public Optional<ContextAwarePredicate> player() {
            return playerPredicate;
        }

        /**
         * Check whether this criterion has been satisfied.
         */
        public boolean requirementsMet(
                ServerPlayer player,
                BlockState blockState,
                double traveledDistance
        ) {

            /*
             * The player must have traveled at least
             * the configured distance.
             */
            if (traveledDistance < distance) {
                return false;
            }

            /*
             * The block underneath must match.
             */
            return blockState.is(block);
        }

        /**
         * Convenient constructor for Java advancement generation.
         */
        public static Conditions of(
                Block block,
                double distance
        ) {
            return new Conditions(
                    Optional.empty(),
                    block,
                    distance
            );
        }

        /**
         * Constructor with a player predicate.
         */
        public static Conditions of(
                Optional<ContextAwarePredicate> playerPredicate,
                Block block,
                double distance
        ) {
            return new Conditions(
                    playerPredicate,
                    block,
                    distance
            );
        }
    }
}