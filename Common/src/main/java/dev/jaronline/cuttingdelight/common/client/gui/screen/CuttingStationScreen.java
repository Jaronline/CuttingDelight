package dev.jaronline.cuttingdelight.common.client.gui.screen;

import com.mojang.logging.LogUtils;
import dev.jaronline.cuttingdelight.common.ModIds;
import dev.jaronline.cuttingdelight.common.client.gui.menu.CuttingStationMenu;
import dev.jaronline.cuttingdelight.common.config.ConfigManager;
import dev.jaronline.cuttingdelight.common.network.CutPacket;
import dev.jaronline.cuttingdelight.common.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class CuttingStationScreen extends AbstractContainerScreen<CuttingStationMenu> {
	private static final ResourceLocation BG_LOCATION = ModIds.cuttingDelightResource("textures/gui/container/cutting_station.png");
	private static final ResourceLocation STONECUTTER_SCREEN_LOCATION = new ResourceLocation("textures/gui/container/stonecutter.png");
	private static final ResourceLocation BEACON_SCREEN_LOCATION = new ResourceLocation("textures/gui/container/beacon.png");
	private static final Logger LOGGER = LogUtils.getLogger();

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
	private CuttingStationScreenButton confirmButton;

	public CuttingStationScreen(CuttingStationMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		menu.registerUpdateListener(this::containerChanged);
		menu.registerSelectedRecipeUpdateListener(this::selectedRecipeChanged);
		--this.titleLabelY;
	}

	@Override
	protected void init() {
		super.init();
		confirmButton = new CuttingStationConfirmButton(this.leftPos + 140, this.topPos + 46);
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
		int k = (int) (41.0F * this.scrollOffs);
		guiGraphics.blit(STONECUTTER_SCREEN_LOCATION, i + 119, j + 15 + k, 176 + (this.isScrollBarActive() ? 0 : 12), 0, SCROLLER_WIDTH, SCROLLER_HEIGHT);
		int l = this.leftPos + RECIPES_X;
		int m = this.topPos + RECIPES_Y;
		int n = this.startIndex + RECIPES_ROWS * RECIPES_COLUMNS;
		this.renderButtons(guiGraphics, mouseX, mouseY, l, m, n);
		this.renderRecipes(guiGraphics, l, m, n);
	}

	@Override
	protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
		super.renderTooltip(guiGraphics, x, y);
		if (this.displayRecipes) {
			int i = this.leftPos + RECIPES_X;
			int j = this.topPos + RECIPES_Y;
			int k = this.startIndex + RECIPES_ROWS * RECIPES_COLUMNS;
			List<CuttingBoardRecipe> list = this.menu.getRecipes();

			for (int l = this.startIndex; l < k && l < this.menu.getNumRecipes(); ++l) {
				int i1 = l - this.startIndex;
				int j1 = i + i1 % 4 * RECIPES_IMAGE_SIZE_WIDTH;
				int k1 = j + i1 / 4 * RECIPES_IMAGE_SIZE_HEIGHT + 2;
				if (x >= j1 && x < j1 + RECIPES_IMAGE_SIZE_WIDTH && y >= k1 && y < k1 + RECIPES_IMAGE_SIZE_HEIGHT) {
                    //noinspection unchecked
                    guiGraphics.renderTooltip(this.font, Services.PLATFORM.getRecipeHelper().getResultItem(list.get(l), this.minecraft.level.registryAccess()), x, y);
				}
			}
		}
	}

	private void renderButtons(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y, int lastVisibleElementIndex) {
		for (int i = this.startIndex; i < lastVisibleElementIndex && i < this.menu.getNumRecipes(); ++i) {
			int j = i - this.startIndex;
			int k = x + j % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH;
			int l = j / RECIPES_COLUMNS;
			int i1 = y + l * RECIPES_IMAGE_SIZE_HEIGHT + 2;
			int j1 = this.imageHeight;
			if (i == this.menu.getSelectedRecipeIndex()) {
				j1 += 18;
			} else if (mouseX >= k && mouseY >= i1 && mouseX < k + 16 && mouseY < i1 + 18) {
				j1 += 36;
			}

			guiGraphics.blit(STONECUTTER_SCREEN_LOCATION, k, i1 - 1, 0, j1, 16, 18);
		}
	}

	private void renderRecipes(GuiGraphics guiGraphics, int x, int y, int startIndex) {
		List<CuttingBoardRecipe> list = this.menu.getRecipes();

		for (int i = this.startIndex; i < startIndex && i < this.menu.getNumRecipes(); ++i) {
			int j = i - this.startIndex;
			int k = x + j % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH;
			int l = j / RECIPES_COLUMNS;
			int i1 = y + l * RECIPES_IMAGE_SIZE_HEIGHT + 2;
            //noinspection unchecked
            guiGraphics.renderItem(Services.PLATFORM.getRecipeHelper().getResultItem(list.get(i), this.minecraft.level.registryAccess()), k, i1);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.scrolling = false;
		if (this.displayRecipes) {
			int i = this.leftPos + RECIPES_X;
			int j = this.topPos + RECIPES_Y;
			int k = this.startIndex + RECIPES_ROWS * RECIPES_COLUMNS;

			for  (int l = this.startIndex; l < k; ++l) {
				int i1 = l - this.startIndex;
				double d0 = mouseX - (double) (i + i1 % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH);
				double d1 = mouseY - (double) (j + i1 / RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_HEIGHT);
				if (d0 >= (double) 0.0F && d1 >= (double) 0.0F && d0 < (double) RECIPES_IMAGE_SIZE_WIDTH && d1 < (double) RECIPES_IMAGE_SIZE_HEIGHT && this.menu.clickMenuButton(this.minecraft.player, l)) {
					Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
					this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, l);
					return true;
				}
			}

			i = this.leftPos + 119;
			j = this.topPos + 9;
			if (mouseX >= (double) i && mouseX < (double) (i + SCROLLER_WIDTH) && mouseY >= (double) j && mouseY < (double) (j + SCROLLER_FULL_HEIGHT)) {
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
			this.scrollOffs = ((float) mouseY - (float) i - 7.5F) / ((float) (j - i) - 15.0F);
			this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
			this.startIndex = (int) ((double) (this.scrollOffs * (float) this.getOffscreenRows()) + (double) 0.5F) * RECIPES_COLUMNS;
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	private boolean isScrollBarActive() {
		return this.displayRecipes && this.menu.getNumRecipes() > RECIPES_ROWS * RECIPES_COLUMNS;
	}

	protected int getOffscreenRows() {
		return (this.menu.getNumRecipes() + RECIPES_COLUMNS - 1) / RECIPES_COLUMNS - RECIPES_ROWS;
	}

	private void containerChanged() {
		this.displayRecipes = this.menu.hasInputItem();
		if (!this.displayRecipes) {
			this.scrollOffs = 0.0F;
			this.startIndex = 0;
			confirmButton.active = false;
		}
	}

	private void selectedRecipeChanged() {
		confirmButton.active = true;
		confirmButton.setFocused(false);
	}

	@OnlyIn(Dist.CLIENT)
	class CuttingStationConfirmButton extends CuttingStationScreenButton {
		protected CuttingStationConfirmButton(int x, int y) {
			super(x, y, 0, 166, CommonComponents.EMPTY);
		}

		@Override
		public void onPress() {
			CuttingBoardRecipe result = CuttingStationScreen.this.menu.getSelectedRecipe();
			if (result == null) {
				LOGGER.error("There was an attempted to cut, but there was no cutting station recipe selected. You will hurt the cutting station this way!");
				return;
			}
			CuttingStationScreen.this.confirmButton.setFocused(false);
			if (ConfigManager.getConfig().shouldProcessStack() || CuttingStationScreen.this.menu.hasSingleInputItem()) {
				CuttingStationScreen.this.confirmButton.active = false;
			}
			Services.PLATFORM.getClientHelper().send(new CutPacket(CuttingStationScreen.this.menu.containerId, result));
		}
	}

	@OnlyIn(Dist.CLIENT)
	abstract static class CuttingStationScreenButton extends AbstractButton {
		private boolean selected;
		private final int iconX;
		private final int iconY;

		public CuttingStationScreenButton(int x, int y, int iconX, int iconY, Component message) {
			super(x, y, 22, 22, message);
			this.iconX = iconX;
			this.iconY = iconY;
		}

		@Override
		protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
			int j = 0;
			if (!this.active) {
				j += this.width * 2;
			} else if (this.selected) {
				j += this.width;
			} else if (this.isHoveredOrFocused()) {
				j += this.width * 3;
			}

			guiGraphics.blit(CuttingStationScreen.BEACON_SCREEN_LOCATION, this.getX(), this.getY(), j, 219, this.width, this.height);
			this.renderIcon(guiGraphics);
		}

		protected void renderIcon(GuiGraphics guiGraphics) {
			guiGraphics.blit(CuttingStationScreen.BG_LOCATION, this.getX() + 2, this.getY() + 2, this.iconX, this.iconY, 18, 18);
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
