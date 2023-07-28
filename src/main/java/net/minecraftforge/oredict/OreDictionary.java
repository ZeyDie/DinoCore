package net.minecraftforge.oredict;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class OreDictionary
{
    private static boolean hasInit = false;
    private static int maxID = 0;
    private static HashMap<String, Integer> oreIDs = new HashMap<String, Integer>();
    private static HashMap<Integer, ArrayList<ItemStack>> oreStacks = new HashMap<Integer, ArrayList<ItemStack>>();


    /**
     * Minecraft changed from -1 to Short.MAX_VALUE in 1.5 release for the "block wildcard". Use this in case it
     * changes again.
     */
    public static final int WILDCARD_VALUE = Short.MAX_VALUE;

    static {
        initVanillaEntries();
    }

    public static void initVanillaEntries()
    {
        if (!hasInit)
        {
            registerOre("logWood",     new ItemStack(Block.wood, 1, WILDCARD_VALUE));
            registerOre("plankWood",   new ItemStack(Block.planks, 1, WILDCARD_VALUE));
            registerOre("slabWood",    new ItemStack(Block.woodSingleSlab, 1, WILDCARD_VALUE));
            registerOre("stairWood",   Block.stairsWoodOak);
            registerOre("stairWood",   Block.stairsWoodBirch);
            registerOre("stairWood",   Block.stairsWoodJungle);
            registerOre("stairWood",   Block.stairsWoodSpruce);
            registerOre("stickWood",   Item.stick);
            registerOre("treeSapling", new ItemStack(Block.sapling, 1, WILDCARD_VALUE));
            registerOre("treeLeaves",  new ItemStack(Block.leaves, 1, WILDCARD_VALUE));
            registerOre("oreGold",     Block.oreGold);
            registerOre("oreIron",     Block.oreIron);
            registerOre("oreLapis",    Block.oreLapis);
            registerOre("oreDiamond",  Block.oreDiamond);
            registerOre("oreRedstone", Block.oreRedstone);
            registerOre("oreEmerald",  Block.oreEmerald);
            registerOre("oreQuartz",   Block.oreNetherQuartz);
            registerOre("stone",       Block.stone);
            registerOre("cobblestone", Block.cobblestone);
            registerOre("record",      Item.record13);
            registerOre("record",      Item.recordCat);
            registerOre("record",      Item.recordBlocks);
            registerOre("record",      Item.recordChirp);
            registerOre("record",      Item.recordFar);
            registerOre("record",      Item.recordMall);
            registerOre("record",      Item.recordMellohi);
            registerOre("record",      Item.recordStal);
            registerOre("record",      Item.recordStrad);
            registerOre("record",      Item.recordWard);
            registerOre("record",      Item.record11);
            registerOre("record",      Item.recordWait);
        }

        // Build our list of items to replace with ore tags
        final Map<ItemStack, String> replacements = new HashMap<ItemStack, String>();
        replacements.put(new ItemStack(Item.stick), "stickWood");
        replacements.put(new ItemStack(Block.planks), "plankWood");
        replacements.put(new ItemStack(Block.planks, 1, WILDCARD_VALUE), "plankWood");
        replacements.put(new ItemStack(Block.stone), "stone");
        replacements.put(new ItemStack(Block.stone, 1, WILDCARD_VALUE), "stone");
        replacements.put(new ItemStack(Block.cobblestone), "cobblestone");
        replacements.put(new ItemStack(Block.cobblestone, 1, WILDCARD_VALUE), "cobblestone");

        // Register dyes
        final String[] dyes =
        {
            "dyeBlack",
            "dyeRed",
            "dyeGreen",
            "dyeBrown",
            "dyeBlue",
            "dyePurple",
            "dyeCyan",
            "dyeLightGray",
            "dyeGray",
            "dyePink",
            "dyeLime",
            "dyeYellow",
            "dyeLightBlue",
            "dyeMagenta",
            "dyeOrange",
            "dyeWhite"
        };

        for(int i = 0; i < 16; i++)
        {
            final ItemStack dye = new ItemStack(Item.dyePowder, 1, i);
            if (!hasInit)
            {
                registerOre(dyes[i], dye);
            }
            replacements.put(dye, dyes[i]);
        }
        hasInit = true;

        final ItemStack[] replaceStacks = replacements.keySet().toArray(new ItemStack[0]);

        // Ignore recipes for the following items
        final ItemStack[] exclusions = {
            new ItemStack(Block.blockLapis),
            new ItemStack(Item.cookie),
            new ItemStack(Block.stoneBrick),
            new ItemStack(Block.stoneSingleSlab),
            new ItemStack(Block.stairsCobblestone),
            new ItemStack(Block.cobblestoneWall),
            new ItemStack(Block.stairsWoodOak),
            new ItemStack(Block.stairsWoodBirch),
            new ItemStack(Block.stairsWoodJungle),
            new ItemStack(Block.stairsWoodSpruce)
        };

        final List recipes = CraftingManager.getInstance().getRecipeList();
        final List<IRecipe> recipesToRemove = new ArrayList<IRecipe>();
        final List<IRecipe> recipesToAdd = new ArrayList<IRecipe>();

        // Search vanilla recipes for recipes to replace
        for(final Object obj : recipes)
        {
            if(obj instanceof ShapedRecipes)
            {
                final ShapedRecipes recipe = (ShapedRecipes)obj;
                final ItemStack output = recipe.getRecipeOutput();
                if ((output != null && containsMatch(false, exclusions, output)) || output == null) // Cauldron - fixes NPE's with null recipes being added to forge
                {
                    continue;
                }

                if(containsMatch(true, recipe.recipeItems, replaceStacks))
                {
                    recipesToRemove.add(recipe);
                    recipesToAdd.add(new ShapedOreRecipe(recipe, replacements));
                }
            }
            else if(obj instanceof ShapelessRecipes)
            {
                final ShapelessRecipes recipe = (ShapelessRecipes)obj;
                final ItemStack output = recipe.getRecipeOutput();
                if ((output != null && containsMatch(false, exclusions, output)) || output == null) // Cauldron - fixes NPE's with null recipes being added to forge
                {
                    continue;
                }

                if(containsMatch(true, (ItemStack[])recipe.recipeItems.toArray(new ItemStack[0]), replaceStacks))
                {
                    recipesToRemove.add((IRecipe)obj);
                    final IRecipe newRecipe = new ShapelessOreRecipe(recipe, replacements);
                    recipesToAdd.add(newRecipe);
                }
            }
        }

        recipes.removeAll(recipesToRemove);
        recipes.addAll(recipesToAdd);
        if (!recipesToRemove.isEmpty())
        {
            System.out.println("Replaced " + recipesToRemove.size() + " ore recipies");
        }
    }

    /**
     * Gets the integer ID for the specified ore name.
     * If the name does not have a ID it assigns it a new one.
     *
     * @param name The unique name for this ore 'oreIron', 'ingotIron', etc..
     * @return A number representing the ID for this ore type
     */
    public static int getOreID(final String name)
    {
        Integer val = oreIDs.get(name);
        if (val == null)
        {
            val = maxID++;
            oreIDs.put(name, val);
            oreStacks.put(val, new ArrayList<ItemStack>());
        }
        return val;
    }

    /**
     * Reverse of getOreID, will not create new entries.
     *
     * @param id The ID to translate to a string
     * @return The String name, or "Unknown" if not found.
     */
    public static String getOreName(final int id)
    {
        for (final Map.Entry<String, Integer> entry : oreIDs.entrySet())
        {
            if (id == entry.getValue())
            {
                return entry.getKey();
            }
        }
        return "Unknown";
    }

    /**
     * Gets the integer ID for the specified item stack.
     * If the item stack is not linked to any ore, this will return -1 and no new entry will be created.
     *
     * @param itemStack The item stack of the ore.
     * @return A number representing the ID for this ore type, or -1 if couldn't find it.
     */
    public static int getOreID(final ItemStack itemStack)
    {
        if (itemStack == null)
        {
            return -1;
        }

        for(final Entry<Integer, ArrayList<ItemStack>> ore : oreStacks.entrySet())
        {
            for(final ItemStack target : ore.getValue())
            {
                if(itemStack.itemID == target.itemID && (target.getItemDamage() == WILDCARD_VALUE || itemStack.getItemDamage() == target.getItemDamage()))
                {
                    return ore.getKey();
                }
            }
        }
        return -1; // didn't find it.
    }

    /**
     * Retrieves the ArrayList of items that are registered to this ore type.
     * Creates the list as empty if it did not exist.
     *
     * @param name The ore name, directly calls getOreID
     * @return An arrayList containing ItemStacks registered for this ore
     */
    public static ArrayList<ItemStack> getOres(final String name)
    {
        return getOres(getOreID(name));
    }

    /**
     * Retrieves a list of all unique ore names that are already registered.
     *
     * @return All unique ore names that are currently registered.
     */
    public static String[] getOreNames()
    {
        return oreIDs.keySet().toArray(new String[0]);
    }

    /**
     * Retrieves the ArrayList of items that are registered to this ore type.
     * Creates the list as empty if it did not exist.
     *
     * @param id The ore ID, see getOreID
     * @return An arrayList containing ItemStacks registered for this ore
     */
    public static ArrayList<ItemStack> getOres(final Integer id)
    {
        ArrayList<ItemStack> val = oreStacks.get(id);
        if (val == null)
        {
            val = new ArrayList<ItemStack>();
            oreStacks.put(id, val);
        }
        return val;
    }

    private static boolean containsMatch(final boolean strict, final ItemStack[] inputs, final ItemStack... targets)
    {
        for (final ItemStack input : inputs)
        {
            for (final ItemStack target : targets)
            {
                if (itemMatches(target, input, strict))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean itemMatches(final ItemStack target, final ItemStack input, final boolean strict)
    {
        if (input == null && target != null || input != null && target == null)
        {
            return false;
        }
        return (target.itemID == input.itemID && ((target.getItemDamage() == WILDCARD_VALUE && !strict) || target.getItemDamage() == input.getItemDamage()));
    }

    //Convenience functions that make for cleaner code mod side. They all drill down to registerOre(String, int, ItemStack)
    public static void registerOre(final String name, final Item      ore){ registerOre(name, new ItemStack(ore));  }
    public static void registerOre(final String name, final Block     ore){ registerOre(name, new ItemStack(ore));  }
    public static void registerOre(final String name, final ItemStack ore){ registerOre(name, getOreID(name), ore); }
    public static void registerOre(final int    id, final Item      ore){ registerOre(id,   new ItemStack(ore));  }
    public static void registerOre(final int    id, final Block     ore){ registerOre(id,   new ItemStack(ore));  }
    public static void registerOre(final int    id, final ItemStack ore){ registerOre(getOreName(id), id, ore);   }

    /**
     * Registers a ore item into the dictionary.
     * Raises the registerOre function in all registered handlers.
     *
     * @param name The name of the ore
     * @param id The ID of the ore
     * @param ore The ore's ItemStack
     */
    private static void registerOre(final String name, final int id, ItemStack ore)
    {
        final ArrayList<ItemStack> ores = getOres(id);
        ItemStack ore1 = ore.copy();
        ores.add(ore1);
        MinecraftForge.EVENT_BUS.post(new OreRegisterEvent(name, ore1));
    }

    public static class OreRegisterEvent extends Event
    {
        public final String Name;
        public final ItemStack Ore;

        public OreRegisterEvent(final String name, final ItemStack ore)
        {
            this.Name = name;
            this.Ore = ore;
        }
    }
}
