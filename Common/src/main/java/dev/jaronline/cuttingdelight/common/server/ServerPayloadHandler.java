package dev.jaronline.cuttingdelight.common.server;

import com.mojang.logging.LogUtils;
import dev.jaronline.cuttingdelight.common.client.gui.menu.CuttingBoardMenu;
import dev.jaronline.cuttingdelight.common.network.CutPayload;
import dev.jaronline.cuttingdelight.common.network.CuttingBoardFilledPayload;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

public class ServerPayloadHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void handleCut(final CutPayload payload, Player player) {
        if (!(payload.recipe() instanceof CuttingBoardRecipe cuttingBoardRecipe)) {
            throw new IllegalArgumentException("Expected CuttingBoardRecipe but found: " + payload.recipe().getClass().getSimpleName());
        }
        if (!(player.containerMenu instanceof CuttingBoardMenu cuttingBoardMenu)) {
            noCuttingBoardMenuWarning(player);
            return;
        }

        cuttingBoardMenu.clickCutButton(player, cuttingBoardRecipe);
    }

    public static void handleCuttingBoardFilled(final CuttingBoardFilledPayload payload, Player player) {
        if (!(player.containerMenu instanceof CuttingBoardMenu cuttingBoardMenu)) {
            noCuttingBoardMenuWarning(player);
            return;
        }

        cuttingBoardMenu.setInputSlot(payload.itemStack());
    }

    private static void noCuttingBoardMenuWarning(Player player) {
        LOGGER.warn("Player {} is not viewing CuttingBoardMenu while cutting on Cutting Board", player.getName());
    }
}
