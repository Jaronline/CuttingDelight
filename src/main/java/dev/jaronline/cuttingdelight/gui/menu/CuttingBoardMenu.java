package dev.jaronline.cuttingdelight.gui.menu;

import com.google.common.collect.Lists;
import dev.jaronline.cuttingdelight.block.entity.CustomCuttingBoardBlockEntity;
import dev.jaronline.cuttingdelight.registry.BlockRegistry;
import dev.jaronline.cuttingdelight.registry.MenuTypeRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipeInput;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CuttingBoardMenu extends AbstractContainerMenu {
    public static final int INPUT_SLOT = 0;
    public static final int RESULT_SLOT = 1;
    private static final int INV_SLOT_START = 2;
    private static final int INV_SLOT_END = 29;
    private static final int USE_ROW_SLOT_START = 29;
    private static final int USE_ROW_SLOT_END = 38;
    protected final ContainerLevelAccess access;
    protected final DataSlot selectedRecipeIndex;
    protected final Level level;
    private List<RecipeHolder<CuttingBoardRecipe>> recipes;
    private ItemStack input;
    long lastSoundTime;
    final Slot inputSlot;
    protected final Slot resultSlot;
    Runnable slotUpdateListener;
    public final Container container;
    final ResultContainer resultContainer;
    private final BlockRegistry blocks = BlockRegistry.getInstance();
    private final MenuTypeRegistry menuTypes = MenuTypeRegistry.getInstance();
    private final ItemStack usedTool;

    public CuttingBoardMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public CuttingBoardMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
        super(MenuTypeRegistry.getInstance().cuttingBoardMenu.get(), containerId);
        if (!(blockEntity instanceof CustomCuttingBoardBlockEntity cuttingBoardEntity)) {
            throw new IllegalArgumentException("BlockEntity must be instance of CustomCuttingBoardBlockEntity");
        }

        this.usedTool = playerInventory.player.getMainHandItem();
        this.selectedRecipeIndex = DataSlot.standalone();
        this.recipes = Lists.newArrayList();
        this.input = ItemStack.EMPTY;
        this.slotUpdateListener = () -> {
        };
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

            //            public void onTake(Player p_150672_, ItemStack p_150673_) {
//                p_150673_.onCraftedBy(p_150672_.level(), p_150672_, p_150673_.getCount());
//                CuttingBoardMenu.this.resultContainer.awardUsedRecipes(p_150672_, this.getRelevantItems());
//                ItemStack itemstack = CuttingBoardMenu.this.inputSlot.remove(1);
//                if (!itemstack.isEmpty()) {
//                    CuttingBoardMenu.this.setupResultSlot();
//                }
//
//                access.execute((p_40364_, p_40365_) -> {
//                    long l = p_40364_.getGameTime();
//                    if (CuttingBoardMenu.this.lastSoundTime != l) {
//                        p_40364_.playSound((Player)null, p_40365_, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
//                        CuttingBoardMenu.this.lastSoundTime = l;
//                    }
//
//                });
//                super.onTake(p_150672_, p_150673_);
//            }

            private List<ItemStack> getRelevantItems() {
                return List.of(CuttingBoardMenu.this.inputSlot.getItem());
            }
        });

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }

        this.addDataSlot(this.selectedRecipeIndex);

        this.inputSlot.set(cuttingBoardEntity.getStoredItem());
    }

    public int getSelectedRecipeIndex() {
        return this.selectedRecipeIndex.get();
    }

    public List<RecipeHolder<CuttingBoardRecipe>> getRecipes() {
        return this.recipes;
    }

    public int getNumRecipes() {
        return this.recipes.size();
    }

    public boolean hasInputItem() {
        return this.inputSlot.hasItem() && !this.recipes.isEmpty();
    }

    public boolean stillValid(Player player) {
        return stillValid(this.access, player, blocks.cuttingBoard.get());
    }

    public boolean clickMenuButton(Player player, int id) {
        if (this.isValidRecipeIndex(id)) {
            this.selectedRecipeIndex.set(id);
            this.setupResultSlot();
        }

        return true;
    }

    private boolean isValidRecipeIndex(int recipeIndex) {
        return recipeIndex >= 0 && recipeIndex < this.recipes.size();
    }

    public void slotsChanged(Container inventory) {
        ItemStack itemstack = this.inputSlot.getItem();
        if (!itemstack.is(this.input.getItem())) {
            this.input = itemstack.copy();
            this.setupRecipeList(inventory, itemstack);
        }

    }

    private CuttingBoardRecipeInput createRecipeInput(Container container) {
        return new CuttingBoardRecipeInput(container.getItem(0), this.usedTool);
    }

    protected void setupRecipeList(Container container, ItemStack stack) {
        this.recipes.clear();
        this.selectedRecipeIndex.set(-1);
        this.resultSlot.set(ItemStack.EMPTY);
        if (!stack.isEmpty()) {
            this.recipes = this.level.getRecipeManager().getRecipesFor(ModRecipeTypes.CUTTING.get(), createRecipeInput(container), this.level);
        }

    }

    void setupResultSlot() {
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

    public MenuType<?> getType() {
        return menuTypes.cuttingBoardMenu.get();
    }

    public void registerUpdateListener(Runnable listener) {
        this.slotUpdateListener = listener;
    }

    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.resultContainer && super.canTakeItemForPickAll(stack, slot);
    }

    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            Item item = itemstack1.getItem();
            itemstack = itemstack1.copy();
            if (index == 1) {
                item.onCraftedBy(itemstack1, player.level(), player);
                if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, INV_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index == 0) {
                if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, INV_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.level.getRecipeManager().getRecipeFor(RecipeType.STONECUTTING, new SingleRecipeInput(itemstack1), this.level).isPresent()) {
                if (!this.moveItemStackTo(itemstack1, INPUT_SLOT, RESULT_SLOT, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= INV_SLOT_START && index < INV_SLOT_END) {
                if (!this.moveItemStackTo(itemstack1, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= USE_ROW_SLOT_START && index < USE_ROW_SLOT_END && !this.moveItemStackTo(itemstack1, INV_SLOT_START, INV_SLOT_END, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            }

            slot.setChanged();
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
            this.broadcastChanges();
        }

        return itemstack;
    }

    public void removed(Player player) {
        super.removed(player);
        this.resultContainer.removeItemNoUpdate(RESULT_SLOT);
        this.access.execute((p_40313_, p_40314_) -> this.clearContainer(player, this.container));
    }
}
