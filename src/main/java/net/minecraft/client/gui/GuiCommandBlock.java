package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntityCommandBlock;
import org.lwjgl.input.Keyboard;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

@SideOnly(Side.CLIENT)
public class GuiCommandBlock extends GuiScreen
{
    /** Text field containing the command block's command. */
    private GuiTextField commandTextField;

    /** Command block being edited. */
    private final TileEntityCommandBlock commandBlock;
    private GuiButton doneBtn;
    private GuiButton cancelBtn;

    public GuiCommandBlock(final TileEntityCommandBlock par1TileEntityCommandBlock)
    {
        this.commandBlock = par1TileEntityCommandBlock;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.commandTextField.updateCursorCounter();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(this.doneBtn = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.getString("gui.done")));
        this.buttonList.add(this.cancelBtn = new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.getString("gui.cancel")));
        this.commandTextField = new GuiTextField(this.fontRenderer, this.width / 2 - 150, 60, 300, 20);
        this.commandTextField.setMaxStringLength(32767);
        this.commandTextField.setFocused(true);
        this.commandTextField.setText(this.commandBlock.getCommand());
        this.doneBtn.enabled = !this.commandTextField.getText().trim().isEmpty();
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(final GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            if (par1GuiButton.id == 1)
            {
                this.mc.displayGuiScreen((GuiScreen)null);
            }
            else if (par1GuiButton.id == 0)
            {
                final String s = "MC|AdvCdm";
                final ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                final DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);

                try
                {
                    dataoutputstream.writeInt(this.commandBlock.xCoord);
                    dataoutputstream.writeInt(this.commandBlock.yCoord);
                    dataoutputstream.writeInt(this.commandBlock.zCoord);
                    Packet.writeString(this.commandTextField.getText(), dataoutputstream);
                    this.mc.getNetHandler().addToSendQueue(new Packet250CustomPayload(s, bytearrayoutputstream.toByteArray()));
                }
                catch (final Exception exception)
                {
                    exception.printStackTrace();
                }

                this.mc.displayGuiScreen((GuiScreen)null);
            }
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(final char par1, final int par2)
    {
        this.commandTextField.textboxKeyTyped(par1, par2);
        this.doneBtn.enabled = !this.commandTextField.getText().trim().isEmpty();

        if (par2 != 28 && par2 != 156)
        {
            if (par2 == 1)
            {
                this.actionPerformed(this.cancelBtn);
            }
        }
        else
        {
            this.actionPerformed(this.doneBtn);
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(final int par1, final int par2, final int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.commandTextField.mouseClicked(par1, par2, par3);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(final int par1, final int par2, final float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, I18n.getString("advMode.setCommand"), this.width / 2, 20, 16777215);
        this.drawString(this.fontRenderer, I18n.getString("advMode.command"), this.width / 2 - 150, 47, 10526880);
        this.drawString(this.fontRenderer, I18n.getString("advMode.nearestPlayer"), this.width / 2 - 150, 97, 10526880);
        this.drawString(this.fontRenderer, I18n.getString("advMode.randomPlayer"), this.width / 2 - 150, 108, 10526880);
        this.drawString(this.fontRenderer, I18n.getString("advMode.allPlayers"), this.width / 2 - 150, 119, 10526880);
        this.commandTextField.drawTextBox();
        super.drawScreen(par1, par2, par3);
    }
}
