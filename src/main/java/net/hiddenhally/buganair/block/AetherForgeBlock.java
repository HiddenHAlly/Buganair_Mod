package net.hiddenhally.buganair.block;

import net.hiddenhally.buganair.BuganairMod;
import net.hiddenhally.buganair.block.entity.AetherForgeBlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

// Add 'implements BlockEntityProvider' to your class definition
public class AetherForgeBlock extends Block implements BlockEntityProvider {
    // 1. Define the block state properties
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = Properties.LIT;

    public AetherForgeBlock(Settings settings) {
        super(settings);
        // 2. Set the default state when the block is generated/obtained
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(LIT, false));
    }

    // 3. Append the properties so Minecraft knows they belong to this block
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    // 4. Make the block properly face the player when placed down
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    // ... your existing properties and constructor ...

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AetherForgeBlockEntity(pos, state);
    }

    // Tell the game to tick our block entity
    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        // Check if the game is asking to tick OUR block entity type
        if (type == BuganairMod.AETHER_FORGE_BE) {
            // Cast it safely and pass it to our static tick method
            return (world1, pos1, state1, blockEntity) -> AetherForgeBlockEntity.tick(world1, pos1, state1, (AetherForgeBlockEntity) blockEntity);
        }
        return null;
    }

    // Open the UI when right-clicked
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof NamedScreenHandlerFactory screenHandlerFactory) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }
}