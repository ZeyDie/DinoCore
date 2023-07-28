package net.minecraft.util;

import cpw.mods.fml.common.asm.ReobfuscationMarker;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.io.CipherInputStream;
import org.bouncycastle.crypto.io.CipherOutputStream;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

@ReobfuscationMarker
public class CryptManager
{
    @SideOnly(Side.CLIENT)

    /**
     * Generate a new shared secret AES key from a secure random source
     */
    public static SecretKey createNewSharedKey()
    {
        final CipherKeyGenerator cipherkeygenerator = new CipherKeyGenerator();
        cipherkeygenerator.init(new KeyGenerationParameters(new SecureRandom(), 128));
        return new SecretKeySpec(cipherkeygenerator.generateKey(), "AES");
    }

    public static KeyPair createNewKeyPair()
    {
        try
        {
            final KeyPairGenerator keypairgenerator = KeyPairGenerator.getInstance("RSA");
            keypairgenerator.initialize(1024);
            return keypairgenerator.generateKeyPair();
        }
        catch (final NoSuchAlgorithmException nosuchalgorithmexception)
        {
            nosuchalgorithmexception.printStackTrace();
            System.err.println("Key pair generation failed!");
            return null;
        }
    }

    /**
     * Compute a serverId hash for use by sendSessionRequest()
     */
    public static byte[] getServerIdHash(final String par0Str, final PublicKey par1PublicKey, final SecretKey par2SecretKey)
    {
        try
        {
            return digestOperation("SHA-1", new byte[][] {par0Str.getBytes("ISO_8859_1"), par2SecretKey.getEncoded(), par1PublicKey.getEncoded()});
        }
        catch (final UnsupportedEncodingException unsupportedencodingexception)
        {
            unsupportedencodingexception.printStackTrace();
            return null;
        }
    }

    /**
     * Compute a message digest on arbitrary byte[] data
     */
    private static byte[] digestOperation(final String par0Str, final byte[] ... par1ArrayOfByte)
    {
        try
        {
            final MessageDigest messagedigest = MessageDigest.getInstance(par0Str);
            final byte[][] abyte = par1ArrayOfByte;
            final int i = par1ArrayOfByte.length;

            for (int j = 0; j < i; ++j)
            {
                final byte[] abyte1 = abyte[j];
                messagedigest.update(abyte1);
            }

            return messagedigest.digest();
        }
        catch (final NoSuchAlgorithmException nosuchalgorithmexception)
        {
            nosuchalgorithmexception.printStackTrace();
            return null;
        }
    }

    /**
     * Create a new PublicKey from encoded X.509 data
     */
    public static PublicKey decodePublicKey(final byte[] par0ArrayOfByte)
    {
        try
        {
            final X509EncodedKeySpec x509encodedkeyspec = new X509EncodedKeySpec(par0ArrayOfByte);
            final KeyFactory keyfactory = KeyFactory.getInstance("RSA");
            return keyfactory.generatePublic(x509encodedkeyspec);
        }
        catch (final NoSuchAlgorithmException nosuchalgorithmexception)
        {
            nosuchalgorithmexception.printStackTrace();
        }
        catch (final InvalidKeySpecException invalidkeyspecexception)
        {
            invalidkeyspecexception.printStackTrace();
        }

        System.err.println("Public key reconstitute failed!");
        return null;
    }

    /**
     * Decrypt shared secret AES key using RSA private key
     */
    public static SecretKey decryptSharedKey(final PrivateKey par0PrivateKey, final byte[] par1ArrayOfByte)
    {
        return new SecretKeySpec(decryptData(par0PrivateKey, par1ArrayOfByte), "AES");
    }

    @SideOnly(Side.CLIENT)

    /**
     * Encrypt byte[] data with RSA public key
     */
    public static byte[] encryptData(final Key par0Key, final byte[] par1ArrayOfByte)
    {
        return cipherOperation(1, par0Key, par1ArrayOfByte);
    }

    /**
     * Decrypt byte[] data with RSA private key
     */
    public static byte[] decryptData(final Key par0Key, final byte[] par1ArrayOfByte)
    {
        return cipherOperation(2, par0Key, par1ArrayOfByte);
    }

    /**
     * Encrypt or decrypt byte[] data using the specified key
     */
    private static byte[] cipherOperation(final int par0, final Key par1Key, final byte[] par2ArrayOfByte)
    {
        try
        {
            return createTheCipherInstance(par0, par1Key.getAlgorithm(), par1Key).doFinal(par2ArrayOfByte);
        }
        catch (final IllegalBlockSizeException illegalblocksizeexception)
        {
            illegalblocksizeexception.printStackTrace();
        }
        catch (final BadPaddingException badpaddingexception)
        {
            badpaddingexception.printStackTrace();
        }

        System.err.println("Cipher data failed!");
        return null;
    }

    /**
     * Creates the Cipher Instance.
     */
    private static Cipher createTheCipherInstance(final int par0, final String par1Str, final Key par2Key)
    {
        try
        {
            final Cipher cipher = Cipher.getInstance(par1Str);
            cipher.init(par0, par2Key);
            return cipher;
        }
        catch (final InvalidKeyException invalidkeyexception)
        {
            invalidkeyexception.printStackTrace();
        }
        catch (final NoSuchAlgorithmException nosuchalgorithmexception)
        {
            nosuchalgorithmexception.printStackTrace();
        }
        catch (final NoSuchPaddingException nosuchpaddingexception)
        {
            nosuchpaddingexception.printStackTrace();
        }

        System.err.println("Cipher creation failed!");
        return null;
    }

    /**
     * Create a new BufferedBlockCipher instance
     */
    private static BufferedBlockCipher createBufferedBlockCipher(final boolean par0, final Key par1Key)
    {
        final BufferedBlockCipher bufferedblockcipher = new BufferedBlockCipher(new CFBBlockCipher(new AESFastEngine(), 8));
        bufferedblockcipher.init(par0, new ParametersWithIV(new KeyParameter(par1Key.getEncoded()), par1Key.getEncoded(), 0, 16));
        return bufferedblockcipher;
    }

    public static OutputStream encryptOuputStream(final SecretKey par0SecretKey, final OutputStream par1OutputStream)
    {
        return new CipherOutputStream(par1OutputStream, createBufferedBlockCipher(true, par0SecretKey));
    }

    public static InputStream decryptInputStream(final SecretKey par0SecretKey, final InputStream par1InputStream)
    {
        return new CipherInputStream(par1InputStream, createBufferedBlockCipher(false, par0SecretKey));
    }

    static
    {
        Security.addProvider(new BouncyCastleProvider());
    }
}
