package de.ep.astralcores.advancement.trigger.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.ep.astralcores.advancement.trigger.TriggerRegistry;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CompassTrigger extends SimpleCriterionTrigger<CompassTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, instance -> {

            int recoveryCount = 0;
            Set<GlobalPos> lodestones = new HashSet<>();

            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);

                // Recovery Compasses
                if (stack.is(instance.recoveryCompass())) {
                    recoveryCount += stack.getCount();
                }

                // Lodestone Compasses
                if (stack.is(instance.lodestoneCompass())) {

                    LodestoneTracker tracker = stack.get(
                            DataComponents.LODESTONE_TRACKER
                    );

                    if (tracker != null) {
                        tracker.target().ifPresent(lodestones::add);
                    }
                }
            }

            return recoveryCount >= instance.recoveryCount()
                    && lodestones.size() >= instance.lodestoneCount();
        });
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player,
            Item lodestoneCompass,
            int lodestoneCount,
            Item recoveryCompass,
            int recoveryCount
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(

                        ContextAwarePredicate.CODEC
                                .optionalFieldOf("player")
                                .forGetter(TriggerInstance::player),

                        BuiltInRegistries.ITEM.byNameCodec()
                                .fieldOf("lodestone_compass")
                                .forGetter(TriggerInstance::lodestoneCompass),

                        Codec.INT
                                .fieldOf("lodestone_count")
                                .forGetter(TriggerInstance::lodestoneCount),

                        BuiltInRegistries.ITEM.byNameCodec()
                                .fieldOf("recovery_compass")
                                .forGetter(TriggerInstance::recoveryCompass),

                        Codec.INT
                                .fieldOf("recovery_count")
                                .forGetter(TriggerInstance::recoveryCount)

                ).apply(instance, TriggerInstance::new));

        public static Criterion<TriggerInstance> create(
                Item lodestoneCompass,
                int lodestoneCount,
                Item recoveryCompass,
                int recoveryCount
        ) {
            return new Criterion<>(
                    TriggerRegistry.COMPASS,
                    new TriggerInstance(
                            Optional.empty(),
                            lodestoneCompass,
                            lodestoneCount,
                            recoveryCompass,
                            recoveryCount
                    )
            );
        }
    }
}