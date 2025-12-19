package dev.jaronline.cuttingdelight.common.server;

import com.mojang.logging.LogUtils;
import dev.jaronline.cuttingdelight.common.client.gui.menu.CuttingBoardMenu;
import dev.jaronline.cuttingdelight.common.network.CutPacket;
import dev.jaronline.cuttingdelight.common.network.PacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Recipe;
import org.slf4j.Logger;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

import java.util.IdentityHashMap;
import java.util.Map;

public class ServerPacketListener implements PacketListener {
	private static final Map<ServerPlayer, ServerPacketListener> LISTENERS = new IdentityHashMap<>();
	private static final Logger LOGGER = LogUtils.getLogger();
	private final ServerPlayer player;

	public static ServerPacketListener getListenerFor(ServerPlayer player) {
		return LISTENERS.computeIfAbsent(player, ServerPacketListener::new);
	}

	private ServerPacketListener(ServerPlayer player) {
		this.player = player;
	}

	public void handleCut(final CutPacket packet) {
		Recipe<?> recipe = player.serverLevel().getRecipeManager().byKey(packet.getRecipe()).orElseThrow();
		if (!(recipe instanceof CuttingBoardRecipe cuttingBoardRecipe)) {
			throw new IllegalArgumentException("Expected CuttingBoardRecipe but found: " + recipe.getClass().getSimpleName());
		}
		AbstractContainerMenu containerMenu = player.containerMenu;
		if (!(containerMenu.containerId == packet.getContainerId() && containerMenu instanceof CuttingBoardMenu cuttingBoardMenu)) {
			noCuttingBoardMenuWarning();
			return;
		}
		cuttingBoardMenu.clickCutButton(player, cuttingBoardRecipe);
	}

	private void noCuttingBoardMenuWarning() {
		LOGGER.warn("Player {} is not viewing CuttingBoardMenu while cutting on Cutting Board", player.getName());
	}
}
