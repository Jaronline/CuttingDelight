package dev.jaronline.cuttingdelight.neoforge.data;

import dev.jaronline.cuttingdelight.neoforge.CuttingDelight;
import dev.jaronline.cuttingdelight.neoforge.common.registry.BlockRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.tag.CompatibilityTags;

import java.util.concurrent.CompletableFuture;

public class BlockTags extends BlockTagsProvider {
    private final BlockRegistry blocks = BlockRegistry.getInstance();

    public BlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CuttingDelight.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_AXE).add(blocks.cuttingBoard.get());
        tag(CompatibilityTags.CREATE_BRITTLE).add(blocks.cuttingBoard.get());
    }
}
