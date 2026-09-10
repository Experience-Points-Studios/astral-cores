package de.ep.astralcores.advancement.advancements.cores;

import de.ep.astralcores.util.AdvancementUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class BerserkerCoreAdvancement {

    private static final Map<EntityType<?>, Integer> MOBS = Map.of(
            EntityTypes.PIGLIN, 1,
            EntityTypes.ZOMBIFIED_PIGLIN, 1,
            EntityTypes.PIGLIN_BRUTE, 1
    );

    public static void generate(

            Consumer<AdvancementHolder> consumer

    ) {
        dareDevil(consumer);
    }

    private static void dareDevil(
            Consumer<AdvancementHolder> consumer
    ) {
        Advancement.Builder builder = Advancement.Builder.advancement()
                .display(
                        Items.BLAZE_POWDER,
                        Component.literal("Dare Devil"),
                        Component.literal(""),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                );

        MOBS.forEach((entityType, count) -> {
            builder.addCriterion(
                    entityType.toString(),
                    AdvancementUtil.entityKilled(entityType, count)
            );
        });

        builder

                .rewards(
                        AdvancementUtil.reward("berserker_core")
                )
                .save(
                        consumer,
                        AdvancementUtil.advancementId("core/berserker_core")
                );

    }
}
