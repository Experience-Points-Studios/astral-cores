package de.ep.astralcores.mixin;

import de.ep.astralcores.advancement.trigger.TriggerRegistry;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CompassItem.class)
public class CompassItemMixin {

    @WrapOperation(
            method = "useOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;set(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private <T> T astralcores$compassTrigger(
            ItemStack stack,
            DataComponentType<T> type,
            T value,
            Operation<T> original,
            UseOnContext context
    ) {
        T result = original.call(stack, type, value);

        if (type == DataComponents.LODESTONE_TRACKER
                && context.getPlayer() instanceof ServerPlayer player
                && value instanceof LodestoneTracker) {

            TriggerRegistry.COMPASS.trigger(player);
        }

        return result;
    }
}