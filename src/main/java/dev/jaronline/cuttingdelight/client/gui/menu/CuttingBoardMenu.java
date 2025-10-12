package dev.jaronline.cuttingdelight.client.gui.menu;

import com.google.common.collect.Lists;
import dev.jaronline.cuttingdelight.common.block.entity.CustomCuttingBoardBlockEntity;
import dev.jaronline.cuttingdelight.common.registry.BlockRegistry;
import dev.jaronline.cuttingdelight.common.registry.MenuTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipeInput;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class CuttingBoardMenu extends AbstractContainerMenu {
    public static final int INPUT_SLOT = 0;
    public static final int RESULT_SLOT = 1;
    protected final ContainerLevelAccess access;
    protected final DataSlot selectedRecipeIndex;
    protected final Level level;
    private List<RecipeHolder<CuttingBoardRecipe>> recipes;
    private ItemStack input;
    final Slot inputSlot;
    protected final Slot resultSlot;
    Runnable slotUpdateListener;
    Runnable selectedRecipeUpdateListener;
    public final Container container;
    final ResultContainer resultContainer;
    private final BlockRegistry blocks = BlockRegistry.getInstance();
    private final MenuTypeRegistry menuTypes = MenuTypeRegistry.getInstance();
    private final ItemStack usedTool;
    private final BlockPos blockPos;

    public CuttingBoardMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public CuttingBoardMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
        super(MenuTypeRegistry.getInstance().cuttingBoardMenu.get(), containerId);
        if (!(blockEntity instanceof CustomCuttingBoardBlockEntity cuttingBoardEntity)) {
            throw new IllegalArgumentException("BlockEntity must be instance of CustomCuttingBoardBlockEntity");
        }

        this.usedTool = playerInventory.player.getMainHandItem();
        this.blockPos = blockEntity.getBlockPos();
        this.selectedRecipeIndex = DataSlot.standalone();
        this.recipes = Lists.newArrayList();
        this.input = ItemStack.EMPTY;
        this.slotUpdateListener = () -> {};
        this.selectedRecipeUpdateListener = () -> {};
        this.container = new SimpleContainer(1) {
            public void setChanged() {
                super.setChanged();
                CuttingBoardMenu.this.slotsChanged(this);
                CuttingBoardMenu.this.slotUpdateListener.run();
            }
        };
        this.resultContainer = new ResultContainer();
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.level = playerInventory.player.level();
        this.inputSlot = this.addSlot(new Slot(this.container, INPUT_SLOT, 20, 33) {
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public boolean mayPickup(Player player) {
                return false;
            }
        });

        this.resultSlot = this.addSlot(new Slot(this.resultContainer, RESULT_SLOT, 143, 21) {
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public boolean mayPickup(Player player) {
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

        this.inputSlot.set(cuttingBoardEntity.getStoredItem());
    }

    public int getSelectedRecipeIndex() {
        return this.selectedRecipeIndex.get();
    }

    public CuttingBoardRecipe getSelectedRecipe() {
        if (this.isValidRecipeIndex(this.selectedRecipeIndex.get())) {
            return this.recipes.get(this.selectedRecipeIndex.get()).value();
        }
        return null;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public List<RecipeHolder<CuttingBoardRecipe>> getRecipes() {
        return this.recipes;
    }

    public int getNumRecipes() {
        return this.recipes.size();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, blocks.cuttingBoard.get());
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (this.isValidRecipeIndex(id)) {
            this.selectedRecipeIndex.set(id);
            this.selectedRecipeUpdateListener.run();
            this.setupResultSlot();
        }

        return true;
    }

    @Override
    public void slotsChanged(Container inventory) {
        ItemStack itemstack = this.inputSlot.getItem();
        if (!itemstack.is(this.input.getItem())) {
            this.input = itemstack.copy();
            this.setupRecipeList(inventory, itemstack);
        }
    }

    @Override
    public MenuType<?> getType() {
        return menuTypes.cuttingBoardMenu.get();
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.resultContainer.removeItemNoUpdate(RESULT_SLOT);
        this.access.execute((level, pos) -> this.clearContainer(player, this.container));
    }

    public boolean hasInputItem() {
        return this.inputSlot.hasItem() && !this.recipes.isEmpty();
    }

    public void registerUpdateListener(Runnable listener) {
        this.slotUpdateListener = listener;
    }

    public void registerSelectedRecipeUpdateListener(Runnable listener) {
        this.selectedRecipeUpdateListener = listener;
    }

    @Override
    protected void clearContainer(Player player, Container container) {
        for (int i = 0; i < container.getContainerSize(); ++i) {
            container.removeItemNoUpdate(i);
        }
    }

    private boolean isValidRecipeIndex(int recipeIndex) {
        return recipeIndex >= 0 && recipeIndex < this.recipes.size();
    }

    private void setupRecipeList(Container container, ItemStack stack) {
        this.recipes.clear();
        this.selectedRecipeIndex.set(-1);
        this.resultSlot.set(ItemStack.EMPTY);
        if (!stack.isEmpty()) {
            this.recipes = this.level.getRecipeManager().getRecipesFor(ModRecipeTypes.CUTTING.get(), createRecipeInput(container), this.level);
        }
    }

    private CuttingBoardRecipeInput createRecipeInput(Container container) {
        return new CuttingBoardRecipeInput(container.getItem(0), this.usedTool);
    }

    private void setupResultSlot() {
        if (!this.recipes.isEmpty() && this.isValidRecipeIndex(this.selectedRecipeIndex.get())) {
            RecipeHolder<CuttingBoardRecipe> recipeholder = this.recipes.get(this.selectedRecipeIndex.get());
            ItemStack itemstack = recipeholder.value().assemble(createRecipeInput(this.container), this.level.registryAccess());
            if (itemstack.isItemEnabled(this.level.enabledFeatures())) {
                this.resultContainer.setRecipeUsed(recipeholder);
                this.resultSlot.set(itemstack);
            } else {
                this.resultSlot.set(ItemStack.EMPTY);
            }
        } else {
            this.resultSlot.set(ItemStack.EMPTY);
        }

        this.broadcastChanges();
    }
}
