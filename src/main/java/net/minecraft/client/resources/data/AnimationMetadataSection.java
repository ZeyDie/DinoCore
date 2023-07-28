package net.minecraft.client.resources.data;

import com.google.common.collect.Sets;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class AnimationMetadataSection implements MetadataSection
{
    private final List animationFrames;
    private final int frameWidth;
    private final int frameHeight;
    private final int frameTime;

    public AnimationMetadataSection(final List par1List, final int par2, final int par3, final int par4)
    {
        this.animationFrames = par1List;
        this.frameWidth = par2;
        this.frameHeight = par3;
        this.frameTime = par4;
    }

    public int getFrameHeight()
    {
        return this.frameHeight;
    }

    public int getFrameWidth()
    {
        return this.frameWidth;
    }

    public int getFrameCount()
    {
        return this.animationFrames.size();
    }

    public int getFrameTime()
    {
        return this.frameTime;
    }

    private AnimationFrame getAnimationFrame(final int par1)
    {
        return (AnimationFrame)this.animationFrames.get(par1);
    }

    public int getFrameTimeSingle(final int par1)
    {
        final AnimationFrame animationframe = this.getAnimationFrame(par1);
        return animationframe.hasNoTime() ? this.frameTime : animationframe.getFrameTime();
    }

    public boolean frameHasTime(final int par1)
    {
        return !((AnimationFrame)this.animationFrames.get(par1)).hasNoTime();
    }

    public int getFrameIndex(final int par1)
    {
        return ((AnimationFrame)this.animationFrames.get(par1)).getFrameIndex();
    }

    public Set getFrameIndexSet()
    {
        final HashSet hashset = Sets.newHashSet();
        final Iterator iterator = this.animationFrames.iterator();

        while (iterator.hasNext())
        {
            final AnimationFrame animationframe = (AnimationFrame)iterator.next();
            hashset.add(Integer.valueOf(animationframe.getFrameIndex()));
        }

        return hashset;
    }
}
