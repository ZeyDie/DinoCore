package net.minecraft.network.packet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.CryptManager;

import javax.crypto.SecretKey;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Packet252SharedKey extends Packet
{
    private byte[] sharedSecret = new byte[0];
    private byte[] verifyToken = new byte[0];

    /**
     * Secret AES key decrypted from sharedSecret via the server's private RSA key
     */
    private SecretKey sharedKey;

    public Packet252SharedKey() {}

    @SideOnly(Side.CLIENT)
    public Packet252SharedKey(final SecretKey par1SecretKey, final PublicKey par2PublicKey, final byte[] par3ArrayOfByte)
    {
        this.sharedKey = par1SecretKey;
        this.sharedSecret = CryptManager.encryptData(par2PublicKey, par1SecretKey.getEncoded());
        this.verifyToken = CryptManager.encryptData(par2PublicKey, par3ArrayOfByte);
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException
    {
        this.sharedSecret = readBytesFromStream(par1DataInput);
        this.verifyToken = readBytesFromStream(par1DataInput);
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException
    {
        writeByteArray(par1DataOutput, this.sharedSecret);
        writeByteArray(par1DataOutput, this.verifyToken);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final NetHandler par1NetHandler)
    {
        par1NetHandler.handleSharedKey(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 2 + this.sharedSecret.length + 2 + this.verifyToken.length;
    }

    /**
     * Return secretKey, decrypting it from the sharedSecret byte array if needed
     */
    public SecretKey getSharedKey(final PrivateKey par1PrivateKey)
    {
        return par1PrivateKey == null ? this.sharedKey : (this.sharedKey = CryptManager.decryptSharedKey(par1PrivateKey, this.sharedSecret));
    }

    /**
     * Return the secret AES sharedKey (used by client only)
     */
    public SecretKey getSharedKey()
    {
        return this.getSharedKey((PrivateKey)null);
    }

    /**
     * Return verifyToken
     */
    public byte[] getVerifyToken(final PrivateKey par1PrivateKey)
    {
        return par1PrivateKey == null ? this.verifyToken : CryptManager.decryptData(par1PrivateKey, this.verifyToken);
    }
}
