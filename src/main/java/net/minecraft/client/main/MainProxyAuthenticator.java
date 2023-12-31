package net.minecraft.client.main;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

@SideOnly(Side.CLIENT)
public final class MainProxyAuthenticator extends Authenticator
{
    final String field_111237_a;

    final String field_111236_b;

    public MainProxyAuthenticator(final String par1Str, final String par2Str)
    {
        this.field_111237_a = par1Str;
        this.field_111236_b = par2Str;
    }

    protected PasswordAuthentication getPasswordAuthentication()
    {
        return new PasswordAuthentication(this.field_111237_a, this.field_111236_b.toCharArray());
    }
}
