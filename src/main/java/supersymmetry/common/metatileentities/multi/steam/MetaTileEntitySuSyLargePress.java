package supersymmetry.common.metatileentities.multi.steam;

import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.client.particle.VanillaParticleEffects;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.utils.TooltipHelper;
import gregtech.common.blocks.*;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import net.minecraftforge.oredict.OreDictionary;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import gregtech.api.capability.impl.SteamMultiWorkable;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.multiblock.RecipeMapSteamMultiblockController;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.ConfigHolder;
import gregtech.api.unification.material.Materials;
import supersymmetry.client.renderer.textures.SusyTextures;


public class MetaTileEntitySuSyLargePress extends RecipeMapSteamMultiblockController {

    private static final int PARALLEL_LIMIT = 8;

    public MetaTileEntitySuSyLargePress(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.COMPRESSOR_RECIPES, CONVERSION_RATE);
        this.recipeMapWorkable = new SteamMultiWorkable(this, CONVERSION_RATE);
        this.recipeMapWorkable.setParallelLimit(PARALLEL_LIMIT);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntitySuSyLargePress(this.metaTileEntityId);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("XXXXX", "X   X", "XXXXX", " YYY ")
                .aisle("PSSSP", "P###P", "PSSSP", "PPGPP")
                .aisle("XXMXX", "X   X", "XXXXX", " YYY ")
                .where('#', air())
                .where(' ', any())
                .where('S', states(MetaBlocks.COMPRESSED.get(Materials.Steel).getBlock(Materials.Steel)))
                .where('M', this.selfPredicate())
                .where('X', states(getCasingState()).setMinGlobalLimited(20).or(autoAbilities()))
                .where('Y', states(getCasingState()))
                .where('P', states(getPipeCasingState()))
                .where('G', states(getGearboxState()))
                .build();
    }

    protected static IBlockState getCasingState() {
        return ConfigHolder.machines.steelSteamMultiblocks ?
                MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID) :
                MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.BRONZE_BRICKS);
    }

    protected static IBlockState getPipeCasingState() {
        return ConfigHolder.machines.steelSteamMultiblocks ?
                MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE) :
                MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.BRONZE_PIPE);
    }

    protected static IBlockState getGearboxState() {
        return ConfigHolder.machines.steelSteamMultiblocks ?
                MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.STEEL_GEARBOX) :
                MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.BRONZE_GEARBOX);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return ConfigHolder.machines.steelSteamMultiblocks ?
                Textures.SOLID_STEEL_CASING : Textures.BRONZE_PLATED_BRICKS;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gregtech.multiblock.steam_.duration_modifier"));
        tooltip.add(I18n.format("gregtech.universal.tooltip.parallel", PARALLEL_LIMIT));
        tooltip.add(TooltipHelper.BLINKING_ORANGE + I18n.format("gregtech.multiblock.require_steam_parts"));
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public int getItemOutputLimit() {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return SusyTextures.LARGE_STEAM_PRESS_OVERLAY;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick() {
        if (isActive()) {
            VanillaParticleEffects.RANDOM_SPARKS.runEffect(this);
        }
    }
}
