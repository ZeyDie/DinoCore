package org.bukkit.craftbukkit.v1_6_R3.updater;

import java.util.Date;

public class ArtifactDetails {
    private String brokenReason;
    private boolean isBroken;
    private int buildNumber;
    private String htmlUrl;
    private String version;
    private Date created;
    private FileDetails file;
    private ChannelDetails channel;

    public ChannelDetails getChannel() {
        return channel;
    }

    public void setChannel(final ChannelDetails channel) {
        this.channel = channel;
    }

    public boolean isIsBroken() {
        return isBroken;
    }

    public void setIsBroken(final boolean isBroken) {
        this.isBroken = isBroken;
    }

    public FileDetails getFile() {
        return file;
    }

    public void setFile(final FileDetails file) {
        this.file = file;
    }

    public String getBrokenReason() {
        return brokenReason;
    }

    public void setBrokenReason(final String brokenReason) {
        this.brokenReason = brokenReason;
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(final int buildNumber) {
        this.buildNumber = buildNumber;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(final Date created) {
        this.created = created;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(final String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public boolean isBroken() {
        return isBroken;
    }

    public void setBroken(final boolean isBroken) {
        this.isBroken = isBroken;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public static class FileDetails {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(final String url) {
            this.url = url;
        }
    }

    public static class ChannelDetails {
        private String name;
        private String slug;
        private int priority;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(final int priority) {
            this.priority = priority;
        }

        public String getSlug() {
            return slug;
        }

        public void setSlug(final String slug) {
            this.slug = slug;
        }
    }
}
