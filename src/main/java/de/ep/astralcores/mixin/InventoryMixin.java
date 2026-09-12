package de.ep.astralcores.mixin;

import de.ep.astralcores.manager.TriggerManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public class InventoryMixin {

    @Shadow @Final public Player player;

    @Inject(method = "setChanged", at = @At("TAIL"))
    private void astralcores$onInventoryChanged(CallbackInfo ci) {
        if (this.player instanceof ServerPlayer serverPlayer) {

            TriggerManager.onInventoryChange(serverPlayer);
        }
    }
}
