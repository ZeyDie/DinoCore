package net.minecraftforge.client.model.obj;

import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.IModelCustomLoader;
import net.minecraftforge.client.model.ModelFormatException;

import java.net.URL;

public class ObjModelLoader implements IModelCustomLoader {

    @Override
    public String getType()
    {
        return "OBJ model";
    }

    private static final String[] types = { "obj" };
    @Override
    public String[] getSuffixes()
    {
        return types;
    }

    @Override
    public IModelCustom loadInstance(final String resourceName, final URL resource) throws ModelFormatException
    {
        return new WavefrontObject(resourceName, resource);
    }

}
