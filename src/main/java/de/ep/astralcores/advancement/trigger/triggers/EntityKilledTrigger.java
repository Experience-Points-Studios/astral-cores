package de.ep.astralcores.advancement.trigger.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.ep.astralcores.advancement.trigger.TriggerRegistry;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public class EntityKilledTrigger
        extends SimpleCriterionTrigger<EntityKilledTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, EntityType<?> killedEntityType) {
        this.trigger(player, instance ->
                instance.entityType().equals(killedEntityType)
                        && instance.requirementsMet(player)
        );
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player,
            EntityType<?> entityType,
            MinMaxBounds.Ints count
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        ContextAwarePredicate.CODEC.optionalFieldOf("player")
                                .forGetter(TriggerInstance::player),

                        BuiltInRegistries.ENTITY_TYPE.byNameCodec()
                                .fieldOf("entity_type")
                                .forGetter(TriggerInstance::entityType),

                        MinMaxBounds.Ints.CODEC
                                .optionalFieldOf("count", MinMaxBounds.Ints.ANY)
                                .forGetter(TriggerInstance::count)

                ).apply(instance, TriggerInstance::new)
        );

        @Override
        public Optional<ContextAwarePredicate> player() {
            return player;
        }

        public boolean requirementsMet(ServerPlayer player) {

            player.awardStat(Stats.ENTITY_KILLED.get(entityType));

            int kills = player.getStats()
                    .getValue(Stats.ENTITY_KILLED.get(entityType));

            return count.matches(kills);
        }

        public static Criterion<TriggerInstance> killedEntity(
                EntityType<?> entityType,
                int count
        ) {
            return new Criterion<>(
                    TriggerRegistry.ENTITY_KILLED,
                    new TriggerInstance(
                            Optional.empty(),
                            entityType,
                            MinMaxBounds.Ints.atLeast(count)
                    )
            );
        }
    }
}