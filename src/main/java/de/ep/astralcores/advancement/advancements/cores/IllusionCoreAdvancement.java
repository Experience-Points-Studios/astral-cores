package de.ep.astralcores.advancement.advancements.cores;

import de.ep.astralcores.util.AdvancementUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.predicates.MobEffectsPredicate;
import net.minecraft.advancements.triggers.EffectsChangedTrigger;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.function.Consumer;

public class IllusionCoreAdvancement {

    // All effects the player must have active at the same time.
    private static final List<Holder<MobEffect>> REQUIRED_EFFECTS = List.of(
            MobEffects.SPEED,
            MobEffects.SLOWNESS,
            MobEffects.STRENGTH,
            MobEffects.INSTANT_HEALTH,
            MobEffects.INSTANT_DAMAGE,
            MobEffects.JUMP_BOOST,
            MobEffects.REGENERATION,
            MobEffects.FIRE_RESISTANCE,
            MobEffects.WATER_BREATHING,
            MobEffects.INVISIBILITY,
            MobEffects.NIGHT_VISION,
            MobEffects.WEAKNESS,
            MobEffects.POISON,
            MobEffects.SLOW_FALLING,
            MobEffects.WIND_CHARGED,
            MobEffects.WEAVING,
            MobEffects.OOZING,
            MobEffects.INFESTED
    );

    public static void generate(Consumer<AdvancementHolder> consumer) {
        masterOfBrewing(consumer);
    }

    private static void masterOfBrewing(Consumer<AdvancementHolder> consumer) {
        Advancement.Builder builder = Advancement.Builder.advancement()
                .display(
                        Items.AMETHYST_SHARD,
                        Component.literal("Master of Brewing"),
                        Component.literal("Have all brewable potion effects at the same time"),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                );

        // The predicate checks which effects are currently active on the player.
        MobEffectsPredicate.Builder effectsPredicate = new MobEffectsPredicate.Builder();

        // and(...) means every listed effect is required, not just one of them.
        for (Holder<MobEffect> effect : REQUIRED_EFFECTS) {
            effectsPredicate.and(effect);
        }

        // This trigger runs whenever the player's active effects change.
        builder.addCriterion(
                "effects",
                EffectsChangedTrigger.TriggerInstance.hasEffects(effectsPredicate)
        );

        builder
                .rewards(AdvancementUtil.reward("illusion_core"))
                .save(
                        consumer,
                        AdvancementUtil.advancementId("core/illusion_core")
                );
    }
}