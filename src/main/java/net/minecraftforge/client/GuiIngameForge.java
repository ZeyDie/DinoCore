package net.minecraftforge.client;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.crash.CallableMinecraftVersion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.*;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.*;

public class GuiIngameForge extends GuiIngame
{
    private static final ResourceLocation VIGNETTE     = new ResourceLocation("textures/misc/vignette.png");
    private static final ResourceLocation WIDGITS      = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation PUMPKIN_BLUR = new ResourceLocation("textures/misc/pumpkinblur.png");

    private static final int WHITE = 0xFFFFFF;

    //Flags to toggle the rendering of certain aspects of the HUD, valid conditions
    //must be met for them to render normally. If those conditions are met, but this flag
    //is false, they will not be rendered.
    public static boolean renderHelmet = true;
    public static boolean renderPortal = true;
    public static boolean renderHotbar = true;
    public static boolean renderCrosshairs = true;
    public static boolean renderBossHealth = true;
    public static boolean renderHealth = true;
    public static boolean renderArmor = true;
    public static boolean renderFood = true;
    public static boolean renderHealthMount = true;
    public static boolean renderAir = true;
    public static boolean renderExperiance = true;
    public static boolean renderJumpBar = true;
    public static boolean renderObjective = true;

    public static int left_height = 39;
    public static int right_height = 39;

    private ScaledResolution res = null;
    private FontRenderer fontrenderer = null;
    private RenderGameOverlayEvent eventParent;
    private static final String MC_VERSION = (new CallableMinecraftVersion(null)).minecraftVersion();

    public GuiIngameForge(final Minecraft mc)
    {
        super(mc);
    }

    @Override
    public void renderGameOverlay(final float partialTicks, final boolean hasScreen, final int mouseX, final int mouseY)
    {
        res = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        eventParent = new RenderGameOverlayEvent(partialTicks, res, mouseX, mouseY);
        final int width = res.getScaledWidth();
        final int height = res.getScaledHeight();
        renderHealthMount = mc.thePlayer.ridingEntity instanceof EntityLivingBase;
        renderFood = mc.thePlayer.ridingEntity == null;
        renderJumpBar = mc.thePlayer.isRidingHorse();

        right_height = 39;
        left_height = 39;

        if (pre(ALL)) return;

        fontrenderer = mc.fontRenderer;
        mc.entityRenderer.setupOverlayRendering();
        GL11.glEnable(GL11.GL_BLEND);

        if (Minecraft.isFancyGraphicsEnabled())
        {
            renderVignette(mc.thePlayer.getBrightness(partialTicks), width, height);
        }
        else
        {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }

        if (renderHelmet) renderHelmet(res, partialTicks, hasScreen, mouseX, mouseY);

        if (renderPortal && !mc.thePlayer.isPotionActive(Potion.confusion))
        {
            renderPortal(width, height, partialTicks);
        }

        if (!mc.playerController.enableEverythingIsScrewedUpMode())
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            zLevel = -90.0F;
            rand.setSeed((long)(updateCounter * 312871));

            if (renderCrosshairs) renderCrosshairs(width, height);
            if (renderBossHealth) renderBossHealth();

            if (this.mc.playerController.shouldDrawHUD())
            {
                if (renderHealth) renderHealth(width, height);
                if (renderArmor)  renderArmor(width, height);
                if (renderFood)   renderFood(width, height);
                if (renderHealthMount) renderHealthMount(width, height);
                if (renderAir)    renderAir(width, height);
            }
            if (renderHotbar) renderHotbar(width, height, partialTicks);
        }

        if (renderJumpBar)
        {
            renderJumpBar(width, height);
        }
        else if (renderExperiance)
        {
            renderExperience(width, height);
        }

        renderSleepFade(width, height);
        renderToolHightlight(width, height);
        renderHUDText(width, height);
        renderRecordOverlay(width, height, partialTicks);

