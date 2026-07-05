package dev.jaronline.cuttingdelight.forge.adapter;

import dev.jaronline.cuttingdelight.common.event.RightClickBlockEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.function.Consumer;

public class ForgeRightClickBlockEvent implements RightClickBlockEvent {
	private final PlayerInteractEvent.RightClickBlock event;

	public ForgeRightClickBlockEvent(PlayerInteractEvent.RightClickBlock event) {
		this.event = event;
	}

	public static Consumer<PlayerInteractEvent.RightClickBlock> withMethod(Consumer<RightClickBlockEvent> method) {
		return event -> method.accept(new ForgeRightClickBlockEvent(event));
	}

	@Override
	public Level getLevel() {
		return event.getLevel();
	}

	@Override
	public BlockPos getPos() {
		return event.getPos();
	}

	@Override
	public Player getEntity() {
		return event.getEntity();
	}

	@Override
	public void setCanceled(boolean canceled) {
		event.setCanceled(canceled);
	}

	@Override
	public void setCancellationResult(InteractionResult result) {
		event.setCancellationResult(result);
	}
}
