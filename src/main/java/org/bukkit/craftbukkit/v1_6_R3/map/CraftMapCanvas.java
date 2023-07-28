package org.bukkit.craftbukkit.v1_6_R3.map;

import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapFont;
import org.bukkit.map.MapFont.CharacterSprite;
import org.bukkit.map.MapPalette;

import java.awt.*;
import java.util.Arrays;

public class CraftMapCanvas implements MapCanvas {

    private final byte[] buffer = new byte[128 * 128];
    private final CraftMapView mapView;
    private byte[] base;
    private MapCursorCollection cursors = new MapCursorCollection();

    protected CraftMapCanvas(final CraftMapView mapView) {
        this.mapView = mapView;
        Arrays.fill(buffer, (byte) -1);
    }

    public CraftMapView getMapView() {
        return mapView;
    }

    public MapCursorCollection getCursors() {
        return cursors;
    }

    public void setCursors(final MapCursorCollection cursors) {
        this.cursors = cursors;
    }

    public void setPixel(final int x, final int y, final byte color) {
        if (x < 0 || y < 0 || x >= 128 || y >= 128)
            return;
        if (buffer[y * 128 + x] != color) {
            buffer[y * 128 + x] = color;
            mapView.worldMap.setColumnDirty(x, y, y);
        }
    }

    public byte getPixel(final int x, final int y) {
        if (x < 0 || y < 0 || x >= 128 || y >= 128)
            return 0;
        return buffer[y * 128 + x];
    }

    public byte getBasePixel(final int x, final int y) {
        if (x < 0 || y < 0 || x >= 128 || y >= 128)
            return 0;
        return base[y * 128 + x];
    }

    protected void setBase(final byte[] base) {
        this.base = base;
    }

    protected byte[] getBuffer() {
        return buffer;
    }

    public void drawImage(final int x, final int y, final Image image) {
        final byte[] bytes = MapPalette.imageToBytes(image);
        for (int x2 = 0; x2 < image.getWidth(null); ++x2) {
            for (int y2 = 0; y2 < image.getHeight(null); ++y2) {
                setPixel(x + x2, y + y2, bytes[y2 * image.getWidth(null) + x2]);
            }
        }
    }

    public void drawText(int x, int y, final MapFont font, final String text) {
        int y1 = y;
        int x1 = x;
        final int xStart = x1;
        byte color = MapPalette.DARK_GRAY;
        if (!font.isValid(text)) {
            throw new IllegalArgumentException("text contains invalid characters");
        }

        for (int i = 0; i < text.length(); ++i) {
            final char ch = text.charAt(i);
            if (ch == '\n') {
                x1 = xStart;
                y1 += font.getHeight() + 1;
                continue;
            } else if (ch == '\u00A7') {
                final int j = text.indexOf(';', i);
                if (j >= 0) {
                    try {
                        color = Byte.parseByte(text.substring(i + 1, j));
                        i = j;
                        continue;
                    }
                    catch (final NumberFormatException ex) {}
                }
            }

            final CharacterSprite sprite = font.getChar(text.charAt(i));
            for (int r = 0; r < font.getHeight(); ++r) {
                for (int c = 0; c < sprite.getWidth(); ++c) {
                    if (sprite.get(r, c)) {
                        setPixel(x1 + c, y1 + r, color);
                    }
                }
            }
            x1 += sprite.getWidth() + 1;
        }
    }

}
