package dev.jaronline.cuttingdelight.common.network;

import dev.jaronline.cuttingdelight.common.server.ServerPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public final class CutPacket implements Packet<ServerPacketListener> {
	private final int containerId;
	private final ResourceLocation recipe;

	public CutPacket(int containerId, Recipe<?> recipe) {
		this.containerId = containerId;
		this.recipe = recipe.getId();
	}

	public CutPacket(FriendlyByteBuf friendlyByteBuf) {
		this.containerId = friendlyByteBuf.readByte();
		this.recipe = friendlyByteBuf.readResourceLocation();
	}

	public void write(FriendlyByteBuf friendlyByteBuf) {
		friendlyByteBuf.writeByte(this.containerId);
		friendlyByteBuf.writeResourceLocation(this.recipe);
	}

	public void handle(ServerPacketListener handler) {
		handler.handleCut(this);
	}

	public int getContainerId() {
		return containerId;
	}

	public ResourceLocation getRecipe() {
		return recipe;
	}
}
