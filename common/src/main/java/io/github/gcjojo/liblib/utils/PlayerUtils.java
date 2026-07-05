package io.github.gcjojo.liblib.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.List;
import java.util.Optional;

// merci ClaudeSlop
public class PlayerUtils {
    public static Optional<ResourceLocation> getPlayerBiome(ServerPlayer serverPlayer) {
        Holder<Biome> biomeHolder = serverPlayer.level()
                .getBiome(serverPlayer.blockPosition());

        Optional<ResourceKey<Biome>> biomeKey = biomeHolder.unwrapKey();
        return biomeKey.map(ResourceKey::location);
    }

    public static boolean isInBiome(ServerPlayer serverPlayer, ResourceLocation biomeId) {
        var playerBiomeOpt = getPlayerBiome(serverPlayer);
        return playerBiomeOpt.filter(biomeId::equals).isPresent();
    }

    // Merci ClaudeSlop
    public static boolean isInStructure(ServerPlayer player, ResourceLocation structureId) {
        ServerLevel level = (ServerLevel) player.level();
        ResourceKey<Structure> key = ResourceKey.create(Registries.STRUCTURE, structureId);

        return level.structureManager()
                .getStructureWithPieceAt(player.blockPosition(), key)
                .isValid();
    }

    public static boolean isInStructureTag(ServerPlayer player, TagKey<Structure> tag) {
        ServerLevel level = (ServerLevel) player.level();
        return level.structureManager()
                .getStructureWithPieceAt(player.blockPosition(), tag)
                .isValid();
    }

    // TODO Rework to return the difference in inventories
    public static boolean compareInventories(List<ItemStack> a, List<ItemStack> b) {
        if(a.size() != b.size()) return false;
        for(int i = 0; i <= a.size(); i++)
            if(!ItemStack.matches(a.get(i), b.get(i)))
                return false;

        return true;
    }
}
