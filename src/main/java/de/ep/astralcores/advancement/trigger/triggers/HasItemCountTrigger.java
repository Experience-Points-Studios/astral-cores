package de.ep.astralcores.advancement.trigger.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.ep.astralcores.advancement.trigger.TriggerRegistry;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class HasItemCountTrigger extends SimpleCriterionTrigger<HasItemCountTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    // Iterates through the player's inventory to count matching items and trigger the advancement
    public void trigger(ServerPlayer player) {
        this.trigger(player, instance -> {
            int total = 0;

            // Safely iterate through the container size since direct field access is restricted
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);

                if (stack.is(instance.item())) {
                    total += stack.getCount();

                    if (total >= instance.count()) {
                        return true;
                    }
                }
            }

            return false;
        });
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player,
            Item item,
            int count
    ) implements SimpleCriterionTrigger.SimpleInstance {

        // JSON configuration codec for serializing and deserializing the criteria parameters
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(TriggerInstance::item),
                Codec.INT.fieldOf("count").forGetter(TriggerInstance::count)
        ).apply(instance, TriggerInstance::new));

        // Helper method to easily construct the criterion programmatically
        public static Criterion<TriggerInstance> hasItem(Item item, int count) {
            return new Criterion<>(
                    TriggerRegistry.HAS_ITEM_COUNT,
                    new TriggerInstance(Optional.empty(), item, count)
            );
        }
    }
}
