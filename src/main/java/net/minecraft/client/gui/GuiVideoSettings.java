package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.EnumOptions;
import net.minecraft.client.settings.GameSettings;

@SideOnly(Side.CLIENT)
public class GuiVideoSettings extends GuiScreen
{
    private GuiScreen parentGuiScreen;

    /** The title string that is displayed in the top-center of the screen. */
    protected String screenTitle = "Video Settings";

    /** GUI game settings */
    private GameSettings guiGameSettings;

    /**
     * True if the system is 64-bit (using a simple indexOf test on a system property)
     */
    private boolean is64bit;

    /** An array of all of EnumOption's video options. */
    private static EnumOptions[] videoOptions = {EnumOptions.GRAPHICS, EnumOptions.RENDER_DISTANCE, EnumOptions.AMBIENT_OCCLUSION, EnumOptions.FRAMERATE_LIMIT, EnumOptions.ANAGLYPH, EnumOptions.VIEW_BOBBING, EnumOptions.GUI_SCALE, EnumOptions.ADVANCED_OPENGL, EnumOptions.GAMMA, EnumOptions.RENDER_CLOUDS, EnumOptions.PARTICLES, EnumOptions.USE_SERVER_TEXTURES, EnumOptions.USE_FULLSCREEN, EnumOptions.ENABLE_VSYNC};

    public GuiVideoSettings(final GuiScreen par1GuiScreen, final GameSettings par2GameSettings)
    {
        this.parentGuiScreen = par1GuiScreen;
        this.guiGameSettings = par2GameSettings;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.screenTitle = I18n.getString("options.videoTitle");
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.getString("gui.done")));
        this.is64bit = false;
        final String[] astring = {"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};
        final String[] astring1 = astring;
        int i = astring.length;

        for (int j = 0; j < i; ++j)
        {
            final String s = astring1[j];
            final String s1 = System.getProperty(s);

            if (s1 != null && s1.contains("64"))
            {
                this.is64bit = true;
                break;
            }
        }

        int k = 0;
        i = this.is64bit ? 0 : -15;
        final EnumOptions[] aenumoptions = videoOptions;
        final int l = aenumoptions.length;

        for (int i1 = 0; i1 < l; ++i1)
        {
            final EnumOptions enumoptions = aenumoptions[i1];

            if (enumoptions.getEnumFloat())
            {
                this.buttonList.add(new GuiSlider(enumoptions.returnEnumOrdinal(), this.width / 2 - 155 + k % 2 * 160, this.height / 7 + i + 24 * (k >> 1), enumoptions, this.guiGameSettings.getKeyBinding(enumoptions), this.guiGameSettings.getOptionFloatValue(enumoptions)));
            }
            else
            {
                this.buttonList.add(new GuiSmallButton(enumoptions.returnEnumOrdinal(), this.width / 2 - 155 + k % 2 * 160, this.height / 7 + i + 24 * (k >> 1), enumoptions, this.guiGameSettings.getKeyBinding(enumoptions)));
            }

            ++k;
        }
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(final GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            final int i = this.guiGameSettings.guiScale;

            if (par1GuiButton.id < 100 && par1GuiButton instanceof GuiSmallButton)
            {
                this.guiGameSettings.setOptionValue(((GuiSmallButton)par1GuiButton).returnEnumOptions(), 1);
                par1GuiButton.displayString = this.guiGameSettings.getKeyBinding(EnumOptions.getEnumOptions(par1GuiButton.id));
            }

            if (par1GuiButton.id == 200)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.parentGuiScreen);
            }

            if (this.guiGameSettings.guiScale != i)
            {
                final ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
                final int j = scaledresolution.getScaledWidth();
                final int k = scaledresolution.getScaledHeight();
                this.setWorldAndResolution(this.mc, j, k);
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(final int par1, final int par2, final float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, this.is64bit ? 20 : 5, 16777215);

        if (!this.is64bit && this.guiGameSettings.renderDistance == 0)
        {
            this.drawCenteredString(this.fontRenderer, I18n.getString("options.farWarning1"), this.width / 2, this.height / 6 + 144 + 1, 11468800);
            this.drawCenteredString(this.fontRenderer, I18n.getString("options.farWarning2"), this.width / 2, this.height / 6 + 144 + 13, 11468800);
        }

        super.drawScreen(par1, par2, par3);
    }
}
