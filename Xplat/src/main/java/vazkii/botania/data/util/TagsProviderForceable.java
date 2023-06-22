package vazkii.botania.data.util;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;

public abstract class TagsProviderForceable<T> extends TagsProvider<T>{
    public TagsProviderForceable(PackOutput packOutput, ResourceKey<? extends Registry<T>> resourceKey, CompletableFuture<HolderLookup.Provider> completableFuture) {
		super(packOutput, resourceKey, completableFuture);
	}

    protected TagBuilder getOrCreateUncheckedBuilder(TagKey<T> tagKey) {
		return this.builders.computeIfAbsent(tagKey.location(), resourceLocation -> new TagBuilderUnchecked());
	}

    protected TagsProvider.TagAppender<T> tagUnchecked(TagKey<T> tagKey) {
		TagBuilder tagBuilder = this.getOrCreateUncheckedBuilder(tagKey);
		return new TagAppender<>(tagBuilder);
	}

    //homeopathic class to get around
    public static class TagAppender<T> extends TagsProvider.TagAppender<T> {
        protected TagAppender(TagBuilder tagBuilder) {
			super(tagBuilder);
		}
    }

    public static class TagBuilderUnchecked extends TagBuilder {
        @Override
        public TagBuilder addElement(ResourceLocation resourceLocation) {
		    return this.add(new TagEntryUnchecked(resourceLocation, false));
	    }
    }

    public static class TagEntryUnchecked extends TagEntry {
        public TagEntryUnchecked(ResourceLocation resourceLocation, boolean tag) {
            super(resourceLocation, tag, true);
        }

        @Override
        public boolean verifyIfPresent(Predicate<ResourceLocation> predicate, Predicate<ResourceLocation> predicate2) {
            return true;
        }
    }

}