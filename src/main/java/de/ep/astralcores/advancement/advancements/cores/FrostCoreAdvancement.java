package de.ep.astralcores.advancement.advancements.cores;

import de.ep.astralcores.advancement.trigger.TriggerRegistry;
import de.ep.astralcores.advancement.trigger.triggers.TraveledOnBlockTrigger;
import de.ep.astralcores.util.AdvancementUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.function.Consumer;

public class FrostCoreAdvancement {

    private static final List<ResourceKey<Biome>> BIOMES = List.of(
            Biomes.ICE_SPIKES,
            Biomes.SNOWY_SLOPES,
            Biomes.SNOWY_TAIGA,
            Biomes.SNOWY_PLAINS
    );

    public static void generate(

            HolderLookup.Provider lookup,
            Consumer<AdvancementHolder> consumer
    ) {
        prettyCold(lookup, consumer);
    }

    private static void prettyCold(
            HolderLookup.Provider lookup,
            Consumer<AdvancementHolder> consumer
    ) {
        Advancement.Builder builder = Advancement.Builder.advancement()
                .display(
                        Blocks.BLUE_ICE,
                        Component.literal("Pretty Cold"),
                        Component.literal("Travel 10000 Blocks on Ice and got to the biomes Ice Spikes, Snowy Slopes, Snowy Taiga and Snowy Plains"),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                );

        for (ResourceKey<Biome> biome : BIOMES) {
            builder.addCriterion(
                    biome.identifier().getPath(),
                    AdvancementUtil.isInBiome(lookup, biome)
            );
        }

        builder

                .addCriterion(
                        "travel_on_ice",
                        TriggerRegistry.TRAVELED_ON_BLOCK.createCriterion(
                                TraveledOnBlockTrigger.Conditions.of(
                                        Blocks.ICE,
                                        10000.0
                                )
                        )
                )

                .rewards(
                        AdvancementUtil.reward("frost_core")
                )
                .save(
                        consumer,
                        AdvancementUtil.advancementId("core/frost_core")
                );


    }
}
