/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 * File Created @ [Jul 26, 2014, 1:42:17 PM (GMT)]
 */
package vazkii.botania.common.block.subtile.generating;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.ItemHandlerHelper;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.SubTileGenerating;
import vazkii.botania.common.lexicon.LexiconData;

import java.util.List;

public class SubTileGourmaryllis extends SubTileGenerating {

	private static final String TAG_COOLDOWN = "cooldown";
	private static final String TAG_DIGESTING_MANA = "digestingMana";
	private static final String TAG_LAST_FOOD = "lastFood";
	private static final String TAG_LAST_FOOD_COUNT = "lastFoodCount";
	private static final int RANGE = 1;

	private int cooldown = 0;
	private int digestingMana = 0;
	private ItemStack lastFood = ItemStack.EMPTY;
	private int lastFoodCount = 0;

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (supertile.getWorld().isRemote)
			return;

		if(cooldown > -1)
			cooldown--;
		if(cooldown == 0 && digestingMana != 0) {
			mana = Math.min(getMaxMana(), mana + digestingMana);
			digestingMana = 0;
			getWorld().playSound(null, supertile.getPos(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.BLOCKS, 1, 1);
			sync();
		}

		int slowdown = getSlowdownFactor();

		List<EntityItem> items = supertile.getWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(supertile.getPos().add(-RANGE, -RANGE, -RANGE), supertile.getPos().add(RANGE + 1, RANGE + 1, RANGE + 1)));

		for(EntityItem item : items) {
			ItemStack stack = item.getItem();

			if(!stack.isEmpty() && stack.getItem() instanceof ItemFood && !item.isDead && item.age >= slowdown) {
				if(cooldown <= 0) {
					if(ItemHandlerHelper.canItemStacksStack(lastFood, stack)) {
						lastFoodCount++;
					} else {
						lastFood = stack.copy();
						lastFood.setCount(1);
						lastFoodCount = 1;
					}

					int val = Math.min(12, ((ItemFood) stack.getItem()).getHealAmount(stack));
					digestingMana = val * val * 64;
					digestingMana *= 1F / lastFoodCount;
					cooldown = val * 10;
					item.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.2F, 0.5F + (float) Math.random() * 0.5F);
					sync();
					((WorldServer) supertile.getWorld()).spawnParticle(EnumParticleTypes.ITEM_CRACK, false, item.posX, item.posY, item.posZ, 20, 0.1D, 0.1D, 0.1D, 0.05D, Item.getIdFromItem(stack.getItem()), stack.getItemDamage());
				}

				item.setDead();
			}
		}
	}

	@Override
	public void writeToPacketNBT(NBTTagCompound cmp) {
		super.writeToPacketNBT(cmp);
		cmp.setInteger(TAG_COOLDOWN, cooldown);
		cmp.setInteger(TAG_DIGESTING_MANA, digestingMana);
		cmp.setTag(TAG_LAST_FOOD, lastFood.writeToNBT(new NBTTagCompound()));
		cmp.setInteger(TAG_LAST_FOOD_COUNT, lastFoodCount);
	}

	@Override
	public void readFromPacketNBT(NBTTagCompound cmp) {
		super.readFromPacketNBT(cmp);
		cooldown = cmp.getInteger(TAG_COOLDOWN);
		digestingMana = cmp.getInteger(TAG_DIGESTING_MANA);
		lastFood = new ItemStack(cmp.getCompoundTag(TAG_LAST_FOOD));
		lastFoodCount = cmp.getInteger(TAG_LAST_FOOD_COUNT);
	}

	@Override
	public RadiusDescriptor getRadius() {
		return new RadiusDescriptor.Square(toBlockPos(), RANGE);
	}

	@Override
	public int getMaxMana() {
		return 8000;
	}

	@Override
	public int getColor() {
		return 0xD3D604;
	}

	@Override
	public LexiconEntry getEntry() {
		return LexiconData.gourmaryllis;
	}

}
