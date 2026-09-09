package de.ep.astralcores.advancement.advancements.cores;

import de.ep.astralcores.advancement.trigger.TriggerRegistry;
import de.ep.astralcores.advancement.trigger.triggers.NetherTimeTrigger;
import de.ep.astralcores.util.AdvancementUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.List;
import java.util.function.Consumer;

public class PhoenixCoreAdvancement {

    private static final long REQUIRED_TICKS =
            12L * 60L * 60L * 20L;

    private static final List<ResourceKey<Biome>> BIOMES = List.of(
            Biomes.NETHER_WASTES,
            Biomes.CRIMSON_FOREST,
            Biomes.WARPED_FOREST,
            Biomes.SOUL_SAND_VALLEY,
            Biomes.BASALT_DELTAS
    );

    public static void generate(
            HolderLookup.Provider lookup,
            Consumer<AdvancementHolder> consumer
    ) {
        warmLiving(
                lookup,
                consumer
        );
    }

    private static void warmLiving(
            HolderLookup.Provider lookup,
            Consumer<AdvancementHolder> consumer
    ) {
        Advancement.Builder builder =
                Advancement.Builder.advancement()
                        .display(
                                Items.BLAZE_POWDER,
                                Component.literal(
                                        "Hot Living"
                                ),
                                Component.literal(
                                        "Be for 12h in the nether and visit every nether biome"
                                ),
                                null,
                                AdvancementType.CHALLENGE,
                                true,
                                true,
                                false
                        );

        for (ResourceKey<Biome> biome : BIOMES) {
            builder.addCriterion(
                    biome.identifier().getPath(),
                    AdvancementUtil.isInBiome(
                            lookup,
                            biome
                    )
            );
        }

        builder
                .addCriterion(
                        "nether_time",
                        TriggerRegistry.NETHER_TIME.createCriterion(
                                NetherTimeTrigger.Conditions.create(
                                        REQUIRED_TICKS
                                )
                        )
                )
                .rewards(
                        AdvancementUtil.reward(
                                "phoenix_core"
                        )
                )
                .save(
                        consumer,
                        AdvancementUtil.advancementId(
                                "core/phoenix_core"
                        )
                );
    }
}