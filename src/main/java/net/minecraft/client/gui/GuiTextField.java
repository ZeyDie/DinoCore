package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiTextField extends Gui
{
    /**
     * Have the font renderer from GuiScreen to render the textbox text into the screen.
     */
    private final FontRenderer fontRenderer;
    private final int xPos;
    private final int yPos;

    /** The width of this text field. */
    private final int width;
    private final int height;

    /** Have the current text beign edited on the textbox. */
    private String text = "";
    private int maxStringLength = 32;
    private int cursorCounter;
    private boolean enableBackgroundDrawing = true;

    /**
     * if true the textbox can lose focus by clicking elsewhere on the screen
     */
    private boolean canLoseFocus = true;

    /**
     * If this value is true along isEnabled, keyTyped will process the keys.
     */
    private boolean isFocused;

    /**
     * If this value is true along isFocused, keyTyped will process the keys.
     */
    private boolean isEnabled = true;

    /**
     * The current character index that should be used as start of the rendered text.
     */
    private int lineScrollOffset;
    private int cursorPosition;

    /** other selection position, maybe the same as the cursor */
    private int selectionEnd;
    private int enabledColor = 14737632;
    private int disabledColor = 7368816;

    /** True if this textbox is visible */
    private boolean visible = true;

    public GuiTextField(final FontRenderer par1FontRenderer, final int par2, final int par3, final int par4, final int par5)
    {
        this.fontRenderer = par1FontRenderer;
        this.xPos = par2;
        this.yPos = par3;
        this.width = par4;
        this.height = par5;
    }

    /**
     * Increments the cursor counter
     */
    public void updateCursorCounter()
    {
        ++this.cursorCounter;
    }

    /**
     * Sets the text of the textbox.
     */
    public void setText(final String par1Str)
    {
        if (par1Str.length() > this.maxStringLength)
        {
            this.text = par1Str.substring(0, this.maxStringLength);
        }
        else
        {
            this.text = par1Str;
        }

        this.setCursorPositionEnd();
    }

    /**
     * Returns the text beign edited on the textbox.
     */
    public String getText()
    {
        return this.text;
    }

    /**
     * @return returns the text between the cursor and selectionEnd
     */
    public String getSelectedtext()
    {
        final int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        final int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        return this.text.substring(i, j);
    }

    /**
     * replaces selected text, or inserts text at the position on the cursor
     */
    public void writeText(final String par1Str)
    {
        String s1 = "";
        final String s2 = ChatAllowedCharacters.filerAllowedCharacters(par1Str);
        final int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        final int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        final int k = this.maxStringLength - this.text.length() - (i - this.selectionEnd);
        final boolean flag = false;

        if (!this.text.isEmpty())
        {
            s1 = s1 + this.text.substring(0, i);
        }

        final int l;

        if (k < s2.length())
        {
            s1 = s1 + s2.substring(0, k);
            l = k;
        }
        else
        {
            s1 = s1 + s2;
            l = s2.length();
        }

        if (!this.text.isEmpty() && j < this.text.length())
        {
            s1 = s1 + this.text.substring(j);
        }

        this.text = s1;
        this.moveCursorBy(i - this.selectionEnd + l);
    }

    /**
     * Deletes the specified number of words starting at the cursor position. Negative numbers will delete words left of
     * the cursor.
     */
    public void deleteWords(final int par1)
    {
        if (!this.text.isEmpty())
        {
            if (this.selectionEnd != this.cursorPosition)
            {
                this.writeText("");
            }
            else
            {
                this.deleteFromCursor(this.getNthWordFromCursor(par1) - this.cursorPosition);
            }
        }
    }

    /**
     * delete the selected text, otherwsie deletes characters from either side of the cursor. params: delete num
     */
    public void deleteFromCursor(final int par1)
    {
        if (!this.text.isEmpty())
        {
            if (this.selectionEnd != this.cursorPosition)
            {
                this.writeText("");
            }
            else
            {
                final boolean flag = par1 < 0;
                final int j = flag ? this.cursorPosition + par1 : this.cursorPosition;
                final int k = flag ? this.cursorPosition : this.cursorPosition + par1;
                String s = "";

                if (j >= 0)
                {
                    s = this.text.substring(0, j);
                }

                if (k < this.text.length())
                {
                    s = s + this.text.substring(k);
                }

                this.text = s;

                if (flag)
                {
                    this.moveCursorBy(par1);
                }
            }
        }
    }

    /**
     * see @getNthNextWordFromPos() params: N, position
     */
    public int getNthWordFromCursor(final int par1)
    {
        return this.getNthWordFromPos(par1, this.getCursorPosition());
    }

    /**
     * gets the position of the nth word. N may be negative, then it looks backwards. params: N, position
     */
    public int getNthWordFromPos(final int par1, final int par2)
    {
        return this.func_73798_a(par1, this.getCursorPosition(), true);
    }

    public int func_73798_a(final int par1, final int par2, final boolean par3)
    {
        int k = par2;
        final boolean flag1 = par1 < 0;
        final int l = Math.abs(par1);

        for (int i1 = 0; i1 < l; ++i1)
        {
            if (flag1)
            {
                while (par3 && k > 0 && this.text.charAt(k - 1) == 32)
                {
                    --k;
                }

                while (k > 0 && this.text.charAt(k - 1) != 32)
                {
                    --k;
                }
            }
            else
            {
                final int j1 = this.text.length();
                k = this.text.indexOf(32, k);

                if (k == -1)
                {
                    k = j1;
                }
                else
                {
                    while (par3 && k < j1 && this.text.charAt(k) == 32)
                    {
                        ++k;
                    }
                }
            }
        }

        return k;
    }

    /**
     * Moves the text cursor by a specified number of characters and clears the selection
     */
    public void moveCursorBy(final int par1)
    {
        this.setCursorPosition(this.selectionEnd + par1);
    }

    /**
     * sets the position of the cursor to the provided index
     */
    public void setCursorPosition(final int par1)
    {
        this.cursorPosition = par1;
        final int j = this.text.length();

        if (this.cursorPosition < 0)
        {
            this.cursorPosition = 0;
        }

        if (this.cursorPosition > j)
        {
            this.cursorPosition = j;
        }

        this.setSelectionPos(this.cursorPosition);
    }

    /**
     * sets the cursors position to the beginning
     */
    public void setCursorPositionZero()
    {
        this.setCursorPosition(0);
    }

    /**
     * sets the cursors position to after the text
     */
    public void setCursorPositionEnd()
    {
        this.setCursorPosition(this.text.length());
    }

    /**
     * Call this method from you GuiScreen to process the keys into textbox.
     */
    public boolean textboxKeyTyped(final char par1, final int par2)
    {
        if (this.isEnabled && this.isFocused)
        {
            switch (par1)
            {
                case 1:
                    this.setCursorPositionEnd();
                    this.setSelectionPos(0);
                    return true;
                case 3:
                    GuiScreen.setClipboardString(this.getSelectedtext());
                    return true;
                case 22:
                    this.writeText(GuiScreen.getClipboardString());
                    return true;
                case 24:
                    GuiScreen.setClipboardString(this.getSelectedtext());
                    this.writeText("");
                    return true;
                default:
                    switch (par2)
                    {
                        case 14:
                            if (GuiScreen.isCtrlKeyDown())
                            {
                                this.deleteWords(-1);
                            }
                            else
                            {
                                this.deleteFromCursor(-1);
                            }

                            return true;
                        case 199:
                            if (GuiScreen.isShiftKeyDown())
                            {
                                this.setSelectionPos(0);
                            }
                            else
                            {
                                this.setCursorPositionZero();
                            }

                            return true;
                        case 203:
                            if (GuiScreen.isShiftKeyDown())
                            {
                                if (GuiScreen.isCtrlKeyDown())
                                {
                                    this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
                                }
                                else
                                {
                                    this.setSelectionPos(this.getSelectionEnd() - 1);
                                }
                            }
                            else if (GuiScreen.isCtrlKeyDown())
                            {
                                this.setCursorPosition(this.getNthWordFromCursor(-1));
                            }
                            else
                            {
                                this.moveCursorBy(-1);
                            }

                            return true;
                        case 205:
                            if (GuiScreen.isShiftKeyDown())
                            {
                                if (GuiScreen.isCtrlKeyDown())
                                {
                                    this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
                                }
                                else
                                {
                                    this.setSelectionPos(this.getSelectionEnd() + 1);
                                }
                            }
                            else if (GuiScreen.isCtrlKeyDown())
                            {
                                this.setCursorPosition(this.getNthWordFromCursor(1));
                            }
                            else
                            {
                                this.moveCursorBy(1);
                            }

                            return true;
                        case 207:
                            if (GuiScreen.isShiftKeyDown())
                            {
                                this.setSelectionPos(this.text.length());
                            }
                            else
                            {
                                this.setCursorPositionEnd();
                            }

                            return true;
                        case 211:
                            if (GuiScreen.isCtrlKeyDown())
                            {
                                this.deleteWords(1);
                            }
                            else
                            {
                                this.deleteFromCursor(1);
                            }

                            return true;
                        default:
                            if (ChatAllowedCharacters.isAllowedCharacter(par1))
                            {
                                this.writeText(Character.toString(par1));
                                return true;
                            }
                            else
                            {
                                return false;
                            }
                    }
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Args: x, y, buttonClicked
     */
    public void mouseClicked(final int par1, final int par2, final int par3)
    {
        final boolean flag = par1 >= this.xPos && par1 < this.xPos + this.width && par2 >= this.yPos && par2 < this.yPos + this.height;

        if (this.canLoseFocus)
        {
            this.setFocused(this.isEnabled && flag);
        }

        if (this.isFocused && par3 == 0)
        {
            int l = par1 - this.xPos;

            if (this.enableBackgroundDrawing)
            {
                l -= 4;
            }

            final String s = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            this.setCursorPosition(this.fontRenderer.trimStringToWidth(s, l).length() + this.lineScrollOffset);
        }
    }

    /**
     * Draws the textbox
     */
    public void drawTextBox()
    {
        if (this.getVisible())
        {
            if (this.getEnableBackgroundDrawing())
            {
                drawRect(this.xPos - 1, this.yPos - 1, this.xPos + this.width + 1, this.yPos + this.height + 1, -6250336);
                drawRect(this.xPos, this.yPos, this.xPos + this.width, this.yPos + this.height, -16777216);
            }

            final int i = this.isEnabled ? this.enabledColor : this.disabledColor;
            final int j = this.cursorPosition - this.lineScrollOffset;
            int k = this.selectionEnd - this.lineScrollOffset;
            final String s = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            final boolean flag = j >= 0 && j <= s.length();
            final boolean flag1 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && flag;
            final int l = this.enableBackgroundDrawing ? this.xPos + 4 : this.xPos;
            final int i1 = this.enableBackgroundDrawing ? this.yPos + (this.height - 8) / 2 : this.yPos;
            int j1 = l;

            if (k > s.length())
            {
                k = s.length();
            }

            if (!s.isEmpty())
            {
                final String s1 = flag ? s.substring(0, j) : s;
                j1 = this.fontRenderer.drawStringWithShadow(s1, l, i1, i);
            }

            final boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            int k1 = j1;

            if (!flag)
            {
                k1 = j > 0 ? l + this.width : l;
            }
            else if (flag2)
            {
                k1 = j1 - 1;
                --j1;
            }

            if (!s.isEmpty() && flag && j < s.length())
            {
                this.fontRenderer.drawStringWithShadow(s.substring(j), j1, i1, i);
            }

            if (flag1)
            {
                if (flag2)
                {
                    Gui.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + this.fontRenderer.FONT_HEIGHT, -3092272);
                }
                else
                {
                    this.fontRenderer.drawStringWithShadow("_", k1, i1, i);
                }
            }

            if (k != j)
            {
                final int l1 = l + this.fontRenderer.getStringWidth(s.substring(0, k));
                this.drawCursorVertical(k1, i1 - 1, l1 - 1, i1 + 1 + this.fontRenderer.FONT_HEIGHT);
            }
        }
    }

    /**
     * draws the vertical line cursor in the textbox
     */
    private void drawCursorVertical(int par1, int par2, int par3, int par4)
    {
        int par11 = par1;
        int par31 = par3;
        int par21 = par2;
        int par41 = par4;
        int i1;

        if (par11 < par31)
        {
            i1 = par11;
            par11 = par31;
            par31 = i1;
        }

        if (par21 < par41)
        {
            i1 = par21;
            par21 = par41;
            par41 = i1;
        }

        final Tessellator tessellator = Tessellator.instance;
        GL11.glColor4f(0.0F, 0.0F, 255.0F, 255.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glLogicOp(GL11.GL_OR_REVERSE);
        tessellator.startDrawingQuads();
        tessellator.addVertex((double) par11, (double) par41, 0.0D);
        tessellator.addVertex((double) par31, (double) par41, 0.0D);
        tessellator.addVertex((double) par31, (double) par21, 0.0D);
        tessellator.addVertex((double) par11, (double) par21, 0.0D);
        tessellator.draw();
        GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public void setMaxStringLength(final int par1)
    {
        this.maxStringLength = par1;

        if (this.text.length() > par1)
        {
            this.text = this.text.substring(0, par1);
        }
    }

    /**
     * returns the maximum number of character that can be contained in this textbox
     */
    public int getMaxStringLength()
    {
        return this.maxStringLength;
    }

    /**
     * returns the current position of the cursor
     */
    public int getCursorPosition()
    {
        return this.cursorPosition;
    }

    /**
     * get enable drawing background and outline
     */
    public boolean getEnableBackgroundDrawing()
    {
        return this.enableBackgroundDrawing;
    }

    /**
     * enable drawing background and outline
     */
    public void setEnableBackgroundDrawing(final boolean par1)
    {
        this.enableBackgroundDrawing = par1;
    }

    /**
     * Sets the text colour for this textbox (disabled text will not use this colour)
     */
    public void setTextColor(final int par1)
    {
        this.enabledColor = par1;
    }

    public void setDisabledTextColour(final int par1)
    {
        this.disabledColor = par1;
    }

    /**
     * setter for the focused field
     */
    public void setFocused(final boolean par1)
    {
        if (par1 && !this.isFocused)
        {
            this.cursorCounter = 0;
        }

        this.isFocused = par1;
    }

    /**
     * getter for the focused field
     */
    public boolean isFocused()
    {
        return this.isFocused;
    }

    public void setEnabled(final boolean par1)
    {
        this.isEnabled = par1;
    }

    /**
     * the side of the selection that is not the cursor, maye be the same as the cursor
     */
    public int getSelectionEnd()
    {
        return this.selectionEnd;
    }

    /**
     * returns the width of the textbox depending on if the the box is enabled
     */
    public int getWidth()
    {
        return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
    }

    /**
     * Sets the position of the selection anchor (i.e. position the selection was started at)
     */
    public void setSelectionPos(int par1)
    {
        int par11 = par1;
        final int j = this.text.length();

        if (par11 > j)
        {
            par11 = j;
        }

        if (par11 < 0)
        {
            par11 = 0;
        }

        this.selectionEnd = par11;

        if (this.fontRenderer != null)
        {
            if (this.lineScrollOffset > j)
            {
                this.lineScrollOffset = j;
            }

            final int k = this.getWidth();
            final String s = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), k);
            final int l = s.length() + this.lineScrollOffset;

            if (par11 == this.lineScrollOffset)
            {
                this.lineScrollOffset -= this.fontRenderer.trimStringToWidth(this.text, k, true).length();
            }

            if (par11 > l)
            {
                this.lineScrollOffset += par11 - l;
            }
            else if (par11 <= this.lineScrollOffset)
            {
                this.lineScrollOffset -= this.lineScrollOffset - par11;
            }

            if (this.lineScrollOffset < 0)
            {
                this.lineScrollOffset = 0;
            }

            if (this.lineScrollOffset > j)
            {
                this.lineScrollOffset = j;
            }
        }
    }

    /**
     * if true the textbox can lose focus by clicking elsewhere on the screen
     */
    public void setCanLoseFocus(final boolean par1)
    {
        this.canLoseFocus = par1;
    }

    /**
     * @return {@code true} if this textbox is visible
     */
    public boolean getVisible()
    {
        return this.visible;
    }

    /**
     * Sets whether or not this textbox is visible
     */
    public void setVisible(final boolean par1)
    {
        this.visible = par1;
    }
}
