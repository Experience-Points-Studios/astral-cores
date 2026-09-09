package de.ep.astralcores.event;

import de.ep.astralcores.advancement.trigger.TriggerRegistry;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.server.level.ServerPlayer;

public class EntityCombatEventsListener {
    public static void register() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((level, entity, killedEntity, damageSource) -> {
            if (entity instanceof ServerPlayer attacker) {
                TriggerRegistry.ENTITY_KILLED.trigger(
                        attacker,
                        killedEntity.getType()
                );
            }

                }
        );
    }
}
