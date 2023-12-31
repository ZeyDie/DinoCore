package net.minecraft.command;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class CommandEnchant extends CommandBase
{
    public String getCommandName()
    {
        return "enchant";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(final ICommandSender par1ICommandSender)
    {
        return "commands.enchant.usage";
    }

    public void processCommand(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length < 2)
        {
            throw new WrongUsageException("commands.enchant.usage", new Object[0]);
        }
        else
        {
            final EntityPlayerMP entityplayermp = getPlayer(par1ICommandSender, par2ArrayOfStr[0]);
            final int i = parseIntBounded(par1ICommandSender, par2ArrayOfStr[1], 0, Enchantment.enchantmentsList.length - 1);
            int j = 1;
            final ItemStack itemstack = entityplayermp.getCurrentEquippedItem();

            if (itemstack == null)
            {
                throw new CommandException("commands.enchant.noItem", new Object[0]);
            }
            else
            {
                final Enchantment enchantment = Enchantment.enchantmentsList[i];

                if (enchantment == null)
                {
                    throw new NumberInvalidException("commands.enchant.notFound", new Object[] {Integer.valueOf(i)});
                }
                else if (!enchantment.canApply(itemstack))
                {
                    throw new CommandException("commands.enchant.cantEnchant", new Object[0]);
                }
                else
                {
                    if (par2ArrayOfStr.length >= 3)
                    {
                        j = parseIntBounded(par1ICommandSender, par2ArrayOfStr[2], enchantment.getMinLevel(), enchantment.getMaxLevel());
                    }

                    if (itemstack.hasTagCompound())
                    {
                        final NBTTagList nbttaglist = itemstack.getEnchantmentTagList();

                        if (nbttaglist != null)
                        {
                            for (int k = 0; k < nbttaglist.tagCount(); ++k)
                            {
                                final short short1 = ((NBTTagCompound)nbttaglist.tagAt(k)).getShort("id");

                                if (Enchantment.enchantmentsList[short1] != null)
                                {
                                    final Enchantment enchantment1 = Enchantment.enchantmentsList[short1];

                                    if (!enchantment1.canApplyTogether(enchantment))
                                    {
                                        throw new CommandException("commands.enchant.cantCombine", new Object[] {enchantment.getTranslatedName(j), enchantment1.getTranslatedName(((NBTTagCompound)nbttaglist.tagAt(k)).getShort("lvl"))});
                                    }
                                }
                            }
                        }
                    }

                    itemstack.addEnchantment(enchantment, j);
                    notifyAdmins(par1ICommandSender, "commands.enchant.success", new Object[0]);
                }
            }
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, this.getListOfPlayers()) : null;
    }

    protected String[] getListOfPlayers()
    {
        return MinecraftServer.getServer().getAllUsernames();
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(final String[] par1ArrayOfStr, final int par2)
    {
        return par2 == 0;
    }
}
