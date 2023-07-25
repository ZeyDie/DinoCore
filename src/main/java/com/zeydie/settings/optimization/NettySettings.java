package com.zeydie.settings.optimization;

import com.zeydie.settings.AbstractSettings;
import lombok.Data;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public final class NettySettings extends AbstractSettings {
    @NotNull
    private NettySettingsData nettySettingsData = new NettySettingsData();

    public static @NotNull NettySettings getInstance() {
        return MinecraftServer.getServer().nettySettings;
    }

    @Override
    public @NotNull String getConfigName() {
        return "netty";
    }

    @Override
    public @NotNull NettySettingsData getSettings() {
        return this.nettySettingsData;
    }

    @Override
    public void setSettings(@NotNull final Object object) {
        this.nettySettingsData = (NettySettingsData) object;
    }

    @Data
    public static final class NettySettingsData {
        private boolean debug;
        private boolean enable = true;
        private boolean asynchronousPackets;
        private int backlogKB = 1024;
        private int backlogSize = 20;
        private boolean keepAlive = true;
    }
}
