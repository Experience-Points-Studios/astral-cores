package de.ep.astralcores.advancement.trigger.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.ep.astralcores.manager.NetherTimeManager;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class NetherTimeTrigger
        extends SimpleCriterionTrigger<NetherTimeTrigger.Conditions> {

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(
            ServerPlayer player
    ) {
        if (player.level().dimension() != Level.NETHER) {
            NetherTimeManager.reset(player);
            return;
        }

        this.trigger(
                player,
                conditions -> {

                    long requiredTicks =
                            conditions.requiredTicks();

                    int elapsedTicks =
                            NetherTimeManager.getOrAddEntry(
                                    player,
                                    requiredTicks
                            );

                    return elapsedTicks >= requiredTicks;
                }
        );
    }

    public record Conditions(
            Optional<ContextAwarePredicate> player,
            long requiredTicks
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<Conditions> CODEC =
                RecordCodecBuilder.create(instance ->
                        instance.group(

                                ContextAwarePredicate.CODEC
                                        .optionalFieldOf("player")
                                        .forGetter(
                                                Conditions::player
                                        ),

                                Codec.LONG
                                        .fieldOf("required_ticks")
                                        .forGetter(
                                                Conditions::requiredTicks
                                        )

                        ).apply(
                                instance,
                                Conditions::new
                        )
                );

        public static Conditions create(
                long requiredTicks
        ) {
            return new Conditions(
                    Optional.empty(),
                    requiredTicks
            );
        }
    }
}