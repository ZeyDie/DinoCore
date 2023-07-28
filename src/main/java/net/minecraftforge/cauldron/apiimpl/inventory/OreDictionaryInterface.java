package net.minecraftforge.cauldron.apiimpl.inventory;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.cauldron.api.inventory.BukkitOreDictionary;
import net.minecraftforge.cauldron.api.inventory.OreDictionaryEntry;
import net.minecraftforge.oredict.OreDictionary;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OreDictionaryInterface implements BukkitOreDictionary {
    private Map<String, String> normalizedToCanonicalMap = null;

    private void initializeMap() {
        normalizedToCanonicalMap = new HashMap<String, String>();

        for (final String str : getAllOreNames()) {
            if (str == null || str.isEmpty()) {
                continue;
            }
            normalizedToCanonicalMap.put(Material.normalizeName(str), str);
        }
    }

    @Override
    public OreDictionaryEntry getOreEntry(final String name) {
        if (normalizedToCanonicalMap == null) {
            initializeMap();
        }

        final String canonical = normalizedToCanonicalMap.get(Material.normalizeName(name));
        if (canonical == null) {
            return null;
        }

        return OreDictionaryEntry.valueOf(OreDictionary.getOreID(canonical));
    }

    @Override
    public List<OreDictionaryEntry> getOreEntries(final ItemStack itemStack) {
        final int id = OreDictionary.getOreID(CraftItemStack.asNMSCopy(itemStack));

        return ImmutableList.of(OreDictionaryEntry.valueOf(id));
    }

    @Override
    public List<OreDictionaryEntry> getOreEntries(final Material material) {
        return getOreEntries(new ItemStack(material));
    }

    @Override
    public String getOreName(final OreDictionaryEntry entry) {
        return OreDictionary.getOreName(entry.getId());
    }

    @Override
    public List<ItemStack> getDefinitions(final OreDictionaryEntry entry) {
        @SuppressWarnings("deprecation") final List<net.minecraft.item.ItemStack> items = OreDictionary.getOres(entry.getId());

        final ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
        for (final net.minecraft.item.ItemStack nmsItem : items) {
            builder.add(CraftItemStack.asCraftMirror(nmsItem));
        }

        return builder.build();
    }

    @Override
    public List<String> getAllOreNames() {
        return Arrays.asList(OreDictionary.getOreNames());
    }
}
