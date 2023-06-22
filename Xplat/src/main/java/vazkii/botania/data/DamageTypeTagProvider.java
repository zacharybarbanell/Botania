package vazkii.botania.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;

import vazkii.botania.common.BotaniaDamageTypes;
import vazkii.botania.common.lib.BotaniaTags;
import vazkii.botania.data.util.TagsProviderForceable;

import java.util.concurrent.CompletableFuture;

public class DamageTypeTagProvider extends TagsProviderForceable<DamageType> {

	public DamageTypeTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(packOutput, Registries.DAMAGE_TYPE, lookupProvider);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(DamageTypeTags.IS_FIRE);
		this.tag(BotaniaTags.DamageTypes.RING_OF_ODIN_IMMUNE).add(
				DamageTypes.DROWN,
				DamageTypes.FALL,
				DamageTypes.IN_WALL,
				DamageTypes.STARVE,
				DamageTypes.FLY_INTO_WALL
		).addTag(DamageTypeTags.IS_FIRE);

		this.tagUnchecked(DamageTypeTags.BYPASSES_ARMOR).add(
				BotaniaDamageTypes.PLAYER_ATTACK_ARMOR_PIERCING,
				BotaniaDamageTypes.RELIC_DAMAGE
		);

		this.tagUnchecked(DamageTypeTags.BYPASSES_RESISTANCE).add(BotaniaDamageTypes.RELIC_DAMAGE);
		this.tagUnchecked(DamageTypeTags.NO_IMPACT).add(BotaniaDamageTypes.RELIC_DAMAGE);
		this.tagUnchecked(DamageTypeTags.BYPASSES_EFFECTS).add(BotaniaDamageTypes.RELIC_DAMAGE);
		this.tagUnchecked(DamageTypeTags.BYPASSES_ENCHANTMENTS).add(BotaniaDamageTypes.RELIC_DAMAGE);
	}
}
