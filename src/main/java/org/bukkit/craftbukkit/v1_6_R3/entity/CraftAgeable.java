package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.Ageable;

public class CraftAgeable extends CraftCreature implements Ageable {
    public CraftAgeable(final CraftServer server, final net.minecraft.entity.EntityAgeable entity) {
        super(server, entity);
    }

    public int getAge() {
        return getHandle().getGrowingAge();
    }

    public void setAge(final int age) {
        getHandle().setGrowingAge(age);
    }

    public void setAgeLock(final boolean lock) {
        getHandle().ageLocked = lock;
    }

    public boolean getAgeLock() {
        return getHandle().ageLocked;
    }

    public void setBaby() {
        if (isAdult()) {
            setAge(-24000);
        }
    }

    public void setAdult() {
        if (!isAdult()) {
            setAge(0);
        }
    }

    public boolean isAdult() {
        return getAge() >= 0;
    }


    public boolean canBreed() {
        return getAge() == 0;
    }

    public void setBreed(final boolean breed) {
        if (breed) {
            setAge(0);
        } else if (isAdult()) {
            setAge(6000);
        }
    }

    @Override
    public net.minecraft.entity.EntityAgeable getHandle() {
        return (net.minecraft.entity.EntityAgeable) entity;
    }

    @Override
    public String toString() {
        return "CraftAgeable";
    }
}
