package dev.jaronline.cuttingdelight.common.client.gui.menu;

import com.google.common.reflect.TypeToken;
import dev.jaronline.cuttingdelight.common.ModBlocks;
import dev.jaronline.cuttingdelight.common.ModMenuTypes;
import dev.jaronline.cuttingdelight.common.block.entity.CustomCuttingBoardBlockEntity;
import dev.jaronline.cuttingdelight.common.config.ConfigManager;
import dev.jaronline.cuttingdelight.common.provider.ProviderManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CuttingBoardMenu extends AbstractContainerMenu {
	public static final int INPUT_SLOT = 0;
	public static final int RESULT_SLOT = 1;
	protected final ContainerLevelAccess access;
	protected final DataSlot selectedRecipeIndex;
	protected final Level level;
	private List<CuttingBoardRecipe> recipes;
	private ItemStack input;
	final Slot inputSlot;
	protected final Slot resultSlot;
	Runnable slotUpdateListener;
	Runnable selectedRecipeUpdateListener;
	public final Container container;
	final ResultContainer resultContainer;
	private final ItemStack usedTool;

	public CuttingBoardMenu(int containerId, Inventory playerInventory) {
		this(containerId, playerInventory, new SimpleContainer(1), ContainerLevelAccess.NULL);
	}

	public CuttingBoardMenu(int containerId, Inventory playerInventory, Container container, ContainerLevelAccess access) {
		super(ModMenuTypes.CUTTING_BOARD_MENU, containerId);

		this.usedTool = playerInventory.player.getMainHandItem();
		this.selectedRecipeIndex = DataSlot.standalone();
		this.selectedRecipeIndex.set(-1);
		this.recipes = new ArrayList<>();
		this.input = ItemStack.EMPTY;
		this.slotUpdateListener = () -> {
		};
		this.selectedRecipeUpdateListener = () -> {
		};
		this.container = container;
		this.resultContainer = new ResultContainer();
		this.access = access;
		this.level = playerInventory.player.level();
		this.inputSlot = this.addSlot(new Slot(this.container, INPUT_SLOT, 20, 33) {
			@Override
			public void setChanged() {
				super.setChanged();
				CuttingBoardMenu.this.slotsChanged(CuttingBoardMenu.this.container);
				CuttingBoardMenu.this.slotUpdateListener.run();
			}
		});

		this.resultSlot = this.addSlot(new Slot(this.resultContainer, RESULT_SLOT, 143, 21) {
			@Override
			public boolean mayPlace(ItemStack $$0) {
				return false;
			}

			@Override
			public boolean mayPickup(Player $$0) {
				return false;
			}
		});

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int k = 0; k < 9; ++k) {
			this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
		}

		this.addDataSlot(this.selectedRecipeIndex);
	}

	public int getSelectedRecipeIndex() {
		return this.selectedRecipeIndex.get();
	}

	public CuttingBoardRecipe getSelectedRecipe() {
		if (this.isValidSlotIndex(this.selectedRecipeIndex.get())) {
			return this.recipes.get(this.selectedRecipeIndex.get());
		}
		return null;
	}

	public List<CuttingBoardRecipe> getRecipes() {
		return this.recipes;
	}

	public int getNumRecipes() {
		return this.recipes.size();
	}

	@Override
	public boolean stillValid(Player player) {
		return stillValid(this.access, player, ModBlocks.CUTTING_BOARD);
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		if (this.isValidSlotIndex(id)) {
			this.selectedRecipeIndex.set(id);
			this.selectedRecipeUpdateListener.run();
			this.setupResultSlot();
		}

		return true;
	}

	@Override
	public void slotsChanged(Container inventory) {
		ItemStack itemStack = this.inputSlot.getItem();
		if (!itemStack.is(this.input.getItem())) {
			this.input = itemStack.copy();
			this.setupRecipeList(inventory, itemStack);
		}
		this.setupResultSlot();
	}

	@Override
	public MenuType<?> getType() {
		return ModMenuTypes.CUTTING_BOARD_MENU;
	}

	@Override
	public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
		return false;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int i) {
		return ItemStack.EMPTY;
	}

	public boolean hasInputItem() {
		return this.inputSlot.hasItem() && !this.recipes.isEmpty();
	}

	public boolean hasSingleInputItem() {
		return this.inputSlot.getItem().getCount() <= 1;
	}

	public void registerUpdateListener(Runnable listener) {
		this.slotUpdateListener = listener;
	}

	public void registerSelectedRecipeUpdateListener(Runnable listener) {
		this.selectedRecipeUpdateListener = listener;
	}

	public void clickCutButton(Player player, CuttingBoardRecipe recipe) {
		this.access.execute((level, pos) -> {
			CustomCuttingBoardBlockEntity blockEntity = getCuttingBoardBlockEntity(level, pos);
			blockEntity.processStoredStackOrItemUsingTool(recipe, player.getMainHandItem(), player);
			this.inputSlot.set(blockEntity.getStoredItem());
		});
	}

	private CustomCuttingBoardBlockEntity getCuttingBoardBlockEntity(Level level, BlockPos pos) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (!(blockEntity instanceof CustomCuttingBoardBlockEntity cuttingBoardBlockEntity)) {
			throw new IllegalArgumentException("Expected " + CustomCuttingBoardBlockEntity.class.getSimpleName() + " but found: " + blockEntity.getClass().getSimpleName());
		}
		return cuttingBoardBlockEntity;
	}

	public boolean isValidRecipeIndex(int recipeIndex) {
		return recipeIndex >= 0 && recipeIndex < this.recipes.size();
	}

	private void setupRecipeList(Container container, ItemStack stack) {
		this.recipes.clear();
		this.selectedRecipeIndex.set(-1);
		this.resultSlot.set(ItemStack.EMPTY);
		if (!stack.isEmpty()) {
			RecipeType<CuttingBoardRecipe> cuttingBoardRecipeRecipeType =
					ProviderManager.getObjectProvider(new TypeToken<RecipeType<CuttingBoardRecipe>>() {
					}).getObject();
			this.recipes = ProviderManager.getRecipeProvider().getRecipesFor(cuttingBoardRecipeRecipeType, container, this.level);
			this.recipes = this.recipes.stream().filter(cuttingRecipe -> cuttingRecipe.getTool().test(this.usedTool)).collect(Collectors.toList());
		}
	}

	private void setupResultSlot() {
		if (!this.recipes.isEmpty() && this.isValidRecipeIndex(this.selectedRecipeIndex.get())) {
			CuttingBoardRecipe recipe = this.recipes.get(this.selectedRecipeIndex.get());
			ItemStack itemStack = ProviderManager.getRecipeProvider().assemble(recipe, this.container, this.level.registryAccess());
			if (ConfigManager.getConfig().shouldProcessStack()) {
				itemStack.setCount(itemStack.getCount() * this.inputSlot.getItem().getCount());
			}
			if (itemStack.isItemEnabled(this.level.enabledFeatures())) {
				this.resultContainer.setRecipeUsed(recipe);
				this.resultSlot.set(itemStack);
			} else {
				this.resultSlot.set(ItemStack.EMPTY);
			}
		} else {
			this.resultSlot.set(ItemStack.EMPTY);
		}

		this.broadcastChanges();
	}
}
