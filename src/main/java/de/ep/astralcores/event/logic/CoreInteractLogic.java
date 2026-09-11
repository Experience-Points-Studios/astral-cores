package de.ep.astralcores.event.logic;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.actionbar.ActionBarManager;
import de.ep.astralcores.core.CoreFactory;
import de.ep.astralcores.core.CoreRegistry;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.core.Core;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

public class CoreInteractLogic {

    // Equips a custom core into the player profile slot when right-clicked
    public static InteractionResult executeEquip(ServerPlayer player, ItemStack stack, Core core, InteractionHand hand) {
        PlayerData data = AstralCores.PLAYER_DATA.get(player);
        if (data == null) {
            return InteractionResult.FAIL;
        }

        // Stops equipment if the single profile slot is already full
        if (data.getEquippedCore() != null) {

            Core equippedCore = CoreRegistry.get(data.getEquippedCore());

            // Cleans up passive buffs or modifiers before the core gets unequipped
            equippedCore.onRemoved(player);

            // Generates the physical item stack and unique UUID for the requested core
            ItemStack coreStack = CoreFactory.createStack(equippedCore);

            // Clears the equipped core reference from the player profile data
            data.setEquippedCore(null);

            // Updates the action bar display text immediately
            ActionBarManager.tick(player, data);

            // Adds the core item to the inventory or drops it on the ground if full
            if (!player.getInventory().add(coreStack)) {
                player.drop(coreStack, false);
            }
        }

        // Binds the core enum type to the player data profile
        data.setEquippedCore(core.getType());
        player.sendSystemMessage(Component.literal("Successfully bound ")
                .append(core.getName())
                .append(" to your profile slot.")
                .withStyle(ChatFormatting.GREEN));

        // Updates the action bar display text immediately
        ActionBarManager.tick(player, data);

        // Reduces the item stack count by one if not in creative mode
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}
