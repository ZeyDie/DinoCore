package net.minecraftforge.oredict;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;
import net.minecraftforge.cauldron.potion.CustomModRecipe;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

// Cauldron start
// Cauldron end

public class ShapedOreRecipe implements IRecipe
{
    //Added in for future ease of change, but hard coded for now.
    private static final int MAX_CRAFT_GRID_WIDTH = 3;
    private static final int MAX_CRAFT_GRID_HEIGHT = 3;

    private ItemStack output = null;
    private Object[] input = null;
    private int width = 0;
    private int height = 0;
    private boolean mirrored = true;
    private ShapedRecipes vanillaRecipe = null; // Cauldron - bukkit compatibility

    public ShapedOreRecipe(final Block     result, final Object... recipe){ this(new ItemStack(result), recipe); }
    public ShapedOreRecipe(final Item      result, final Object... recipe){ this(new ItemStack(result), recipe); }
    public ShapedOreRecipe(final ItemStack result, Object... recipe)
    {
        Object[] recipe1 = recipe;
        output = result.copy();

        String shape = "";
        int idx = 0;

        if (recipe1[idx] instanceof Boolean)
        {
            mirrored = (Boolean) recipe1[idx];
            if (recipe1[idx+1] instanceof Object[])
            {
                recipe1 = (Object[]) recipe1[idx+1];
            }
            else
            {
                idx = 1;
            }
        }

        if (recipe1[idx] instanceof String[])
        {
            final String[] parts = ((String[]) recipe1[idx++]);

            for (final String s : parts)
            {
                width = s.length();
                shape += s;
            }

            height = parts.length;
        }
        else
        {
            while (recipe1[idx] instanceof String)
            {
                final String s = (String) recipe1[idx++];
                shape += s;
                width = s.length();
                height++;
            }
        }

        if (width * height != shape.length())
        {
            String ret = "Invalid shaped ore recipe: ";
            for (final Object tmp : recipe1)
            {
                ret += tmp + ", ";
            }
            ret += output;
            throw new RuntimeException(ret);
        }

        final HashMap<Character, Object> itemMap = new HashMap<Character, Object>();

        for (; idx < recipe1.length; idx += 2)
        {
            final Character chr = (Character) recipe1[idx];
            final Object in = recipe1[idx + 1];

            if (in instanceof ItemStack)
            {
                itemMap.put(chr, ((ItemStack)in).copy());
            }
            else if (in instanceof Item)
            {
                itemMap.put(chr, new ItemStack((Item)in));
            }
            else if (in instanceof Block)
            {
                itemMap.put(chr, new ItemStack((Block)in, 1, OreDictionary.WILDCARD_VALUE));
            }
            else if (in instanceof String)
            {
                itemMap.put(chr, OreDictionary.getOres((String)in));
            }
            else
            {
                String ret = "Invalid shaped ore recipe: ";
                for (final Object tmp : recipe1)
                {
                    ret += tmp + ", ";
                }
                ret += output;
                throw new RuntimeException(ret);
            }
        }

        input = new Object[width * height];
        int x = 0;
        for (final char chr : shape.toCharArray())
        {
            input[x++] = itemMap.get(chr);
        }
    }

    ShapedOreRecipe(final ShapedRecipes recipe, final Map<ItemStack, String> replacements)
    {
        vanillaRecipe = recipe; // Cauldron - bukkit compatibility
        output = recipe.getRecipeOutput();
        width = recipe.recipeWidth;
        height = recipe.recipeHeight;

        input = new Object[recipe.recipeItems.length];

        for(int i = 0; i < input.length; i++)
        {
            final ItemStack ingred = recipe.recipeItems[i];

            if(ingred == null) continue;

            input[i] = recipe.recipeItems[i];

            for(final Entry<ItemStack, String> replace : replacements.entrySet())
            {
                if(OreDictionary.itemMatches(replace.getKey(), ingred, true))
                {
                    input[i] = OreDictionary.getOres(replace.getValue());
                    break;
                }
            }
        }
    }

    @Override
    public ItemStack getCraftingResult(final InventoryCrafting var1){ return output.copy(); }

    @Override
    public int getRecipeSize(){ return input.length; }

    @Override
    public ItemStack getRecipeOutput(){ return output; }

    @Override
    public boolean matches(final InventoryCrafting inv, final World world)
    {
        for (int x = 0; x <= MAX_CRAFT_GRID_WIDTH - width; x++)
        {
            for (int y = 0; y <= MAX_CRAFT_GRID_HEIGHT - height; ++y)
            {
                if (checkMatch(inv, x, y, false))
                {
                    return true;
                }

                if (mirrored && checkMatch(inv, x, y, true))
                {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkMatch(final InventoryCrafting inv, final int startX, final int startY, final boolean mirror)
    {
        for (int x = 0; x < MAX_CRAFT_GRID_WIDTH; x++)
        {
            for (int y = 0; y < MAX_CRAFT_GRID_HEIGHT; y++)
            {
                final int subX = x - startX;
                final int subY = y - startY;
                Object target = null;

                if (subX >= 0 && subY >= 0 && subX < width && subY < height)
                {
                    if (mirror)
                    {
                        target = input[width - subX - 1 + subY * width];
                    }
                    else
                    {
                        target = input[subX + subY * width];
                    }
                }

                final ItemStack slot = inv.getStackInRowAndColumn(x, y);

                if (target instanceof ItemStack)
                {
                    if (!checkItemEquals((ItemStack)target, slot))
                    {
                        return false;
                    }
                }
                else if (target instanceof ArrayList)
                {
                    boolean matched = false;

                    for (final ItemStack item : (ArrayList<ItemStack>)target)
                    {
                        matched = matched || checkItemEquals(item, slot);
                    }

                    if (!matched)
                    {
                        return false;
                    }
                }
                else if (target == null && slot != null)
                {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean checkItemEquals(final ItemStack target, final ItemStack input)
    {
        if (input == null && target != null || input != null && target == null)
        {
            return false;
        }
        return (target.itemID == input.itemID && (target.getItemDamage() == OreDictionary.WILDCARD_VALUE|| target.getItemDamage() == input.getItemDamage()));
    }

    public ShapedOreRecipe setMirrored(final boolean mirror)
    {
        mirrored = mirror;
        return this;
    }

    /**
     * Returns the input for this recipe, any mod accessing this value should never
     * manipulate the values in this array as it will effect the recipe itself.
     * @return The recipes input vales.
     */
    public Object[] getInput()
    {
        return this.input;
    }

    // Cauldron start - required for Bukkit API
    @Override
    public Recipe toBukkitRecipe() {
        if (vanillaRecipe != null)
            return vanillaRecipe.toBukkitRecipe();
        return new CustomModRecipe(this);
    }
    // Cauldron end
}
