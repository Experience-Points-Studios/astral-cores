package de.ep.astralcores.mixin;

import de.ep.astralcores.config.ConfigManager;
import de.ep.astralcores.core.CoreFactory;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFrame.class)
public class ItemFrameMixin {

    // Intercepts the player interaction completely to prevent item frame consumption
    @Inject(
            method = "interact",
            at = @At("HEAD"),
            cancellable = true
    )
    private void astralcores$preventCoreItemFrameInteract(Player player, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir) {
        // Get the item currently held by the player during this interaction
        ItemStack heldItem = player.getItemInHand(hand);

        if (ConfigManager.get().general.only_allow_inventory
                && CoreFactory.isOrContainsCore(heldItem)) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
