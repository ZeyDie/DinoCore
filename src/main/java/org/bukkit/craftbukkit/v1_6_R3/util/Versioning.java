package org.bukkit.craftbukkit.v1_6_R3.util;

public final class Versioning {
    public static String getBukkitVersion() {
        // Cauldron start - disable file check as we no longer use maven
        /*String result = "Unknown-Version";

        InputStream stream = Bukkit.class.getClassLoader().getResourceAsStream("META-INF/maven/za.co.mcportcentral/cauldron-api/pom.properties");
        Properties properties = new Properties();

        if (stream != null) {
            try {
                properties.load(stream);

                result = properties.getProperty("version");
            } catch (IOException ex) {
                Logger.getLogger(Versioning.class.getName()).log(Level.SEVERE, "Could not get Bukkit version!", ex);
            }
        }

        return result;*/
        return "1.6.4-R2.1-SNAPSHOT"; // return current Bukkit API version used
    }
}
