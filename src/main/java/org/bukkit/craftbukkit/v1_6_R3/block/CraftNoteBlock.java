package org.bukkit.craftbukkit.v1_6_R3.block;


import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.NoteBlock;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;

public class CraftNoteBlock extends CraftBlockState implements NoteBlock {
    private final CraftWorld world;
    private final net.minecraft.tileentity.TileEntityNote note;

    public CraftNoteBlock(final Block block) {
        super(block);

        world = (CraftWorld) block.getWorld();
        note = (net.minecraft.tileentity.TileEntityNote) world.getTileEntityAt(getX(), getY(), getZ());
    }

    public Note getNote() {
        return new Note(note.note);
    }

    public byte getRawNote() {
        return note.note;
    }

    public void setNote(final Note n) {
        note.note = n.getId();
    }

    public void setRawNote(final byte n) {
        note.note = n;
    }

    public boolean play() {
        final Block block = getBlock();

        if (block.getType() == Material.NOTE_BLOCK) {
            note.triggerNote(world.getHandle(), getX(), getY(), getZ());
            return true;
        } else {
            return false;
        }
    }

    public boolean play(final byte instrument, final byte note) {
        final Block block = getBlock();

        if (block.getType() == Material.NOTE_BLOCK) {
            world.getHandle().addBlockEvent(getX(), getY(), getZ(), block.getTypeId(), instrument, note);
            return true;
        } else {
            return false;
        }
    }

    public boolean play(final Instrument instrument, final Note note) {
        final Block block = getBlock();

        if (block.getType() == Material.NOTE_BLOCK) {
            world.getHandle().addBlockEvent(getX(), getY(), getZ(), block.getTypeId(), instrument.getType(), note.getId());
            return true;
        } else {
            return false;
        }
    }
}
