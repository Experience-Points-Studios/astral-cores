package de.ep.astralcores.advancement.trigger;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.advancement.trigger.triggers.*;
import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;


public class TriggerRegistry {

    public static final VoidSurvivalTrigger VOID_SURVIVAL =
            register("void_survival", new VoidSurvivalTrigger());

    public static final NetherTimeTrigger NETHER_TIME =
            register("nether_time", new NetherTimeTrigger());

    public static final HasItemCountTrigger HAS_ITEM_COUNT =
            register("has_item_count", new HasItemCountTrigger());

    public static final EntityKilledTrigger ENTITY_KILLED =
            register("entity_killed", new EntityKilledTrigger());

    public static final CompassTrigger COMPASS =
            register("compass", new CompassTrigger());

    public static final TraveledOnBlockTrigger TRAVELED_ON_BLOCK =
            register("traveled_on_block", new TraveledOnBlockTrigger());


    private static <T extends CriterionTrigger<?>> T register(
            String name,
            T criterion
    ) {
        return Registry.register(
                BuiltInRegistries.TRIGGER_TYPES,
                Identifier.fromNamespaceAndPath(
                        AstralCores.MOD_ID,
                        name
                ),
                criterion
        );
    }


    public static void init() {
        // Triggers are registered through the static fields above.
    }
}