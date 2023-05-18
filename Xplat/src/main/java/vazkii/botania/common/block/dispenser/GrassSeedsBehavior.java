package vazkii.botania.common.block.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.item.GrassSeedsItem;
import vazkii.botania.network.EffectType;
import vazkii.botania.network.clientbound.BotaniaEffectPacket;
import vazkii.botania.xplat.XplatAbstractions;

public class GrassSeedsBehavior extends OptionalDispenseItemBehavior {
	@NotNull
	@Override
	public ItemStack execute(BlockSource source, ItemStack stack) {
		ServerLevel world = source.getLevel();
		BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));

		setSuccess(((GrassSeedsItem) stack.getItem()).applySeeds(world, pos, stack));

		if (isSuccess()) {
			XplatAbstractions.INSTANCE.sendToNear(world, pos,
					new BotaniaEffectPacket(EffectType.GRASS_SEED_PARTICLES,
							(double) pos.getX(), (double) pos.getY(), (double) pos.getZ(),
							GrassSeedsItem.getID(((GrassSeedsItem) stack.getItem()).getIslandType(null))));
			return stack;
		}

		return super.execute(source, stack);
	}
}
