package de.ep.astralcores.advancement.advancements.cores;

import de.ep.astralcores.util.AdvancementUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

public class GravityCoreAdvancement {

    public static void generate(
            HolderLookup.Provider lookup,
            Consumer<AdvancementHolder> consumer
    ) {
        thatsHeavy(lookup, consumer);
    }

    private static void thatsHeavy(
            HolderLookup.Provider lookup,
            Consumer<AdvancementHolder> consumer
    ) {
        Advancement.Builder.advancement()
                .display(
                        Blocks.ANVIL,
                        Component.literal("That's heavy!"),
                        Component.literal("Get 2 stacks of Anvils"),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "has_128_anvils",
                        AdvancementUtil.hasItemTotal(Blocks.ANVIL.asItem(), 128)
                )
                .rewards(
                        AdvancementUtil.reward("gravity_core")
                )
                .save(
                        consumer,
                        AdvancementUtil.advancementId("core/gravity_core")
                );

    }
}
