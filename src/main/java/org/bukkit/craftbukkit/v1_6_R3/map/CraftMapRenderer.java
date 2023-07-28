package org.bukkit.craftbukkit.v1_6_R3.map;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class CraftMapRenderer extends MapRenderer {

    private final net.minecraft.world.storage.MapData worldMap;

    public CraftMapRenderer(final CraftMapView mapView, final net.minecraft.world.storage.MapData worldMap) {
        super(false);
        this.worldMap = worldMap;
    }

    @Override
    public void render(final MapView map, final MapCanvas canvas, final Player player) {
        // Map
        for (int x = 0; x < 128; ++x) {
            for (int y = 0; y < 128; ++y) {
                canvas.setPixel(x, y, worldMap.colors[y * 128 + x]);
            }
        }

        // Cursors
        final MapCursorCollection cursors = canvas.getCursors();
        while (cursors.size() > 0) {
            cursors.removeCursor(cursors.getCursor(0));
        }

        for (final Object key : worldMap.playersVisibleOnMap.keySet()) {
            // If this cursor is for a player check visibility with vanish system
            final Player other = Bukkit.getPlayerExact((String) key);
            if (other != null && !player.canSee(other)) {
                continue;
            }

            final net.minecraft.world.storage.MapCoord decoration = (net.minecraft.world.storage.MapCoord) worldMap.playersVisibleOnMap.get(key);
            cursors.addCursor(decoration.centerX, decoration.centerZ, (byte) (decoration.iconRotation & 15), decoration.iconSize);
        }
    }

}
