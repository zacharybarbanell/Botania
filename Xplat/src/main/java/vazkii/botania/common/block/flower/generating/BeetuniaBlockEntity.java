package vazkii.botania.common.block.flower.generating;

import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import vazkii.botania.api.block_entity.GeneratingFlowerBlockEntity;
import vazkii.botania.api.block_entity.RadiusDescriptor;
import vazkii.botania.client.fx.WispParticleData;

public class BeetuniaBlockEntity extends GeneratingFlowerBlockEntity {
    public static final int RANGE = 8;

    private static final int NORTH = 0;
    private static final int EAST = 1;
    private static final int SOUTH = 2;
    private static final int WEST = 3;
    private static final Direction[] DIRECTIONS = {
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST
    };

    private final BlockPos[] hiveBindings = new BlockPos[4];
    private final BlockPos[] flowerBindings = new BlockPos[4];

    public BeetuniaBlockEntity(BlockPos pos, BlockState state) {
        super(null, pos, state); // TODO
    }

    @Override
    public void tickFlower() {
        super.tickFlower();

        updateBindings(Direction.UP, hiveBindings,
                (pos) -> getLevel().getBlockState(pos).getBlock() instanceof BeehiveBlock);
        updateBindings(Direction.DOWN, flowerBindings, (pos) -> getLevel().getBlockState(pos).is(BlockTags.FLOWERS));

        spawnParticles();
        // TODO
    }

    private void updateBindings(Direction vert, BlockPos[] bindings, Predicate<BlockPos> accept) {
        for (int i = 0; i < 4; i++) {
            BlockPos oldBinding = bindings[i];
            BlockPos newBinding = null;
            Vec3i offset = vert.getNormal().offset(DIRECTIONS[i].getNormal());
            for (int j = 1; j <= RANGE; j++) {
                BlockPos candidate = getEffectivePos().offset(offset.multiply(j));
                if (accept.test(candidate)) {
                    newBinding = candidate;
                    break;
                }
            }
            if (!Objects.equal(oldBinding, newBinding)) {
                onBindingChange(oldBinding, newBinding);
            }
            bindings[i] = newBinding;
        }
    }

    private void spawnParticles() {
        for (int i = 0; i < 4; i++) {
            if (hiveBindings[i] != null) {
                spawnParticle(Direction.UP, DIRECTIONS[i], hiveBindings[i].getCenter());
            }
            if (flowerBindings[i] != null) {
                spawnParticle(Direction.DOWN, DIRECTIONS[i], flowerBindings[i].getCenter());
            }
        }
    }

    private void spawnParticle(Direction dir1, Direction dir2, Vec3 target) {
        if (getLevel().isClientSide) {
            double velMul = (1 - 0.98);
            float ageMul = 10;
            WispParticleData data = WispParticleData.wisp(1.0f, 1.0f, 0.0f, 0.0f, ageMul).withNoClip(true);
            Vec3 offset = getLevel().getBlockState(getBlockPos()).getOffset(getLevel(), getBlockPos());
            double x = getBlockPos().getX() + offset.x;
			double y = getBlockPos().getY() + offset.y;
			double z = getBlockPos().getZ() + offset.z;
            Vec3 center = new Vec3(x + 0.3 + Math.random() * 0.5, y + 0.5 + Math.random() * 0.5, z + 0.3 + Math.random() * 0.5);
            Vec3 vel = target.subtract(center).scale(velMul);
            getLevel().addParticle(data, center.x, center.y, center.z, vel.x, vel.y, vel.z);
        }
    }

    private void onBindingChange(@Nullable BlockPos oldBinding, @Nullable BlockPos newBinding) {
        // TODO
    }

    @Override
    public int getMaxMana() {
        // TODO
        return 1000000;
    }

    @Override
    public int getColor() {
        // TODO
        return 0xFF0000;
    }

    @Override
    public @Nullable RadiusDescriptor getRadius() {
        return RadiusDescriptor.Rectangle.square(getEffectivePos(), RANGE);
    }

    @Override
    public boolean isOvergrowthAffected() {
        return false;
    }
}
