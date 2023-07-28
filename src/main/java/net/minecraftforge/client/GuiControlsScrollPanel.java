package net.minecraftforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiControlsScrollPanel extends GuiSlot
{
    protected static final ResourceLocation WIDGITS = new ResourceLocation("textures/gui/widgets.png");
    private GuiControls controls;
    private GameSettings options;
    private Minecraft mc;
    private String[] message;
    private int _mouseX;
    private int _mouseY;
    private int selected = -1;

    public GuiControlsScrollPanel(final GuiControls controls, final GameSettings options, final Minecraft mc)
    {
        super(mc, controls.width, controls.height, 16, (controls.height - 32) + 4, 25);
        this.controls = controls;
        this.options = options;
        this.mc = mc;
    }

    @Override
    protected int getSize()
    {
        return options.keyBindings.length;
    }

    @Override
    protected void elementClicked(final int i, final boolean flag)
    {
        if (!flag)
        {
            if (selected == -1)
            {
                selected = i;
            }
            else
            {
                options.setKeyBinding(selected, -100);
                selected = -1;
                KeyBinding.resetKeyBindingArrayAndHash();
            }
        }
    }

    @Override
    protected boolean isSelected(final int i)
    {
        return false;
    }

    @Override
    protected void drawBackground() {}

    @Override
    public void drawScreen(final int mX, final int mY, final float f)
    {
        _mouseX = mX;
        _mouseY = mY;

        if (selected != -1 && !Mouse.isButtonDown(0) && Mouse.getDWheel() == 0)
        {
            if (Mouse.next() && Mouse.getEventButtonState())
            {
                options.setKeyBinding(selected, -100 + Mouse.getEventButton());
                selected = -1;
                KeyBinding.resetKeyBindingArrayAndHash();
            }
        }

        super.drawScreen(mX, mY, f);
    }

    @Override
    protected void drawSlot(final int index, int xPosition, final int yPosition, final int l, final Tessellator tessellator)
    {
        int xPosition1 = xPosition;
        final int width = 70;
        final int height = 20;
        xPosition1 -= 20;
        final boolean flag = _mouseX >= xPosition1 && _mouseY >= yPosition && _mouseX < xPosition1 + width && _mouseY < yPosition + height;
        final int k = (flag ? 2 : 1);

        mc.renderEngine.bindTexture(WIDGITS);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        controls.drawTexturedModalRect(xPosition1, yPosition, 0, 46 + k * 20, width / 2, height);
        controls.drawTexturedModalRect(xPosition1 + width / 2, yPosition, 200 - width / 2, 46 + k * 20, width / 2, height);
        controls.drawString(mc.fontRenderer, options.getKeyBindingDescription(index), xPosition1 + width + 4, yPosition + 6, 0xFFFFFFFF);

        boolean conflict = false;
        for (int x = 0; x < options.keyBindings.length; x++)
        {
            if (x != index && options.keyBindings[x].keyCode == options.keyBindings[index].keyCode)
            {
                conflict = true;
                break;
            }
        }

        String str = (conflict ? EnumChatFormatting.RED : "") + options.getOptionDisplayString(index);
        str = (index == selected ? EnumChatFormatting.WHITE + "> " + EnumChatFormatting.YELLOW + "??? " + EnumChatFormatting.WHITE + "<" : str);
        controls.drawCenteredString(mc.fontRenderer, str, xPosition1 + (width / 2), yPosition + (height - 8) / 2, 0xFFFFFFFF);
    }

    public boolean keyTyped(final char c, final int i)
    {
        if (selected != -1)
        {
            options.setKeyBinding(selected, i);
            selected = -1;
            KeyBinding.resetKeyBindingArrayAndHash();
            return false;
        }
        return true;
    }
}
