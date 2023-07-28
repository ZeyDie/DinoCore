package org.bukkit.craftbukkit.v1_6_R3.updater;

import com.google.gson.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BukkitDLUpdaterService {
    private static final String API_PREFIX_ARTIFACT = "/api/1.0/downloads/projects/craftbukkit/view/";
    private static final String API_PREFIX_CHANNEL = "/api/1.0/downloads/channels/";
    private static final DateDeserializer dateDeserializer = new DateDeserializer();
    private final String host;

    public BukkitDLUpdaterService(final String host) {
        this.host = host;
    }

    public ArtifactDetails getArtifact(final String slug, final String name) {
        try {
            return fetchArtifact(slug);
        } catch (final UnsupportedEncodingException ex) {
            Logger.getLogger(BukkitDLUpdaterService.class.getName()).log(Level.WARNING, "Could not get " + name + ": " + ex.getClass().getSimpleName());
        } catch (final IOException ex) {
            Logger.getLogger(BukkitDLUpdaterService.class.getName()).log(Level.WARNING, "Could not get " + name + ": " + ex.getClass().getSimpleName());
        }

        return null;
    }

    private String getUserAgent() {
         return "CraftBukkit/" + BukkitDLUpdaterService.class.getPackage().getImplementationVersion() + "/" + System.getProperty("java.version");
    }

    public ArtifactDetails fetchArtifact(final String slug) throws IOException {
        final URL url = new URL("http", host, API_PREFIX_ARTIFACT + slug + "/");
        InputStreamReader reader = null;

        try {
            final URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", getUserAgent());
            reader = new InputStreamReader(connection.getInputStream());
            final Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, dateDeserializer).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
            return gson.fromJson(reader, ArtifactDetails.class);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public ArtifactDetails.ChannelDetails getChannel(final String slug, final String name) {
        try {
            return fetchChannel(slug);
        } catch (final UnsupportedEncodingException ex) {
            Logger.getLogger(BukkitDLUpdaterService.class.getName()).log(Level.WARNING, "Could not get " + name + ": " + ex.getClass().getSimpleName());
        } catch (final IOException ex) {
            Logger.getLogger(BukkitDLUpdaterService.class.getName()).log(Level.WARNING, "Could not get " + name + ": " + ex.getClass().getSimpleName());
        }

        return null;
    }

    public ArtifactDetails.ChannelDetails fetchChannel(final String slug) throws IOException {
        final URL url = new URL("http", host, API_PREFIX_CHANNEL + slug + "/");
        InputStreamReader reader = null;

        try {
            final URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", getUserAgent());
            reader = new InputStreamReader(connection.getInputStream());
            final Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, dateDeserializer).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

            return gson.fromJson(reader, ArtifactDetails.ChannelDetails.class);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    static class DateDeserializer implements JsonDeserializer<Date> {
        private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        public Date deserialize(final JsonElement je, final Type type, final JsonDeserializationContext jdc) throws JsonParseException {
            try {
                return format.parse(je.getAsString());
            } catch (final ParseException ex) {
                throw new JsonParseException("Date is not formatted correctly", ex);
            }
        }
    }
}
