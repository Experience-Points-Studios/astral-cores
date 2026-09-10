package de.ep.astralcores.advancement.trigger.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TraveledOnBlockTrigger
        extends SimpleCriterionTrigger<TraveledOnBlockTrigger.Conditions> {

    // Stores the last known position and traveled distance for each player.
    // The UUID is used as the key so the data can be found again on the next trigger call.
    private final Map<UUID, PlayerData> players = new HashMap<>();

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayer player) {
        UUID uuid = player.getUUID();
        Vec3 current = player.position();

        // Get the player's previous position and accumulated distance.
        PlayerData old = players.get(uuid);

        // On the first call there is no previous position.
        // We only save the current position and start the distance at 0.
        if (old == null) {
            players.put(uuid, new PlayerData(current, 0));
            return;
        }

        // Only calculate horizontal movement.
        // Y movement is ignored, so jumping/falling does not add to the traveled distance.
        double dx = current.x - old.position().x;
        double dz = current.z - old.position().z;
        double moved = Math.sqrt(dx * dx + dz * dz);

        // If the player is riding an entity, use the vehicle's position and ground state.
        // Otherwise, use the player itself.
        Entity entity = player.getVehicle() != null
                ? player.getVehicle()
                : player;

        // Update the stored position before checking whether the movement should count.
        // This prevents movement during invalid states from being counted later.
        players.put(uuid, new PlayerData(current, old.distance()));

        // Spectators should never accumulate distance.
        // Players who are not on the ground also do not count unless they are riding a vehicle.
        if (player.isSpectator() || (!entity.onGround() && player.getVehicle() == null)) {
            return;
        }

        // Ignore calls where the player has not moved horizontally.
        if (moved <= 0) {
            return;
        }

        // Get the block directly below the player/vehicle.
        // This is the block the player is considered to be traveling on.
        BlockPos pos = entity.blockPosition().below();
        BlockState state = player.level().getBlockState(pos);

        // Add the newly traveled distance to the player's accumulated distance.
        double distance = old.distance() + moved;

        // Save the updated distance so it can be continued on the next trigger call.
        players.put(uuid, new PlayerData(current, distance));

        // Check all advancement conditions registered for this trigger.
        // The advancement is completed when the block matches and enough distance was traveled.
        trigger(player, conditions ->
                conditions.requirementsMet(state, distance)
        );
    }

    // Removes all stored movement data for a player.
    // This is useful when the player leaves the server so their data does not stay in memory.
    public void removePlayer(UUID uuid) {
        players.remove(uuid);
    }

    // Stores the player's last position and the total distance traveled.
    private record PlayerData(Vec3 position, double distance) {
    }

    public record Conditions(
            Optional<ContextAwarePredicate> playerPredicate,
            Block block,
            double distance
    ) implements SimpleCriterionTrigger.SimpleInstance {

        // Defines how the advancement condition is read from JSON.
        // For example, "block" identifies the block and "distance" defines
        // how many blocks the player needs to travel.
        public static final Codec<Conditions> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                        // The player predicate is optional and can contain additional
                        // conditions about the player who activates the trigger.
                        ContextAwarePredicate.CODEC
                                .optionalFieldOf("player")
                                .forGetter(Conditions::player),

                        // Converts the block name from JSON into an actual Minecraft Block.
                        BuiltInRegistries.BLOCK
                                .byNameCodec()
                                .fieldOf("block")
                                .forGetter(Conditions::block),

                        // Reads the required travel distance from JSON.
                        Codec.DOUBLE
                                .fieldOf("distance")
                                .forGetter(Conditions::distance)

                ).apply(instance, Conditions::new));

        @Override
        public Optional<ContextAwarePredicate> player() {
            return playerPredicate;
        }

        // Checks whether the block underneath the player matches the required block
        // and whether the player has traveled at least the required distance.
        public boolean requirementsMet(
                BlockState state,
                double traveledDistance
        ) {
            return state.is(block) && traveledDistance >= distance;
        }

        // Convenience method for creating conditions without a player predicate.
        public static Conditions of(Block block, double distance) {
            return new Conditions(
                    Optional.empty(),
                    block,
                    distance
            );
        }
    }
}