package net.minecraftforge.oredict;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.toposort.TopologicalSort;
import cpw.mods.fml.common.toposort.TopologicalSort.DirectedGraph;
import net.minecraft.item.crafting.*;

import java.util.*;

import static net.minecraftforge.oredict.RecipeSorter.Category.*;

public class RecipeSorter implements Comparator<IRecipe>
{
    public enum Category
    {
        UNKNOWN,
        SHAPELESS,
        SHAPED
    };

    private static class SortEntry
    {
        private String name;
        private Class cls;
        private Category cat;
        List<String> before = Lists.newArrayList();
        List<String> after = Lists.newArrayList();

        private SortEntry(final String name, final Class cls, final Category cat, final String deps)
        {
            this.name = name;
            this.cls = cls;
            this.cat = cat;
            parseDepends(deps);
        }

        private void parseDepends(final String deps)
        {
            if (deps.isEmpty()) return;
            for (final String dep : deps.split(" "))
            {
                if (dep.startsWith("before:"))
                {
                    before.add(dep.substring(7));
                }
                else if (dep.startsWith("after:"))
                {
                    after.add(dep.substring(6));
                }
                else
                {
                    throw new IllegalArgumentException("Invalid dependancy: " + dep);
                }
            }
        }

        @Override
        public String toString()
        {
            final StringBuilder buf = new StringBuilder();
            buf.append("RecipeEntry(\"").append(name).append("\", ");
            buf.append(cat.name()).append(", ");
            buf.append(cls ==  null ? "" : cls.getName()).append(")");

            if (!before.isEmpty())
            {
                buf.append(" Before: ").append(Joiner.on(", ").join(before));
            }

            if (!after.isEmpty())
            {
                buf.append(" After: ").append(Joiner.on(", ").join(after));
            }

            return buf.toString();
        }

        @Override
        public int hashCode()
        {
            return name.hashCode();
        }
    };

    private static Map<Class, Category>     categories = Maps.newHashMap();
    private static Map<String, Class>       types = Maps.newHashMap();
    private static Map<String, SortEntry>   entries = Maps.newHashMap();
    private static Map<Class, Integer>      priorities = Maps.newHashMap();

    public static RecipeSorter INSTANCE = new RecipeSorter();
    private static boolean isDirty = true;

    private static SortEntry before = new SortEntry("Before", null, UNKNOWN, "");
    private static SortEntry after  = new SortEntry("After",  null, UNKNOWN, "");

    private RecipeSorter()
    {
        register("minecraft:shaped",       ShapedRecipes.class,       SHAPED,    "before:minecraft:shapeless");
        register("minecraft:mapextending", RecipesMapExtending.class, SHAPED,    "after:minecraft:shaped before:minecraft:shapeless");
        register("minecraft:shapeless",    ShapelessRecipes.class,    SHAPELESS, "after:minecraft:shaped");
        register("minecraft:fireworks",    RecipeFireworks.class,     SHAPELESS, "after:minecraft:shapeless");
        register("minecraft:armordyes",    RecipesArmorDyes.class,    SHAPELESS, "after:minecraft:shapeless");
        register("minecraft:mapcloning",   RecipesMapCloning.class,   SHAPELESS, "after:minecraft:shapeless");

        register("forge:shapedore",     ShapedOreRecipe.class,    SHAPED,    "after:minecraft:shaped before:minecraft:shapeless");
        register("forge:shapelessore",  ShapelessOreRecipe.class, SHAPELESS, "after:minecraft:shapeless");
    }
    
    @Override
    public int compare(final IRecipe r1, final IRecipe r2)
    {
        final Category c1 = getCategory(r1);
        final Category c2 = getCategory(r2);
        if (c1 == SHAPELESS && c2 == SHAPED) return  1;
        if (c1 == SHAPED && c2 == SHAPELESS) return -1;
        if (r2.getRecipeSize() < r1.getRecipeSize()) return -1;
        if (r2.getRecipeSize() > r1.getRecipeSize()) return  1;
        return getPriority(r2) - getPriority(r1); // high priority value first! 
    }

    private static Set<Class> warned = Sets.newHashSet();
    public static void sortCraftManager()
    {
        bake();
        FMLLog.fine("Sorting recipies");
        warned.clear();
        Collections.sort(CraftingManager.getInstance().getRecipeList(), INSTANCE);
    }
    
    public static void register(final String name, final Class recipe, final Category category, final String dependancies)
    {
        assert(category != UNKNOWN) : "Category must not be unknown!";
        isDirty = true;

        final SortEntry entry = new SortEntry(name, recipe, category, dependancies);
        entries.put(name, entry);
        setCategory(recipe, category);
    }

    public static void setCategory(final Class recipe, final Category category)
    {
        assert(category != UNKNOWN) : "Category must not be unknown!";
        categories.put(recipe, category);
    }

    public static Category getCategory(final IRecipe recipe)
    {
        return getCategory(recipe.getClass());
    }

    public static Category getCategory(final Class recipe)
    {
        Class cls = recipe;
        Category ret = categories.get(cls);

        if (ret == null)
        {
            cls = cls.getSuperclass();
            while (cls != Object.class)
            {
                ret = categories.get(cls);
                if (ret != null)
                {
                    categories.put(recipe, ret);
                    return ret;
                }
            }
        }

        return ret == null ? UNKNOWN : ret;
    }

    private static int getPriority(final IRecipe recipe)
    {
        Class cls = recipe.getClass();
        Integer ret = priorities.get(cls);

        if (ret == null)
        {
            if (!INSTANCE.warned.contains(cls))
            {
                FMLLog.fine("  Unknown recipe class! %s Modder please refer to %s", cls.getName(), RecipeSorter.class.getName());
                warned.add(cls);
            }
            cls = cls.getSuperclass();
            while (cls != Object.class)
            {
                ret = priorities.get(cls);
                if (ret != null)
                {
                    priorities.put(recipe.getClass(), ret);
                    FMLLog.fine("    Parent Found: %d - %s", ret.intValue(), cls.getName());
                    return ret.intValue();
                }
            }
        }

        return ret == null ? 0 : ret.intValue();
    }

    private static void bake()
    {
        if (!isDirty) return;
        FMLLog.fine("Forge RecipeSorter Baking:");
        final DirectedGraph<SortEntry> sorter = new DirectedGraph<SortEntry>();
        sorter.addNode(before);
        sorter.addNode(after);
        sorter.addEdge(before, after);

        for (final Map.Entry<String, SortEntry> entry : entries.entrySet())
        {
            sorter.addNode(entry.getValue());
        }

        for (final Map.Entry<String, SortEntry> e : entries.entrySet())
        {
            final SortEntry entry = e.getValue();
            boolean postAdded = false;

            sorter.addEdge(before, entry);
            for (final String dep : entry.after)
            {
                if (entries.containsKey(dep))
                {
                    sorter.addEdge(entries.get(dep), entry);
                }
            }

            for (final String dep : entry.before)
            {
                postAdded = true;
                sorter.addEdge(entry, after);
                if (entries.containsKey(dep))
                {
                    sorter.addEdge(entry, entries.get(dep));
                }
            }

            if (!postAdded)
            {
                sorter.addEdge(entry, after);
            }
        }


        final List<SortEntry> sorted = TopologicalSort.topologicalSort(sorter);
        int x = sorted.size();
        for (final SortEntry entry : sorted)
        {
            FMLLog.fine("  %d: %s", x, entry);
            priorities.put(entry.cls, x--);
        }
    }
}
