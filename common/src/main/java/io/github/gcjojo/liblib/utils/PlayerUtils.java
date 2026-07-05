package io.github.gcjojo.liblib.utils;

import io.github.gcjojo.liblib.LibLib;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static Map<ResourceLocation, Integer> getInventoryItemAmounts(ServerLevel level, List<ItemStack> inventory) {
        Map<ResourceLocation, Integer> itemAmounts = new HashMap<>();
        inventory.forEach(stack -> {
            Registry<Item> itemRegistry = level.registryAccess().registryOrThrow(Registries.ITEM);
            ResourceLocation itemId = itemRegistry.getKey(stack.getItem());
            itemAmounts.computeIfPresent(itemId, (item, oldAmount) -> oldAmount + stack.getCount());
            itemAmounts.putIfAbsent(itemId, stack.getCount());
        });

        return itemAmounts;
    }

    public static Map<ResourceLocation, Integer> compareInventories(ServerLevel level, Map<ResourceLocation, Integer> amountsA, Map<ResourceLocation, Integer> amountsB) {
        Map<ResourceLocation, Integer> difference = new HashMap<>();
        if (amountsA.isEmpty() || amountsB.isEmpty() || amountsA.equals(amountsB)) return difference;

        /*LibLib.getLogger().info("Amounts a");
        amountsA.forEach((itemId, amountA) -> LibLib.getLogger().info("    -{}x{}", amountA, itemId));

        LibLib.getLogger().info("Amounts b");
        amountsB.forEach((itemId, amountB) -> LibLib.getLogger().info("    -{}x{}", amountB, itemId));*/

        List<ResourceLocation> allItems = new java.util.ArrayList<>(amountsB.keySet().stream().toList());
        try {
            allItems.addAll(amountsA.keySet().stream().toList());
        } catch (UnsupportedOperationException e) {
            LibLib.printException("Invalid Operation when adding amountsA and amountsB", e);
        }

        allItems.forEach(itemId -> {
            if (!amountsA.containsKey(itemId) || amountsA.get(itemId) == 0) {
                difference.put(itemId, amountsB.get(itemId));
                return;
            } else if (!amountsB.containsKey(itemId) || amountsB.get(itemId) == 0) {
                difference.put(itemId, -amountsA.get(itemId));
                return;
            }

            int amountA = amountsA.get(itemId);
            int amountB = amountsB.get(itemId);
            difference.put(itemId, amountB - amountA);
        });

        return difference;
    }
}
