package de.ep.astralcores.advancement.advancements.cores;

import de.ep.astralcores.advancement.trigger.triggers.CompassTrigger;
import de.ep.astralcores.util.AdvancementUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class MagnetCoreAdvancement {

    public static void generate(
            Consumer<AdvancementHolder> consumer
    ) {
        magneticField(consumer);
    }

    private static void magneticField(
            Consumer<AdvancementHolder> consumer
    ) {
        Advancement.Builder.advancement()
                .display(
                        Items.COMPASS,
                        Component.literal(
                                "Magnetic Field"
                        ),
                        Component.literal(
                                "Have 9 Lodestone Compasses linked to different Lodestones and 3 Recovery Compasses"
                        ),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "compasses",
                        CompassTrigger.TriggerInstance.create(
                                Items.COMPASS,
                                9,
                                Items.RECOVERY_COMPASS,
                                3
                        )
                )
                .rewards(
                        AdvancementUtil.reward(
                                "magnet_core"
                        )
                )
                .save(
                        consumer,
                        AdvancementUtil.advancementId(
                                "core/magnet_core"
                        )
                );
    }
}