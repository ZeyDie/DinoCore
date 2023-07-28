package org.bukkit.craftbukkit.v1_6_R3.map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.util.*;

public final class CraftMapView implements MapView {

    private final Map<CraftPlayer, RenderData> renderCache = new HashMap<CraftPlayer, RenderData>();
    public final List<MapRenderer> renderers = new ArrayList<MapRenderer>(); // Spigot
    private final Map<MapRenderer, Map<CraftPlayer, CraftMapCanvas>> canvases = new HashMap<MapRenderer, Map<CraftPlayer, CraftMapCanvas>>();
    protected final net.minecraft.world.storage.MapData worldMap;

    public CraftMapView(final net.minecraft.world.storage.MapData worldMap) {
        this.worldMap = worldMap;
        addRenderer(new CraftMapRenderer(this, worldMap));
    }

    public short getId() {
        final String text = worldMap.mapName;
        if (text.startsWith("map_")) {
            try {
                return Short.parseShort(text.substring("map_".length()));
            }
            catch (final NumberFormatException ex) {
                throw new IllegalStateException("Map has non-numeric ID");
            }
        } else {
            throw new IllegalStateException("Map has invalid ID");
        }
    }

    public boolean isVirtual() {
        return !renderers.isEmpty() && !(renderers.get(0) instanceof CraftMapRenderer);
    }

    public Scale getScale() {
        return Scale.valueOf(worldMap.scale);
    }

    public void setScale(final Scale scale) {
        worldMap.scale = scale.getValue();
    }

    public World getWorld() {
        final int dimension = worldMap.dimension; // Cauldron - byte -> int for Forge
        for (final World world : Bukkit.getServer().getWorlds()) {
            if (((CraftWorld) world).getHandle().provider.dimensionId == dimension) {
                return world;
            }
        }
        return null;
    }

    public void setWorld(final World world) {
        worldMap.dimension = (byte) ((CraftWorld) world).getHandle().provider.dimensionId;
    }

    public int getCenterX() {
        return worldMap.xCenter;
    }

    public int getCenterZ() {
        return worldMap.zCenter;
    }

    public void setCenterX(final int x) {
        worldMap.xCenter = x;
    }

    public void setCenterZ(final int z) {
        worldMap.zCenter = z;
    }

    public List<MapRenderer> getRenderers() {
        return new ArrayList<MapRenderer>(renderers);
    }

    public void addRenderer(final MapRenderer renderer) {
        if (!renderers.contains(renderer)) {
            renderers.add(renderer);
            canvases.put(renderer, new HashMap<CraftPlayer, CraftMapCanvas>());
            renderer.initialize(this);
        }
    }

    public boolean removeRenderer(final MapRenderer renderer) {
        if (renderers.contains(renderer)) {
            renderers.remove(renderer);
            for (final Map.Entry<CraftPlayer, CraftMapCanvas> entry : canvases.get(renderer).entrySet()) {
                for (int x = 0; x < 128; ++x) {
                    for (int y = 0; y < 128; ++y) {
                        entry.getValue().setPixel(x, y, (byte) -1);
                    }
                }
            }
            canvases.remove(renderer);
            return true;
        } else {
            return false;
        }
    }

    private boolean isContextual() {
        for (final MapRenderer renderer : renderers) {
            if (renderer.isContextual()) return true;
        }
        return false;
    }

    public RenderData render(final CraftPlayer player) {
        final boolean context = isContextual();
        RenderData render = renderCache.get(context ? player : null);

        if (render == null) {
            render = new RenderData();
            renderCache.put(context ? player : null, render);
        }

        if (context && renderCache.containsKey(null)) {
            renderCache.remove(null);
        }

        Arrays.fill(render.buffer, (byte) 0);
        render.cursors.clear();

        for (final MapRenderer renderer : renderers) {
            CraftMapCanvas canvas = canvases.get(renderer).get(renderer.isContextual() ? player : null);
            if (canvas == null) {
                canvas = new CraftMapCanvas(this);
                canvases.get(renderer).put(renderer.isContextual() ? player : null, canvas);
            }

            canvas.setBase(render.buffer);
            renderer.render(this, canvas, player);

            final byte[] buf = canvas.getBuffer();
            for (int i = 0; i < buf.length; ++i) {
                if (buf[i] >= 0) render.buffer[i] = buf[i];
            }

            for (int i = 0; i < canvas.getCursors().size(); ++i) {
                render.cursors.add(canvas.getCursors().getCursor(i));
            }
        }

        return render;
    }

}