        final ScoreObjective objective = mc.theWorld.getScoreboard().func_96539_a(1);
        if (renderObjective && objective != null)
        {
            this.func_96136_a(objective, height, width, fontrenderer);
        }

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);

        renderChat(width, height);

        renderPlayerList(width, height);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        post(ALL);
    }

    public ScaledResolution getResolution()
    {
        return res;
    }

    protected void renderHotbar(final int width, final int height, final float partialTicks)
    {
        if (pre(HOTBAR)) return;
        mc.mcProfiler.startSection("actionBar");

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(WIDGITS);

        final InventoryPlayer inv = mc.thePlayer.inventory;
        drawTexturedModalRect(width / 2 - 91, height - 22, 0, 0, 182, 22);
        drawTexturedModalRect(width / 2 - 91 - 1 + inv.currentItem * 20, height - 22 - 1, 0, 22, 24, 22);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();

        for (int i = 0; i < 9; ++i)
        {
            final int x = width / 2 - 90 + i * 20 + 2;
            final int z = height - 16 - 3;
            renderInventorySlot(i, x, z, partialTicks);
        }

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        mc.mcProfiler.endSection();
        post(HOTBAR);
    }

    protected void renderCrosshairs(final int width, final int height)
    {
        if (pre(CROSSHAIRS)) return;
        bind(Gui.icons);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
        drawTexturedModalRect(width / 2 - 7, height / 2 - 7, 0, 0, 16, 16);
        GL11.glDisable(GL11.GL_BLEND);
        post(CROSSHAIRS);
    }

    @Override
    protected void renderBossHealth()
    {
        if (pre(BOSSHEALTH)) return;
        mc.mcProfiler.startSection("bossHealth");
        super.renderBossHealth();
        mc.mcProfiler.endSection();
        post(BOSSHEALTH);
    }

    private void renderHelmet(final ScaledResolution res, final float partialTicks, final boolean hasScreen, final int mouseX, final int mouseY)
    {
        if (pre(HELMET)) return;

        final ItemStack itemstack = this.mc.thePlayer.inventory.armorItemInSlot(3);

        if (this.mc.gameSettings.thirdPersonView == 0 && itemstack != null && itemstack.getItem() != null)
        {
            if (itemstack.itemID == Block.pumpkin.blockID)
            {
                renderPumpkinBlur(res.getScaledWidth(), res.getScaledHeight());
            }
            else
            {
                itemstack.getItem().renderHelmetOverlay(itemstack, mc.thePlayer, res, partialTicks, hasScreen, mouseX, mouseY);
            }
        }

        post(HELMET);
    }

    protected void renderArmor(final int width, final int height)
    {
        if (pre(ARMOR)) return;
        mc.mcProfiler.startSection("armor");

        int left = width / 2 - 91;
        final int top = height - left_height;

        final int level = ForgeHooks.getTotalArmorValue(mc.thePlayer);
        for (int i = 1; level > 0 && i < 20; i += 2)
        {
            if (i < level)
            {
                drawTexturedModalRect(left, top, 34, 9, 9, 9);
            }
            else if (i == level)
            {
                drawTexturedModalRect(left, top, 25, 9, 9, 9);
            }
            else if (i > level)
            {
                drawTexturedModalRect(left, top, 16, 9, 9, 9);
            }
            left += 8;
        }
        left_height += 10;

        mc.mcProfiler.endSection();
        post(ARMOR);
    }

    protected void renderPortal(final int width, final int height, final float partialTicks)
    {
        if (pre(PORTAL)) return;

        final float f1 = mc.thePlayer.prevTimeInPortal + (mc.thePlayer.timeInPortal - mc.thePlayer.prevTimeInPortal) * partialTicks;

        if (f1 > 0.0F)
        {
            func_130015_b(f1, width, height);
        }

        post(PORTAL);
    }

    protected void renderAir(final int width, final int height)
    {
        if (pre(AIR)) return;
        mc.mcProfiler.startSection("air");
        final int left = width / 2 + 91;
        final int top = height - right_height;

        if (mc.thePlayer.isInsideOfMaterial(Material.water))
        {
            final int air = mc.thePlayer.getAir();
            final int full = MathHelper.ceiling_double_int((double)(air - 2) * 10.0D / 300.0D);
            final int partial = MathHelper.ceiling_double_int((double)air * 10.0D / 300.0D) - full;

            for (int i = 0; i < full + partial; ++i)
            {
                drawTexturedModalRect(left - i * 8 - 9, top, (i < full ? 16 : 25), 18, 9, 9);
            }
            right_height += 10;
        }

        mc.mcProfiler.endSection();
        post(AIR);
    }

    public void renderHealth(final int width, final int height)
    {
        bind(icons);
        if (pre(HEALTH)) return;
        mc.mcProfiler.startSection("health");

        boolean highlight = mc.thePlayer.hurtResistantTime / 3 % 2 == 1;

        if (mc.thePlayer.hurtResistantTime < 10)
        {
            highlight = false;
        }

        final AttributeInstance attrMaxHealth = this.mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        final int health = MathHelper.ceiling_float_int(mc.thePlayer.getHealth());
        final int healthLast = MathHelper.ceiling_float_int(mc.thePlayer.prevHealth);
        final float healthMax = (float)attrMaxHealth.getAttributeValue();
        final float absorb = this.mc.thePlayer.getAbsorptionAmount();

        final int healthRows = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F / 10.0F);
        final int rowHeight = Math.max(10 - (healthRows - 2), 3);

        this.rand.setSeed((long)(updateCounter * 312871));

        final int left = width / 2 - 91;
        final int top = height - left_height;
        left_height += (healthRows * rowHeight);
        if (rowHeight != 10) left_height += 10 - rowHeight;

        int regen = -1;
        if (mc.thePlayer.isPotionActive(Potion.regeneration))
        {
            regen = updateCounter % 25;
        }

        final int TOP =  9 * (mc.theWorld.getWorldInfo().isHardcoreModeEnabled() ? 5 : 0);
        final int BACKGROUND = (highlight ? 25 : 16);
        int MARGIN = 16;
        if (mc.thePlayer.isPotionActive(Potion.poison))      MARGIN += 36;
        else if (mc.thePlayer.isPotionActive(Potion.wither)) MARGIN += 72;
        float absorbRemaining = absorb;

        for (int i = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F) - 1; i >= 0; --i)
        {
            final int b0 = (highlight ? 1 : 0);
            final int row = MathHelper.ceiling_float_int((float)(i + 1) / 10.0F) - 1;
            final int x = left + i % 10 * 8;
            int y = top - row * rowHeight;

            if (health <= 4) y += rand.nextInt(2);
            if (i == regen) y -= 2;

            drawTexturedModalRect(x, y, BACKGROUND, TOP, 9, 9);

            if (highlight)
            {
                if (i * 2 + 1 < healthLast)
                    drawTexturedModalRect(x, y, MARGIN + 54, TOP, 9, 9); //6
                else if (i * 2 + 1 == healthLast)
                    drawTexturedModalRect(x, y, MARGIN + 63, TOP, 9, 9); //7
            }

            if (absorbRemaining > 0.0F)
            {
                if (absorbRemaining == absorb && absorb % 2.0F == 1.0F)
                    drawTexturedModalRect(x, y, MARGIN + 153, TOP, 9, 9); //17
                else
                    drawTexturedModalRect(x, y, MARGIN + 144, TOP, 9, 9); //16
                absorbRemaining -= 2.0F;
            }
            else
            {
                if (i * 2 + 1 < health)
                    drawTexturedModalRect(x, y, MARGIN + 36, TOP, 9, 9); //4
                else if (i * 2 + 1 == health)
                    drawTexturedModalRect(x, y, MARGIN + 45, TOP, 9, 9); //5
            }
        }

        mc.mcProfiler.endSection();
        post(HEALTH);
    }

    public void renderFood(final int width, final int height)
    {
        if (pre(FOOD)) return;
        mc.mcProfiler.startSection("food");

        final int left = width / 2 + 91;
        final int top = height - right_height;
        right_height += 10;
        final boolean unused = false;// Unused flag in vanilla, seems to be part of a 'fade out' mechanic

        final FoodStats stats = mc.thePlayer.getFoodStats();
        final int level = stats.getFoodLevel();
        final int levelLast = stats.getPrevFoodLevel();

        for (int i = 0; i < 10; ++i)
        {
            final int idx = i * 2 + 1;
            final int x = left - i * 8 - 9;
            int y = top;
            int icon = 16;
            byte backgound = 0;

            if (mc.thePlayer.isPotionActive(Potion.hunger))
            {
                icon += 36;
                backgound = 13;
            }
            if (unused) backgound = 1; //Probably should be a += 1 but vanilla never uses this

            if (mc.thePlayer.getFoodStats().getSaturationLevel() <= 0.0F && updateCounter % (level * 3 + 1) == 0)
            {
                y = top + (rand.nextInt(3) - 1);
            }

            drawTexturedModalRect(x, y, 16 + backgound * 9, 27, 9, 9);

            if (unused)
            {
                if (idx < levelLast)
                    drawTexturedModalRect(x, y, icon + 54, 27, 9, 9);
                else if (idx == levelLast)
                    drawTexturedModalRect(x, y, icon + 63, 27, 9, 9);
            }

            if (idx < level)
                drawTexturedModalRect(x, y, icon + 36, 27, 9, 9);
            else if (idx == level)
                drawTexturedModalRect(x, y, icon + 45, 27, 9, 9);
        }
        mc.mcProfiler.endSection();
        post(FOOD);
    }

    protected void renderSleepFade(final int width, final int height)
    {
        if (mc.thePlayer.getSleepTimer() > 0)
        {
            mc.mcProfiler.startSection("sleep");
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            final int sleepTime = mc.thePlayer.getSleepTimer();
            float opacity = (float)sleepTime / 100.0F;

            if (opacity > 1.0F)
            {
                opacity = 1.0F - (float)(sleepTime - 100) / 10.0F;
            }

            final int color = (int)(220.0F * opacity) << 24 | 1052704;
            drawRect(0, 0, width, height, color);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            mc.mcProfiler.endSection();
        }
    }

    protected void renderExperience(final int width, final int height)
    {
        bind(icons);
        if (pre(EXPERIENCE)) return;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        if (mc.playerController.func_78763_f())
        {
            mc.mcProfiler.startSection("expBar");
            final int cap = this.mc.thePlayer.xpBarCap();
            final int left = width / 2 - 91;

            if (cap > 0)
            {
                final short barWidth = 182;
                final int filled = (int)(mc.thePlayer.experience * (float)(barWidth + 1));
                final int top = height - 32 + 3;
                drawTexturedModalRect(left, top, 0, 64, barWidth, 5);

                if (filled > 0)
                {
                    drawTexturedModalRect(left, top, 0, 69, filled, 5);
                }
            }

            this.mc.mcProfiler.endSection();


            if (mc.playerController.func_78763_f() && mc.thePlayer.experienceLevel > 0)
            {
                mc.mcProfiler.startSection("expLevel");
                final boolean flag1 = false;
                final int color = flag1 ? 16777215 : 8453920;
                final String text = "" + mc.thePlayer.experienceLevel;
                final int x = (width - fontrenderer.getStringWidth(text)) / 2;
                final int y = height - 31 - 4;
                fontrenderer.drawString(text, x + 1, y, 0);
                fontrenderer.drawString(text, x - 1, y, 0);
                fontrenderer.drawString(text, x, y + 1, 0);
                fontrenderer.drawString(text, x, y - 1, 0);
                fontrenderer.drawString(text, x, y, color);
                mc.mcProfiler.endSection();
            }
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        post(EXPERIENCE);
    }

    protected void renderJumpBar(final int width, final int height)
    {
        bind(icons);
        if (pre(JUMPBAR)) return;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        mc.mcProfiler.startSection("jumpBar");
        final float charge = mc.thePlayer.getHorseJumpPower();
        final int barWidth = 182;
        final int x = (width / 2) - (barWidth / 2);
        final int filled = (int)(charge * (float)(barWidth + 1));
        final int top = height - 32 + 3;

        drawTexturedModalRect(x, top, 0, 84, barWidth, 5);

        if (filled > 0)
        {
            this.drawTexturedModalRect(x, top, 0, 89, filled, 5);
        }

        mc.mcProfiler.endSection();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        post(JUMPBAR);
    }

    protected void renderToolHightlight(final int width, final int height)
    {
        if (this.mc.gameSettings.heldItemTooltips)
        {
            mc.mcProfiler.startSection("toolHighlight");

            if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null)
            {
                final String name = this.highlightingItemStack.getDisplayName();

                int opacity = (int)((float)this.remainingHighlightTicks * 256.0F / 10.0F);
                if (opacity > 255) opacity = 255;

                if (opacity > 0)
                {
                    int y = height - 59;
                    if (!mc.playerController.shouldDrawHUD()) y += 14;

                    GL11.glPushMatrix();
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    final FontRenderer font = highlightingItemStack.getItem().getFontRenderer(highlightingItemStack);
                    if (font != null)
                    {
                        final int x = (width - font.getStringWidth(name)) / 2;
                        font.drawStringWithShadow(name, x, y, WHITE | (opacity << 24));
                    }
                    else
                    {
                        final int x = (width - fontrenderer.getStringWidth(name)) / 2;
                        fontrenderer.drawStringWithShadow(name, x, y, WHITE | (opacity << 24));
                    }
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glPopMatrix();
                }
            }

            mc.mcProfiler.endSection();
        }
    }

    protected void renderHUDText(final int width, final int height)
    {
        mc.mcProfiler.startSection("forgeHudText");
        final ArrayList<String> left = new ArrayList<String>();
        final ArrayList<String> right = new ArrayList<String>();

        if (mc.isDemo())
        {
            final long time = mc.theWorld.getTotalWorldTime();
            if (time >= 120500L)
            {
                right.add(StatCollector.translateToLocal("demo.demoExpired"));
            }
            else
            {
                right.add(String.format(StatCollector.translateToLocal("demo.remainingTime"), StringUtils.ticksToElapsedTime((int)(120500L - time))));
            }
        }


        if (this.mc.gameSettings.showDebugInfo)
        {
            mc.mcProfiler.startSection("debug");
            GL11.glPushMatrix();
            left.add("Minecraft " + MC_VERSION + " (" + this.mc.debug + ")");
            left.add(mc.debugInfoRenders());
            left.add(mc.getEntityDebug());
            left.add(mc.debugInfoEntities());
            left.add(mc.getWorldProviderName());
            left.add(null); //Spacer

            final long max = Runtime.getRuntime().maxMemory();
            final long total = Runtime.getRuntime().totalMemory();
            final long free = Runtime.getRuntime().freeMemory();
            final long used = total - free;

            right.add("Used memory: " + used * 100L / max + "% (" + used / 1024L / 1024L + "MB) of " + max / 1024L / 1024L + "MB");
            right.add("Allocated memory: " + total * 100L / max + "% (" + total / 1024L / 1024L + "MB)");

            final int x = MathHelper.floor_double(mc.thePlayer.posX);
            final int y = MathHelper.floor_double(mc.thePlayer.posY);
            final int z = MathHelper.floor_double(mc.thePlayer.posZ);
            final float yaw = mc.thePlayer.rotationYaw;
            final int heading = MathHelper.floor_double((double)(mc.thePlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

            left.add(String.format("x: %.5f (%d) // c: %d (%d)", mc.thePlayer.posX, x, x >> 4, x & 15));
            left.add(String.format("y: %.3f (feet pos, %.3f eyes pos)", mc.thePlayer.boundingBox.minY, mc.thePlayer.posY));
            left.add(String.format("z: %.5f (%d) // c: %d (%d)", mc.thePlayer.posZ, z, z >> 4, z & 15));
            left.add(String.format("f: %d (%s) / %f", heading, Direction.directions[heading], MathHelper.wrapAngleTo180_float(yaw)));

            if (mc.theWorld != null && mc.theWorld.blockExists(x, y, z))
            {
                final Chunk chunk = this.mc.theWorld.getChunkFromBlockCoords(x, z);
                left.add(String.format("lc: %d b: %s bl: %d sl: %d rl: %d",
                  chunk.getTopFilledSegment() + 15,
                  chunk.getBiomeGenForWorldCoords(x & 15, z & 15, mc.theWorld.getWorldChunkManager()).biomeName,
                  chunk.getSavedLightValue(EnumSkyBlock.Block, x & 15, y, z & 15),
                  chunk.getSavedLightValue(EnumSkyBlock.Sky, x & 15, y, z & 15),
                  chunk.getBlockLightValue(x & 15, y, z & 15, 0)));
            }
            else
            {
                left.add(null);
            }

            left.add(String.format("ws: %.3f, fs: %.3f, g: %b, fl: %d", mc.thePlayer.capabilities.getWalkSpeed(), mc.thePlayer.capabilities.getFlySpeed(), mc.thePlayer.onGround, mc.theWorld.getHeightValue(x, z)));
            right.add(null);
            right.addAll(FMLCommonHandler.instance().getBrandings().subList(1, FMLCommonHandler.instance().getBrandings().size()));
            GL11.glPopMatrix();
            mc.mcProfiler.endSection();
        }

        final RenderGameOverlayEvent.Text event = new RenderGameOverlayEvent.Text(eventParent, left, right);
        if (!MinecraftForge.EVENT_BUS.post(event))
        {
            for (int x = 0; x < left.size(); x++)
            {
                final String msg = left.get(x);
                if (msg == null) continue;
                fontrenderer.drawStringWithShadow(msg, 2, 2 + x * 10, WHITE);
            }

            for (int x = 0; x < right.size(); x++)
            {
                final String msg = right.get(x);
                if (msg == null) continue;
                final int w = fontrenderer.getStringWidth(msg);
                fontrenderer.drawStringWithShadow(msg, width - w - 10, 2 + x * 10, WHITE);
            }
        }

        mc.mcProfiler.endSection();
        post(TEXT);
    }

    protected void renderRecordOverlay(final int width, final int height, final float partialTicks)
    {
        if (recordPlayingUpFor > 0)
        {
            mc.mcProfiler.startSection("overlayMessage");
            final float hue = (float)recordPlayingUpFor - partialTicks;
            int opacity = (int)(hue * 256.0F / 20.0F);
            if (opacity > 255) opacity = 255;

            if (opacity > 0)
            {
                GL11.glPushMatrix();
                GL11.glTranslatef((float)(width / 2), (float)(height - 48), 0.0F);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                final int color = (recordIsPlaying ? Color.HSBtoRGB(hue / 50.0F, 0.7F, 0.6F) & WHITE : WHITE);
                fontrenderer.drawString(recordPlaying, -fontrenderer.getStringWidth(recordPlaying) / 2, -4, color | (opacity << 24));
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
            }

            mc.mcProfiler.endSection();
        }
    }

    protected void renderChat(final int width, final int height)
    {
        mc.mcProfiler.startSection("chat");

        final RenderGameOverlayEvent.Chat event = new RenderGameOverlayEvent.Chat(eventParent, 0, height - 48);
        if (MinecraftForge.EVENT_BUS.post(event)) return;

        GL11.glPushMatrix();
        GL11.glTranslatef((float)event.posX, (float)event.posY, 0.0F);
        persistantChatGUI.drawChat(updateCounter);
        GL11.glPopMatrix();

        post(CHAT);

        mc.mcProfiler.endSection();
    }

    protected void renderPlayerList(final int width, final int height)
    {
        final ScoreObjective scoreobjective = this.mc.theWorld.getScoreboard().func_96539_a(0);
        final NetClientHandler handler = mc.thePlayer.sendQueue;

        if (mc.gameSettings.keyBindPlayerList.pressed && (!mc.isIntegratedServerRunning() || handler.playerInfoList.size() > 1 || scoreobjective != null))
        {
            if (pre(PLAYER_LIST)) return;
            this.mc.mcProfiler.startSection("playerList");
            final List players = handler.playerInfoList;
            final int maxPlayers = handler.currentServerMaxPlayers;
            int rows = maxPlayers;
            int columns = 1;

            for (columns = 1; rows > 20; rows = (maxPlayers + columns - 1) / columns)
            {
                columns++;
            }

            int columnWidth = 300 / columns;

            if (columnWidth > 150)
            {
                columnWidth = 150;
            }

            final int left = (width - columns * columnWidth) / 2;
            final byte border = 10;
            drawRect(left - 1, border - 1, left + columnWidth * columns, border + 9 * rows, Integer.MIN_VALUE);

            for (int i = 0; i < maxPlayers; i++)
            {
                final int xPos = left + i % columns * columnWidth;
                final int yPos = border + i / columns * 9;
                drawRect(xPos, yPos, xPos + columnWidth - 1, yPos + 8, 553648127);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glEnable(GL11.GL_ALPHA_TEST);

                if (i < players.size())
                {
                    final GuiPlayerInfo player = (GuiPlayerInfo)players.get(i);
                    final ScorePlayerTeam team = mc.theWorld.getScoreboard().getPlayersTeam(player.name);
                    final String displayName = ScorePlayerTeam.formatPlayerName(team, player.name);
                    fontrenderer.drawStringWithShadow(displayName, xPos, yPos, 16777215);

                    if (scoreobjective != null)
                    {
                        final int endX = xPos + fontrenderer.getStringWidth(displayName) + 5;
                        final int maxX = xPos + columnWidth - 12 - 5;

                        if (maxX - endX > 5)
                        {
                            final Score score = scoreobjective.getScoreboard().func_96529_a(player.name, scoreobjective);
                            final String scoreDisplay = EnumChatFormatting.YELLOW + "" + score.getScorePoints();
                            fontrenderer.drawStringWithShadow(scoreDisplay, maxX - fontrenderer.getStringWidth(scoreDisplay), yPos, 16777215);
                        }
                    }

                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                    mc.getTextureManager().bindTexture(Gui.icons);
                    int pingIndex = 4;
                    final int ping = player.responseTime;
                    if (ping < 0) pingIndex = 5;
                    else if (ping < 150) pingIndex = 0;
                    else if (ping < 300) pingIndex = 1;
                    else if (ping < 600) pingIndex = 2;
                    else if (ping < 1000) pingIndex = 3;

                    zLevel += 100.0F;
                    drawTexturedModalRect(xPos + columnWidth - 12, yPos, 0, 176 + pingIndex * 8, 10, 8);
                    zLevel -= 100.0F;
                }
            }
            post(PLAYER_LIST);
        }
    }

    protected void renderHealthMount(final int width, final int height)
    {
        final Entity tmp = mc.thePlayer.ridingEntity;
        if (!(tmp instanceof EntityLivingBase)) return;

        bind(icons);

        if (pre(HEALTHMOUNT)) return;

        final boolean unused = false;
        final int left_align = width / 2 + 91;

        mc.mcProfiler.endStartSection("mountHealth");
        final EntityLivingBase mount = (EntityLivingBase)tmp;
        final int health = (int)Math.ceil((double)mount.getHealth());
        final float healthMax = mount.getMaxHealth();
        int hearts = (int)(healthMax + 0.5F) / 2;

        if (hearts > 30) hearts = 30;

        final int MARGIN = 52;
        final int BACKGROUND = MARGIN + (unused ? 1 : 0);
        final int HALF = MARGIN + 45;
        final int FULL = MARGIN + 36;

        for (int heart = 0; hearts > 0; heart += 20)
        {
            final int top = height - right_height;

            final int rowCount = Math.min(hearts, 10);
            hearts -= rowCount;

            for (int i = 0; i < rowCount; ++i)
            {
                final int x = left_align - i * 8 - 9;
                drawTexturedModalRect(x, top, BACKGROUND, 9, 9, 9);

                if (i * 2 + 1 + heart < health)
                    drawTexturedModalRect(x, top, FULL, 9, 9, 9);
                else if (i * 2 + 1 + heart == health)
                    drawTexturedModalRect(x, top, HALF, 9, 9, 9);
            }

            right_height += 10;
        }
        post(HEALTHMOUNT);
    }

    //Helper macros
    private boolean pre(final ElementType type)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(eventParent, type));
    }
    private void post(final ElementType type)
    {
        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(eventParent, type));
    }
    private void bind(final ResourceLocation res)
    {
        mc.getTextureManager().bindTexture(res);
    }
}
