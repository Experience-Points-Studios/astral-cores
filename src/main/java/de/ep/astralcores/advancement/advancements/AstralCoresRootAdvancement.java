package de.ep.astralcores.advancement.advancements;

import de.ep.astralcores.util.AdvancementUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.triggers.PlayerTrigger;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;

import java.util.Optional;
import java.util.function.Consumer;

public final class AstralCoresRootAdvancement {

    public static AdvancementHolder generate(
            Consumer<AdvancementHolder> consumer
    ) {
        return Advancement.Builder.advancement()
                .display(
                        new DisplayInfo(
                                new ItemStackTemplate(Items.NETHER_STAR),
                                Component.literal("Astral Cores"),
                                Component.literal("Discover the Astral Cores."),
                                Optional.of(
                                        new ClientAsset.ResourceTexture(
                                                Identifier.withDefaultNamespace(
                                                        "gui/advancements/backgrounds/stone"
                                                )
                                        )
                                ),
                                AdvancementType.TASK,
                                false,
                                false,
                                false
                        )
                )
                .addCriterion(
                        "root",
                        PlayerTrigger.TriggerInstance.tick()
                )
                .save(
                        consumer,
                        AdvancementUtil.advancementId("root")
                );
    }
}