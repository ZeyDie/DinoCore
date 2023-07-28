package net.minecraftforge.oredict;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;
import net.minecraftforge.cauldron.potion.CustomModRecipe;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

// Cauldron start
// Cauldron end

public class ShapelessOreRecipe implements IRecipe
{
    private ItemStack output = null;
    private ArrayList input = new ArrayList();
    private ShapelessRecipes vanillaRecipe = null; // Cauldron - bukkit compatibility

    public ShapelessOreRecipe(final Block result, final Object... recipe){ this(new ItemStack(result), recipe); }
    public ShapelessOreRecipe(final Item  result, final Object... recipe){ this(new ItemStack(result), recipe); }

    public ShapelessOreRecipe(final ItemStack result, final Object... recipe)
    {
        output = result.copy();
        for (final Object in : recipe)
        {
            if (in instanceof ItemStack)
            {
                input.add(((ItemStack)in).copy());
            }
            else if (in instanceof Item)
            {
                input.add(new ItemStack((Item)in));
            }
            else if (in instanceof Block)
            {
                input.add(new ItemStack((Block)in));
            }
            else if (in instanceof String)
            {
                input.add(OreDictionary.getOres((String)in));
            }
            else
            {
                String ret = "Invalid shapeless ore recipe: ";
                for (final Object tmp :  recipe)
                {
                    ret += tmp + ", ";
                }
                ret += output;
                throw new RuntimeException(ret);
            }
        }
    }

    ShapelessOreRecipe(final ShapelessRecipes recipe, final Map<ItemStack, String> replacements)
    {
        vanillaRecipe = recipe; // Cauldron - bukkit compatibility
        output = recipe.getRecipeOutput();

        for(final ItemStack ingred : ((List<ItemStack>)recipe.recipeItems))
        {
            Object finalObj = ingred;
            for(final Entry<ItemStack, String> replace : replacements.entrySet())
            {
                if(OreDictionary.itemMatches(replace.getKey(), ingred, false))
                {
                    finalObj = OreDictionary.getOres(replace.getValue());
                    break;
                }
            }
            input.add(finalObj);
        }
    }

    @Override
    public int getRecipeSize(){ return input.size(); }

    @Override
    public ItemStack getRecipeOutput(){ return output; }

    @Override
    public ItemStack getCraftingResult(final InventoryCrafting var1){ return output.copy(); }

    @Override
    public boolean matches(final InventoryCrafting var1, final World world)
    {
        final ArrayList required = new ArrayList(input);

        for (int x = 0; x < var1.getSizeInventory(); x++)
        {
            final ItemStack slot = var1.getStackInSlot(x);

            if (slot != null)
            {
                boolean inRecipe = false;
                final Iterator req = required.iterator();

                while (req.hasNext())
                {
                    boolean match = false;

                    final Object next = req.next();

                    if (next instanceof ItemStack)
                    {
                        match = checkItemEquals((ItemStack)next, slot);
                    }
                    else if (next instanceof ArrayList)
                    {
                        for (final ItemStack item : (ArrayList<ItemStack>)next)
                        {
                            match = match || checkItemEquals(item, slot);
                        }
                    }

                    if (match)
                    {
                        inRecipe = true;
                        required.remove(next);
                        break;
                    }
                }

                if (!inRecipe)
                {
                    return false;
                }
            }
        }

        return required.isEmpty();
    }

    private boolean checkItemEquals(final ItemStack target, final ItemStack input)
    {
        return (target.itemID == input.itemID && (target.getItemDamage() == OreDictionary.WILDCARD_VALUE || target.getItemDamage() == input.getItemDamage()));
    }

    /**
     * Returns the input for this recipe, any mod accessing this value should never
     * manipulate the values in this array as it will effect the recipe itself.
     * @return The recipes input vales.
     */
    public ArrayList getInput()
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
