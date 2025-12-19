package dev.jaronline.cuttingdelight.forge.world;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ItemHandlerContainer implements Container {
	private final IItemHandler itemHandler;

	public ItemHandlerContainer(IItemHandler itemHandler) {
		this.itemHandler = itemHandler;
	}

	@Override
	public int getContainerSize() {
		return itemHandler.getSlots();
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			if (!itemHandler.getStackInSlot(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getItem(int pSlot) {
		return itemHandler.getStackInSlot(pSlot);
	}

	@Override
	public ItemStack removeItem(int pSlot, int pAmount) {
		return itemHandler.extractItem(pSlot, pAmount, false);
	}

	@Override
	public ItemStack removeItemNoUpdate(int pSlot) {
		return itemHandler.extractItem(pSlot, itemHandler.getSlotLimit(pSlot), false);
	}

	@Override
	public void setItem(int pSlot, ItemStack pStack) {
		if (!(itemHandler instanceof IItemHandlerModifiable modifiable)) {
			throw new UnsupportedOperationException("Item handler is not modifiable");
		}
		modifiable.setStackInSlot(pSlot, pStack);
	}

	@Override
	public void setChanged() {

	}

	@Override
	public boolean stillValid(Player pPlayer) {
		return true;
	}

	@Override
	public void clearContent() {
		if (!(itemHandler instanceof IItemHandlerModifiable modifiable)) {
			throw new UnsupportedOperationException("Item handler is not modifiable");
		}
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			modifiable.setStackInSlot(i, ItemStack.EMPTY);
		}
	}
}
