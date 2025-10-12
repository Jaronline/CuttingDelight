package dev.jaronline.cuttingdelight.client.gui.screen;

import dev.jaronline.cuttingdelight.CuttingDelight;
import dev.jaronline.cuttingdelight.client.gui.menu.CuttingBoardMenu;
import dev.jaronline.cuttingdelight.network.CutPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class CuttingBoardScreen extends AbstractContainerScreen<CuttingBoardMenu> {
    private static final ResourceLocation BG_LOCATION = ResourceLocation.fromNamespaceAndPath(CuttingDelight.MOD_ID, "textures/gui/container/cutting_board.png");

    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/stonecutter/scroller");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/stonecutter/scroller_disabled");
    private static final ResourceLocation RECIPE_SELECTED_SPRITE = ResourceLocation.withDefaultNamespace("container/stonecutter/recipe_selected");
    private static final ResourceLocation RECIPE_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("container/stonecutter/recipe_highlighted");
    private static final ResourceLocation RECIPE_SPRITE = ResourceLocation.withDefaultNamespace("container/stonecutter/recipe");

    private static final ResourceLocation BUTTON_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/beacon/button_disabled");
    private static final ResourceLocation BUTTON_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("container/beacon/button_highlighted");
    private static final ResourceLocation BUTTON_SELECTED_SPRITE = ResourceLocation.withDefaultNamespace("container/beacon/button_selected");
    private static final ResourceLocation BUTTON_SPRITE = ResourceLocation.withDefaultNamespace("container/beacon/button");
    private static final ResourceLocation CONFIRM_SPRITE = ResourceLocation.withDefaultNamespace("container/beacon/confirm");

    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    private static final int RECIPES_COLUMNS = 4;
    private static final int RECIPES_ROWS = 3;
    private static final int RECIPES_IMAGE_SIZE_WIDTH = 16;
    private static final int RECIPES_IMAGE_SIZE_HEIGHT = 18;
    private static final int SCROLLER_FULL_HEIGHT = 54;
    private static final int RECIPES_X = 52;
    private static final int RECIPES_Y = 14;

    private float scrollOffs;
    private boolean scrolling;
    private int startIndex;
    private boolean displayRecipes;
    private CuttingBoardScreenButton confirmButton;

    public CuttingBoardScreen(CuttingBoardMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        menu.registerUpdateListener(this::containerChanged);
        menu.registerSelectedRecipeUpdateListener(this::selectedRecipeChanged);
        --this.titleLabelY;
    }

    @Override
    protected void init() {
        super.init();
        confirmButton = new CuttingBoardConfirmButton(this.leftPos + 140, this.topPos + 46);
        confirmButton.active = false;
        this.addRenderableWidget(confirmButton);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = this.leftPos;
        int j = this.topPos;
        guiGraphics.blit(BG_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
        int k = (int)(41.0F * this.scrollOffs);
        ResourceLocation resourcelocation = this.isScrollBarActive() ? SCROLLER_SPRITE : SCROLLER_DISABLED_SPRITE;
        guiGraphics.blitSprite(resourcelocation, i + 119, j + 15 + k, SCROLLER_WIDTH, SCROLLER_HEIGHT);
        int l = this.leftPos + RECIPES_X;
        int i1 = this.topPos + RECIPES_Y;
        int j1 = this.startIndex + (RECIPES_ROWS * RECIPES_COLUMNS);
        this.renderButtons(guiGraphics, mouseX, mouseY, l, i1, j1);
        this.renderRecipes(guiGraphics, l, i1, j1);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
        if (this.displayRecipes) {
            int i = this.leftPos + RECIPES_X;
            int j = this.topPos + RECIPES_Y;
            int k = this.startIndex + (RECIPES_ROWS * RECIPES_COLUMNS);
            List<RecipeHolder<CuttingBoardRecipe>> list = this.menu.getRecipes();

            for(int l = this.startIndex; l < k && l < this.menu.getNumRecipes(); ++l) {
                int i1 = l - this.startIndex;
                int j1 = i + i1 % 4 * RECIPES_IMAGE_SIZE_WIDTH;
                int k1 = j + i1 / 4 * RECIPES_IMAGE_SIZE_HEIGHT + 2;
                if (x >= j1 && x < j1 + RECIPES_IMAGE_SIZE_WIDTH && y >= k1 && y < k1 + RECIPES_IMAGE_SIZE_HEIGHT) {
                    guiGraphics.renderTooltip(this.font, list.get(l).value().getResultItem(this.minecraft.level.registryAccess()), x, y);
                }
            }
        }

    }

    private void renderButtons(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y, int lastVisibleElementIndex) {
        for(int i = this.startIndex; i < lastVisibleElementIndex && i < this.menu.getNumRecipes(); ++i) {
            int j = i - this.startIndex;
            int k = x + j % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH;
            int l = j / RECIPES_COLUMNS;
            int i1 = y + l * RECIPES_IMAGE_SIZE_HEIGHT + 2;
            ResourceLocation resourcelocation;
            if (i == this.menu.getSelectedRecipeIndex()) {
                resourcelocation = RECIPE_SELECTED_SPRITE;
            } else if (mouseX >= k && mouseY >= i1 && mouseX < k + RECIPES_IMAGE_SIZE_WIDTH && mouseY < i1 + RECIPES_IMAGE_SIZE_HEIGHT) {
                resourcelocation = RECIPE_HIGHLIGHTED_SPRITE;
            } else {
                resourcelocation = RECIPE_SPRITE;
            }

            guiGraphics.blitSprite(resourcelocation, k, i1 - 1, RECIPES_IMAGE_SIZE_WIDTH, RECIPES_IMAGE_SIZE_HEIGHT);
        }

    }

    private void renderRecipes(GuiGraphics guiGraphics, int x, int y, int startIndex) {
        List<RecipeHolder<CuttingBoardRecipe>> list = this.menu.getRecipes();

        for(int i = this.startIndex; i < startIndex && i < this.menu.getNumRecipes(); ++i) {
            int j = i - this.startIndex;
            int k = x + j % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH;
            int l = j / RECIPES_COLUMNS;
            int i1 = y + l * RECIPES_IMAGE_SIZE_HEIGHT + 2;
            guiGraphics.renderItem(list.get(i).value().getResultItem(this.minecraft.level.registryAccess()), k, i1);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.scrolling = false;
        if (this.displayRecipes) {
            int i = this.leftPos + RECIPES_X;
            int j = this.topPos + RECIPES_Y;
            int k = this.startIndex + (RECIPES_ROWS * RECIPES_COLUMNS);

            for(int l = this.startIndex; l < k; ++l) {
                int i1 = l - this.startIndex;
                double d0 = mouseX - (double)(i + i1 % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH);
                double d1 = mouseY - (double)(j + i1 / RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_HEIGHT);
                if (d0 >= (double)0.0F && d1 >= (double)0.0F && d0 < (double)RECIPES_IMAGE_SIZE_WIDTH && d1 < (double)RECIPES_IMAGE_SIZE_HEIGHT && this.menu.clickMenuButton(this.minecraft.player, l)) {
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, l);
                    return true;
                }
            }

            i = this.leftPos + 119;
            j = this.topPos + 9;
            if (mouseX >= (double)i && mouseX < (double)(i + (RECIPES_ROWS * RECIPES_COLUMNS)) && mouseY >= (double)j && mouseY < (double)(j + SCROLLER_FULL_HEIGHT)) {
                this.scrolling = true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.scrolling && this.isScrollBarActive()) {
            int i = this.topPos + 14;
            int j = i + SCROLLER_FULL_HEIGHT;
            this.scrollOffs = ((float)mouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
            this.startIndex = (int)((double)(this.scrollOffs * (float)this.getOffscreenRows()) + (double)0.5F) * RECIPES_COLUMNS;
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.isScrollBarActive()) {
            int i = this.getOffscreenRows();
            float f = (float)scrollY / (float)i;
            this.scrollOffs = Mth.clamp(this.scrollOffs - f, 0.0F, 1.0F);
            this.startIndex = (int)((double)(this.scrollOffs * (float)i) + (double)0.5F) * RECIPES_COLUMNS;
        }

        return true;
    }

    private boolean isScrollBarActive() {
        return this.displayRecipes && this.menu.getNumRecipes() > (RECIPES_ROWS * RECIPES_COLUMNS);
    }

    protected int getOffscreenRows() {
        return (this.menu.getNumRecipes() + RECIPES_COLUMNS - 1) / RECIPES_COLUMNS - RECIPES_ROWS;
    }

    private void containerChanged() {
        this.displayRecipes = this.menu.hasInputItem();
        if (!this.displayRecipes) {
            this.scrollOffs = 0.0F;
            this.startIndex = 0;
        }
    }

    private void selectedRecipeChanged() {
        confirmButton.active = true;
    }

    @OnlyIn(Dist.CLIENT)
    class CuttingBoardConfirmButton extends CuttingBoardScreenButton {
        protected CuttingBoardConfirmButton(int x, int y) {
            super(x, y, CuttingBoardScreen.CONFIRM_SPRITE, CommonComponents.GUI_DONE);
        }

        @Override
        public void onPress() {
            BlockPos blockPos = CuttingBoardScreen.this.menu.getBlockPos();
            CuttingBoardRecipe result = CuttingBoardScreen.this.menu.getSelectedRecipe();
            if (result == null) {
                return;
            }
            CuttingBoardScreen.this.minecraft.getConnection().send(new CutPayload(blockPos, result));
            CuttingBoardScreen.this.minecraft.player.closeContainer();
        }
    }

    @OnlyIn(Dist.CLIENT)
    abstract static class CuttingBoardScreenButton extends AbstractButton {
        private boolean selected;
        private final ResourceLocation sprite;

        protected CuttingBoardScreenButton(int x, int y, ResourceLocation sprite, Component message) {
            super(x, y, 22, 22, message);
            this.sprite = sprite;
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            ResourceLocation resourcelocation;
            if (!this.active) {
                resourcelocation = CuttingBoardScreen.BUTTON_DISABLED_SPRITE;
            } else if (this.selected) {
                resourcelocation = CuttingBoardScreen.BUTTON_SELECTED_SPRITE;
            } else if (this.isHoveredOrFocused()) {
                resourcelocation = CuttingBoardScreen.BUTTON_HIGHLIGHTED_SPRITE;
            } else {
                resourcelocation = CuttingBoardScreen.BUTTON_SPRITE;
            }

            guiGraphics.blitSprite(resourcelocation, this.getX(), this.getY(), this.width, this.height);
            this.renderIcon(guiGraphics);
        }

        protected void renderIcon(GuiGraphics guiGraphics) {
            guiGraphics.blitSprite(this.sprite, this.getX() + 2, this.getY() + 2, 18, 18);
        }

        public boolean isSelected() {
            return this.selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
            this.defaultButtonNarrationText(narrationElementOutput);
        }
    }
}
