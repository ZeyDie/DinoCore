package net.minecraft.client.gui.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class GuiContainerCreative extends InventoryEffectRenderer
{
    private static final ResourceLocation field_110424_t = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
    private static InventoryBasic inventory = new InventoryBasic("tmp", true, 45);

    /** Currently selected creative inventory tab index. */
    private static int selectedTabIndex = CreativeTabs.tabBlock.getTabIndex();

    /** Amount scrolled in Creative mode inventory (0 = top, 1 = bottom) */
    private float currentScroll;

    /** True if the scrollbar is being dragged */
    private boolean isScrolling;

    /**
     * True if the left mouse button was held down last time drawScreen was called.
     */
    private boolean wasClicking;
    private GuiTextField searchField;

    /**
     * Used to back up the ContainerCreative's inventory slots before filling it with the player's inventory slots for
     * the inventory tab.
     */
    private List backupContainerSlots;
    private Slot field_74235_v;
    private boolean field_74234_w;
    private CreativeCrafting field_82324_x;
    private static int tabPage = 0;
    private int maxPages = 0;

    public GuiContainerCreative(final EntityPlayer par1EntityPlayer)
    {
        super(new ContainerCreative(par1EntityPlayer));
        par1EntityPlayer.openContainer = this.inventorySlots;
        this.allowUserInput = true;
        par1EntityPlayer.addStat(AchievementList.openInventory, 1);
        this.ySize = 136;
        this.xSize = 195;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        if (!this.mc.playerController.isInCreativeMode())
        {
            this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
        }
    }

    protected void handleMouseClick(final Slot par1Slot, final int par2, final int par3, int par4)
    {
        this.field_74234_w = true;
        final boolean flag = par4 == 1;
        int par41 = par2 == -999 && par4 == 0 ? 4 : par4;
        ItemStack itemstack;
        final InventoryPlayer inventoryplayer;

        if (par1Slot == null && selectedTabIndex != CreativeTabs.tabInventory.getTabIndex() && par41 != 5)
        {
            inventoryplayer = this.mc.thePlayer.inventory;

            if (inventoryplayer.getItemStack() != null)
            {
                if (par3 == 0)
                {
                    this.mc.thePlayer.dropPlayerItem(inventoryplayer.getItemStack());
                    this.mc.playerController.func_78752_a(inventoryplayer.getItemStack());
                    inventoryplayer.setItemStack((ItemStack)null);
                }

                if (par3 == 1)
                {
                    itemstack = inventoryplayer.getItemStack().splitStack(1);
                    this.mc.thePlayer.dropPlayerItem(itemstack);
                    this.mc.playerController.func_78752_a(itemstack);

                    if (inventoryplayer.getItemStack().stackSize == 0)
                    {
                        inventoryplayer.setItemStack((ItemStack)null);
                    }
                }
            }
        }
        else
        {
            int l;

            if (par1Slot == this.field_74235_v && flag)
            {
                for (l = 0; l < this.mc.thePlayer.inventoryContainer.getInventory().size(); ++l)
                {
                    this.mc.playerController.sendSlotPacket((ItemStack)null, l);
                }
            }
            else
            {
                final ItemStack itemstack1;

                if (selectedTabIndex == CreativeTabs.tabInventory.getTabIndex())
                {
                    if (par1Slot == this.field_74235_v)
                    {
                        this.mc.thePlayer.inventory.setItemStack((ItemStack)null);
                    }
                    else if (par41 == 4 && par1Slot != null && par1Slot.getHasStack())
                    {
                        itemstack1 = par1Slot.decrStackSize(par3 == 0 ? 1 : par1Slot.getStack().getMaxStackSize());
                        this.mc.thePlayer.dropPlayerItem(itemstack1);
                        this.mc.playerController.func_78752_a(itemstack1);
                    }
                    else if (par41 == 4 && this.mc.thePlayer.inventory.getItemStack() != null)
                    {
                        this.mc.thePlayer.dropPlayerItem(this.mc.thePlayer.inventory.getItemStack());
                        this.mc.playerController.func_78752_a(this.mc.thePlayer.inventory.getItemStack());
                        this.mc.thePlayer.inventory.setItemStack((ItemStack)null);
                    }
                    else
                    {
                        this.mc.thePlayer.inventoryContainer.slotClick(par1Slot == null ? par2 : SlotCreativeInventory.func_75240_a((SlotCreativeInventory)par1Slot).slotNumber, par3, par41, this.mc.thePlayer);
                        this.mc.thePlayer.inventoryContainer.detectAndSendChanges();
                    }
                }
                else if (par41 != 5 && par1Slot.inventory == inventory)
                {
                    inventoryplayer = this.mc.thePlayer.inventory;
                    itemstack = inventoryplayer.getItemStack();
                    final ItemStack itemstack2 = par1Slot.getStack();
                    final ItemStack itemstack3;

                    if (par41 == 2)
                    {
                        if (itemstack2 != null && par3 >= 0 && par3 < 9)
                        {
                            itemstack3 = itemstack2.copy();
                            itemstack3.stackSize = itemstack3.getMaxStackSize();
                            this.mc.thePlayer.inventory.setInventorySlotContents(par3, itemstack3);
                            this.mc.thePlayer.inventoryContainer.detectAndSendChanges();
                        }

                        return;
                    }

                    if (par41 == 3)
                    {
                        if (inventoryplayer.getItemStack() == null && par1Slot.getHasStack())
                        {
                            itemstack3 = par1Slot.getStack().copy();
                            itemstack3.stackSize = itemstack3.getMaxStackSize();
                            inventoryplayer.setItemStack(itemstack3);
                        }

                        return;
                    }

                    if (par41 == 4)
                    {
                        if (itemstack2 != null)
                        {
                            itemstack3 = itemstack2.copy();
                            itemstack3.stackSize = par3 == 0 ? 1 : itemstack3.getMaxStackSize();
                            this.mc.thePlayer.dropPlayerItem(itemstack3);
                            this.mc.playerController.func_78752_a(itemstack3);
                        }

                        return;
                    }

                    if (itemstack != null && itemstack2 != null && itemstack.isItemEqual(itemstack2) && ItemStack.areItemStackTagsEqual(itemstack, itemstack2)) //Forge: Bugfix, Compare NBT data, allow for deletion of enchanted books, MC-12770
                    {
                        if (par3 == 0)
                        {
                            if (flag)
                            {
                                itemstack.stackSize = itemstack.getMaxStackSize();
                            }
                            else if (itemstack.stackSize < itemstack.getMaxStackSize())
                            {
                                ++itemstack.stackSize;
                            }
                        }
                        else if (itemstack.stackSize <= 1)
                        {
                            inventoryplayer.setItemStack((ItemStack)null);
                        }
                        else
                        {
                            --itemstack.stackSize;
                        }
                    }
                    else if (itemstack2 != null && itemstack == null)
                    {
                        inventoryplayer.setItemStack(ItemStack.copyItemStack(itemstack2));
                        itemstack = inventoryplayer.getItemStack();

                        if (flag)
                        {
                            itemstack.stackSize = itemstack.getMaxStackSize();
                        }
                    }
                    else
                    {
                        inventoryplayer.setItemStack((ItemStack)null);
                    }
                }
                else
                {
                    this.inventorySlots.slotClick(par1Slot == null ? par2 : par1Slot.slotNumber, par3, par41, this.mc.thePlayer);

                    if (Container.func_94532_c(par3) == 2)
                    {
                        for (l = 0; l < 9; ++l)
                        {
                            this.mc.playerController.sendSlotPacket(this.inventorySlots.getSlot(45 + l).getStack(), 36 + l);
                        }
                    }
                    else if (par1Slot != null)
                    {
                        itemstack1 = this.inventorySlots.getSlot(par1Slot.slotNumber).getStack();
                        this.mc.playerController.sendSlotPacket(itemstack1, par1Slot.slotNumber - this.inventorySlots.inventorySlots.size() + 9 + 36);
                    }
                }
            }
        }
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        if (this.mc.playerController.isInCreativeMode())
        {
            super.initGui();
            this.buttonList.clear();
            Keyboard.enableRepeatEvents(true);
            this.searchField = new GuiTextField(this.fontRenderer, this.guiLeft + 82, this.guiTop + 6, 89, this.fontRenderer.FONT_HEIGHT);
            this.searchField.setMaxStringLength(15);
            this.searchField.setEnableBackgroundDrawing(false);
            this.searchField.setVisible(false);
            this.searchField.setTextColor(16777215);
            final int i = selectedTabIndex;
            selectedTabIndex = -1;
            this.setCurrentCreativeTab(CreativeTabs.creativeTabArray[i]);
            this.field_82324_x = new CreativeCrafting(this.mc);
            this.mc.thePlayer.inventoryContainer.addCraftingToCrafters(this.field_82324_x);
            final int tabCount = CreativeTabs.creativeTabArray.length;
            if (tabCount > 12)
            {
                buttonList.add(new GuiButton(101, guiLeft,              guiTop - 50, 20, 20, "<"));
                buttonList.add(new GuiButton(102, guiLeft + xSize - 20, guiTop - 50, 20, 20, ">"));
                maxPages = ((tabCount - 12) / 10) + 1;
            }
        }
        else
        {
            this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
        }
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        super.onGuiClosed();

        if (this.mc.thePlayer != null && this.mc.thePlayer.inventory != null)
        {
            this.mc.thePlayer.inventoryContainer.removeCraftingFromCrafters(this.field_82324_x);
        }

        Keyboard.enableRepeatEvents(false);
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(final char par1, final int par2)
    {
        if (!CreativeTabs.creativeTabArray[selectedTabIndex].hasSearchBar())
        {
            if (GameSettings.isKeyDown(this.mc.gameSettings.keyBindChat))
            {
                this.setCurrentCreativeTab(CreativeTabs.tabAllSearch);
            }
            else
            {
                super.keyTyped(par1, par2);
            }
        }
        else
        {
            if (this.field_74234_w)
            {
                this.field_74234_w = false;
                this.searchField.setText("");
            }

            if (!this.checkHotbarKeys(par2))
            {
                if (this.searchField.textboxKeyTyped(par1, par2))
                {
                    this.updateCreativeSearch();
                }
                else
                {
                    super.keyTyped(par1, par2);
                }
            }
        }
    }

    private void updateCreativeSearch()
    {
        final ContainerCreative containercreative = (ContainerCreative)this.inventorySlots;
        containercreative.itemList.clear();

        final CreativeTabs tab = CreativeTabs.creativeTabArray[selectedTabIndex];
        if (tab.hasSearchBar() && tab != CreativeTabs.tabAllSearch)
        {
            tab.displayAllReleventItems(containercreative.itemList);
            updateFilteredItems(containercreative);
            return;
        }

        final Item[] aitem = Item.itemsList;
        int i = aitem.length;
        int j;

        for (j = 0; j < i; ++j)
        {
            final Item item = aitem[j];

            if (item != null && item.getCreativeTab() != null)
            {
                item.getSubItems(item.itemID, (CreativeTabs)null, containercreative.itemList);
            }
        }

        final Enchantment[] aenchantment = Enchantment.enchantmentsList;
        i = aenchantment.length;

        for (j = 0; j < i; ++j)
        {
            final Enchantment enchantment = aenchantment[j];

            if (enchantment != null && enchantment.type != null)
            {
                Item.enchantedBook.func_92113_a(enchantment, containercreative.itemList);
            }
        }
        updateFilteredItems(containercreative);
    }

    //split from above for custom search tabs
    private void updateFilteredItems(final ContainerCreative containercreative)
    {
        final Iterator iterator = containercreative.itemList.iterator();
        final String s = this.searchField.getText().toLowerCase();

        while (iterator.hasNext())
        {
            final ItemStack itemstack = (ItemStack)iterator.next();
            boolean flag = false;
            final Iterator iterator1 = itemstack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips).iterator();

            while (true)
            {
                if (iterator1.hasNext())
                {
                    final String s1 = (String)iterator1.next();

                    if (!s1.toLowerCase().contains(s))
                    {
                        continue;
                    }

                    flag = true;
                }

                if (!flag)
                {
                    iterator.remove();
                }

                break;
            }
        }

        this.currentScroll = 0.0F;
        containercreative.scrollTo(0.0F);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(final int par1, final int par2)
    {
        final CreativeTabs creativetabs = CreativeTabs.creativeTabArray[selectedTabIndex];

        if (creativetabs != null && creativetabs.drawInForegroundOfTab())
        {
            this.fontRenderer.drawString(I18n.getString(creativetabs.getTranslatedTabLabel()), 8, 6, 4210752);
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(final int par1, final int par2, final int par3)
    {
        if (par3 == 0)
        {
            final int l = par1 - this.guiLeft;
            final int i1 = par2 - this.guiTop;
            final CreativeTabs[] acreativetabs = CreativeTabs.creativeTabArray;
            final int j1 = acreativetabs.length;

            for (int k1 = 0; k1 < j1; ++k1)
            {
                final CreativeTabs creativetabs = acreativetabs[k1];

                if (this.func_74232_a(creativetabs, l, i1))
                {
                    return;
                }
            }
        }

        super.mouseClicked(par1, par2, par3);
    }

    /**
     * Called when the mouse is moved or a mouse button is released.  Signature: (mouseX, mouseY, which) which==-1 is
     * mouseMove, which==0 or which==1 is mouseUp
     */
    protected void mouseMovedOrUp(final int par1, final int par2, final int par3)
    {
        if (par3 == 0)
        {
            final int l = par1 - this.guiLeft;
            final int i1 = par2 - this.guiTop;
            final CreativeTabs[] acreativetabs = CreativeTabs.creativeTabArray;
            final int j1 = acreativetabs.length;

            for (int k1 = 0; k1 < j1; ++k1)
            {
                final CreativeTabs creativetabs = acreativetabs[k1];

                if (creativetabs != null && func_74232_a(creativetabs, l, i1))
                {
                    this.setCurrentCreativeTab(creativetabs);
                    return;
                }
            }
        }

        super.mouseMovedOrUp(par1, par2, par3);
    }

    /**
     * returns (if you are not on the inventoryTab) and (the flag isn't set) and( you have more than 1 page of items)
     */
    private boolean needsScrollBars()
    {
        if (CreativeTabs.creativeTabArray[selectedTabIndex] == null) return false;
        return selectedTabIndex != CreativeTabs.tabInventory.getTabIndex() && CreativeTabs.creativeTabArray[selectedTabIndex].shouldHidePlayerInventory() && ((ContainerCreative)this.inventorySlots).hasMoreThan1PageOfItemsInList();
    }

    private void setCurrentCreativeTab(final CreativeTabs par1CreativeTabs)
    {
        if (par1CreativeTabs == null)
        {
            return;
        }

        final int i = selectedTabIndex;
        selectedTabIndex = par1CreativeTabs.getTabIndex();
        final ContainerCreative containercreative = (ContainerCreative)this.inventorySlots;
        this.field_94077_p.clear();
        containercreative.itemList.clear();
        par1CreativeTabs.displayAllReleventItems(containercreative.itemList);

        if (par1CreativeTabs == CreativeTabs.tabInventory)
        {
            final Container container = this.mc.thePlayer.inventoryContainer;

            if (this.backupContainerSlots == null)
            {
                this.backupContainerSlots = containercreative.inventorySlots;
            }

            containercreative.inventorySlots = new ArrayList();

            for (int j = 0; j < container.inventorySlots.size(); ++j)
            {
                final SlotCreativeInventory slotcreativeinventory = new SlotCreativeInventory(this, (Slot)container.inventorySlots.get(j), j);
                containercreative.inventorySlots.add(slotcreativeinventory);
                final int k;
                final int l;
                final int i1;

                if (j >= 5 && j < 9)
                {
                    k = j - 5;
                    l = k / 2;
                    i1 = k % 2;
                    slotcreativeinventory.xDisplayPosition = 9 + l * 54;
                    slotcreativeinventory.yDisplayPosition = 6 + i1 * 27;
                }
                else if (j >= 0 && j < 5)
                {
                    slotcreativeinventory.yDisplayPosition = -2000;
                    slotcreativeinventory.xDisplayPosition = -2000;
                }
                else if (j < container.inventorySlots.size())
                {
                    k = j - 9;
                    l = k % 9;
                    i1 = k / 9;
                    slotcreativeinventory.xDisplayPosition = 9 + l * 18;

                    if (j >= 36)
                    {
                        slotcreativeinventory.yDisplayPosition = 112;
                    }
                    else
                    {
                        slotcreativeinventory.yDisplayPosition = 54 + i1 * 18;
                    }
                }
            }

            this.field_74235_v = new Slot(inventory, 0, 173, 112);
            containercreative.inventorySlots.add(this.field_74235_v);
        }
        else if (i == CreativeTabs.tabInventory.getTabIndex())
        {
            containercreative.inventorySlots = this.backupContainerSlots;
            this.backupContainerSlots = null;
        }

        if (this.searchField != null)
        {
            if (par1CreativeTabs.hasSearchBar())
            {
                this.searchField.setVisible(true);
                this.searchField.setCanLoseFocus(false);
                this.searchField.setFocused(true);
                this.searchField.setText("");
                this.updateCreativeSearch();
            }
            else
            {
                this.searchField.setVisible(false);
                this.searchField.setCanLoseFocus(true);
                this.searchField.setFocused(false);
            }
        }

        this.currentScroll = 0.0F;
        containercreative.scrollTo(0.0F);
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput()
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();

        if (i != 0 && this.needsScrollBars())
        {
            final int j = ((ContainerCreative)this.inventorySlots).itemList.size() / 9 - 5 + 1;

            if (i > 0)
            {
                i = 1;
            }

            if (i < 0)
            {
                i = -1;
            }

            this.currentScroll = (float)((double)this.currentScroll - (double)i / (double)j);

            if (this.currentScroll < 0.0F)
            {
                this.currentScroll = 0.0F;
            }

            if (this.currentScroll > 1.0F)
            {
                this.currentScroll = 1.0F;
            }

            ((ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(final int par1, final int par2, final float par3)
    {
        final boolean flag = Mouse.isButtonDown(0);
        final int k = this.guiLeft;
        final int l = this.guiTop;
        final int i1 = k + 175;
        final int j1 = l + 18;
        final int k1 = i1 + 14;
        final int l1 = j1 + 112;

        if (!this.wasClicking && flag && par1 >= i1 && par2 >= j1 && par1 < k1 && par2 < l1)
        {
            this.isScrolling = this.needsScrollBars();
        }

        if (!flag)
        {
            this.isScrolling = false;
        }

        this.wasClicking = flag;

        if (this.isScrolling)
        {
            this.currentScroll = ((float)(par2 - j1) - 7.5F) / ((float)(l1 - j1) - 15.0F);

            if (this.currentScroll < 0.0F)
            {
                this.currentScroll = 0.0F;
            }

            if (this.currentScroll > 1.0F)
            {
                this.currentScroll = 1.0F;
            }

            ((ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
        }

        super.drawScreen(par1, par2, par3);
        final CreativeTabs[] acreativetabs = CreativeTabs.creativeTabArray;
        int start = tabPage * 10;
        final int i2 = Math.min(acreativetabs.length, ((tabPage + 1) * 10) + 2);
        if (tabPage != 0) start += 2;
        boolean rendered = false;

        for (int j2 = start; j2 < i2; ++j2)
        {
            final CreativeTabs creativetabs = acreativetabs[j2];

            if (creativetabs != null && this.renderCreativeInventoryHoveringText(creativetabs, par1, par2))
            {
                rendered = true;
                break;
            }
        }

        if (!rendered && !renderCreativeInventoryHoveringText(CreativeTabs.tabAllSearch, par1, par2))
        {
            renderCreativeInventoryHoveringText(CreativeTabs.tabInventory, par1, par2);
        }

        if (this.field_74235_v != null && selectedTabIndex == CreativeTabs.tabInventory.getTabIndex() && this.isPointInRegion(this.field_74235_v.xDisplayPosition, this.field_74235_v.yDisplayPosition, 16, 16, par1, par2))
        {
            this.drawCreativeTabHoveringText(I18n.getString("inventory.binSlot"), par1, par2);
        }

        if (maxPages != 0)
        {
            final String page = String.format("%d / %d", tabPage + 1, maxPages + 1);
            final int width = fontRenderer.getStringWidth(page);
            GL11.glDisable(GL11.GL_LIGHTING);
            this.zLevel = 300.0F;
            itemRenderer.zLevel = 300.0F;
            fontRenderer.drawString(page, guiLeft + (xSize / 2) - (width / 2), guiTop - 44, -1);
            this.zLevel = 0.0F;
            itemRenderer.zLevel = 0.0F;
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    protected void drawItemStackTooltip(final ItemStack par1ItemStack, final int par2, final int par3)
    {
        if (selectedTabIndex == CreativeTabs.tabAllSearch.getTabIndex())
        {
            final List list = par1ItemStack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);
            CreativeTabs creativetabs = par1ItemStack.getItem().getCreativeTab();

            if (creativetabs == null && par1ItemStack.itemID == Item.enchantedBook.itemID)
            {
                final Map map = EnchantmentHelper.getEnchantments(par1ItemStack);

                if (map.size() == 1)
                {
                    final Enchantment enchantment = Enchantment.enchantmentsList[((Integer)map.keySet().iterator().next()).intValue()];
                    final CreativeTabs[] acreativetabs = CreativeTabs.creativeTabArray;
                    final int k = acreativetabs.length;

                    for (int l = 0; l < k; ++l)
                    {
                        final CreativeTabs creativetabs1 = acreativetabs[l];

                        if (creativetabs1.func_111226_a(enchantment.type))
                        {
                            creativetabs = creativetabs1;
                            break;
                        }
                    }
                }
            }

            if (creativetabs != null)
            {
                list.add(1, "" + EnumChatFormatting.BOLD + EnumChatFormatting.BLUE + I18n.getString(creativetabs.getTranslatedTabLabel()));
            }

            for (int i1 = 0; i1 < list.size(); ++i1)
            {
                if (i1 == 0)
                {
                    list.set(i1, "\u00a7" + Integer.toHexString(par1ItemStack.getRarity().rarityColor) + (String)list.get(i1));
                }
                else
                {
                    list.set(i1, EnumChatFormatting.GRAY + (String)list.get(i1));
                }
            }

            this.func_102021_a(list, par2, par3);
        }
        else
        {
            super.drawItemStackTooltip(par1ItemStack, par2, par3);
        }
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.enableGUIStandardItemLighting();
        final CreativeTabs creativetabs = CreativeTabs.creativeTabArray[selectedTabIndex];
        final CreativeTabs[] acreativetabs = CreativeTabs.creativeTabArray;
        int k = acreativetabs.length;
        int l;

        int start = tabPage * 10;
        k = Math.min(acreativetabs.length, ((tabPage + 1) * 10 + 2));
        if (tabPage != 0) start += 2;

        for (l = start; l < k; ++l)
        {
            final CreativeTabs creativetabs1 = acreativetabs[l];
            this.mc.getTextureManager().bindTexture(field_110424_t);

            if (creativetabs1 != null && creativetabs1.getTabIndex() != selectedTabIndex)
            {
                this.renderCreativeTab(creativetabs1);
            }
        }

        if (tabPage != 0)
        {
            if (creativetabs != CreativeTabs.tabAllSearch)
            {
                this.mc.getTextureManager().bindTexture(field_110424_t);
                renderCreativeTab(CreativeTabs.tabAllSearch);
            }
            if (creativetabs != CreativeTabs.tabInventory)
            {
                this.mc.getTextureManager().bindTexture(field_110424_t);
                renderCreativeTab(CreativeTabs.tabInventory);
            }
        }

        this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/creative_inventory/tab_" + creativetabs.getBackgroundImageName()));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.searchField.drawTextBox();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        final int i1 = this.guiLeft + 175;
        k = this.guiTop + 18;
        l = k + 112;
        this.mc.getTextureManager().bindTexture(field_110424_t);

        if (creativetabs.shouldHidePlayerInventory())
        {
            this.drawTexturedModalRect(i1, k + (int)((float)(l - k - 17) * this.currentScroll), 232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
        }

        if (creativetabs == null || creativetabs.getTabPage() != tabPage)
        {
            if (creativetabs != CreativeTabs.tabAllSearch && creativetabs != CreativeTabs.tabInventory)
            {
                return;
            }
        }

        this.renderCreativeTab(creativetabs);

        if (creativetabs == CreativeTabs.tabInventory)
        {
            GuiInventory.func_110423_a(this.guiLeft + 43, this.guiTop + 45, 20, (float)(this.guiLeft + 43 - par2), (float)(this.guiTop + 45 - 30 - par3), this.mc.thePlayer);
        }
    }

    protected boolean func_74232_a(final CreativeTabs par1CreativeTabs, final int par2, final int par3)
    {
        if (par1CreativeTabs.getTabPage() != tabPage)
        {
            if (par1CreativeTabs != CreativeTabs.tabAllSearch &&
                par1CreativeTabs != CreativeTabs.tabInventory)
            {
                return false;
            }
        }

        final int k = par1CreativeTabs.getTabColumn();
        int l = 28 * k;
        final byte b0 = 0;

        if (k == 5)
        {
            l = this.xSize - 28 + 2;
        }
        else if (k > 0)
        {
            l += k;
        }

        final int i1;

        if (par1CreativeTabs.isTabInFirstRow())
        {
            i1 = b0 - 32;
        }
        else
        {
            i1 = b0 + this.ySize;
        }

        return par2 >= l && par2 <= l + 28 && par3 >= i1 && par3 <= i1 + 32;
    }

    /**
     * Renders the creative inventory hovering text if mouse is over it. Returns true if did render or false otherwise.
     * Params: current creative tab to be checked, current mouse x position, current mouse y position.
     */
    protected boolean renderCreativeInventoryHoveringText(final CreativeTabs par1CreativeTabs, final int par2, final int par3)
    {
        final int k = par1CreativeTabs.getTabColumn();
        int l = 28 * k;
        final byte b0 = 0;

        if (k == 5)
        {
            l = this.xSize - 28 + 2;
        }
        else if (k > 0)
        {
            l += k;
        }

        final int i1;

        if (par1CreativeTabs.isTabInFirstRow())
        {
            i1 = b0 - 32;
        }
        else
        {
            i1 = b0 + this.ySize;
        }

        if (this.isPointInRegion(l + 3, i1 + 3, 23, 27, par2, par3))
        {
            this.drawCreativeTabHoveringText(I18n.getString(par1CreativeTabs.getTranslatedTabLabel()), par2, par3);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Renders passed creative inventory tab into the screen.
     */
    protected void renderCreativeTab(final CreativeTabs par1CreativeTabs)
    {
        final boolean flag = par1CreativeTabs.getTabIndex() == selectedTabIndex;
        final boolean flag1 = par1CreativeTabs.isTabInFirstRow();
        final int i = par1CreativeTabs.getTabColumn();
        final int j = i * 28;
        int k = 0;
        int l = this.guiLeft + 28 * i;
        int i1 = this.guiTop;
        final byte b0 = 32;

        if (flag)
        {
            k += 32;
        }

        if (i == 5)
        {
            l = this.guiLeft + this.xSize - 28;
        }
        else if (i > 0)
        {
            l += i;
        }

        if (flag1)
        {
            i1 -= 28;
        }
        else
        {
            k += 64;
            i1 += this.ySize - 4;
        }

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glColor3f(1.0F, 1.0F, 1.0F); //Forge: Reset color in case Items change it.
        this.drawTexturedModalRect(l, i1, j, k, 28, b0);
        this.zLevel = 100.0F;
        itemRenderer.zLevel = 100.0F;
        l += 6;
        i1 += 8 + (flag1 ? 1 : -1);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        final ItemStack itemstack = par1CreativeTabs.getIconItemStack();
        itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.getTextureManager(), itemstack, l, i1);
        itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.getTextureManager(), itemstack, l, i1);
        GL11.glDisable(GL11.GL_LIGHTING);
        itemRenderer.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(final GuiButton par1GuiButton)
    {
        if (par1GuiButton.id == 0)
        {
            this.mc.displayGuiScreen(new GuiAchievements(this.mc.statFileWriter));
        }

        if (par1GuiButton.id == 1)
        {
            this.mc.displayGuiScreen(new GuiStats(this, this.mc.statFileWriter));
        }

        if (par1GuiButton.id == 101)
        {
            tabPage = Math.max(tabPage - 1, 0);
        }
        else if (par1GuiButton.id == 102)
        {
            tabPage = Math.min(tabPage + 1, maxPages);
        }
    }

    /**
     * Returns the current creative tab index.
     */
    public int getCurrentTabIndex()
    {
        return selectedTabIndex;
    }

    /**
     * Returns the creative inventory
     */
    static InventoryBasic getInventory()
    {
        return inventory;
    }
}
