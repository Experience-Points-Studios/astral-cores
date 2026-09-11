package de.ep.astralcores.advancement.advancements.cores;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.Core;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class IllusionCoreAdvancement {

    public static void generate(

            HolderLookup.Provider lookup,
            Consumer<AdvancementHolder> consumer
    ) {
        s(lookup, consumer);
    }

    private static void s(
            HolderLookup.Provider lookup,
            Consumer<AdvancementHolder> consumer
    ) {
        Advancement.Builder.advancement()
                .display(
                        Items.AMETHYST_SHARD,
                        Component.literal(""),
                        Component.literal(""),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                );
    }
}
