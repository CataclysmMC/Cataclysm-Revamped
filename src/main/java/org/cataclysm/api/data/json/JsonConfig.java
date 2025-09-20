package org.cataclysm.api.data.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility class for persisting and loading data into JSON configuration files.
 * <p>
 * Provides a simple abstraction over {@link JsonObject} and Google Gson to store
 * and retrieve plugin-related or application data as JSON. Files are created automatically
 * if they do not exist, and can be saved/loaded as needed.
 * </p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Creates JSON files in the plugin's data folder or a custom path.</li>
 *   <li>Handles reading and writing using {@link Gson}.</li>
 *   <li>Exposes the internal {@link JsonObject} for flexible manipulation.</li>
 *   <li>Convenience method {@link #cfg(String, JavaPlugin)} to create configs tied to a plugin.</li>
 * </ul>
 *
 * <h2>Example usage:</h2>
 * <pre>{@code
 * JsonConfig config = JsonConfig.cfg("settings.json", myPlugin);
 * config.getJsonObject().addProperty("enabled", true);
 * config.save();
 * }</pre>
 */
public class JsonConfig {
    /** Gson instance configured to serialize nulls. */
    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .create();

    /** The backing JSON object storing the configuration data. */
    private @Getter @Setter JsonObject jsonObject = new JsonObject();

    /** The file where the JSON configuration is stored. */
    private final @Getter File file;

    /**
     * Creates a new JSON configuration file at the given path.
     * If the file does not exist, it will be created and initialized with an empty object.
     *
     * @param filename The name of the JSON file (e.g. {@code config.json}).
     * @param path     The absolute or relative folder path to store the file.
     * @throws Exception If the file cannot be created or read.
     */
    @SuppressWarnings("all")
    public JsonConfig(String filename, String path) throws Exception {
        this.file = new File(path + File.separatorChar + filename);

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            writeFile(file);
        } else {
            readFile(file);
        }
    }

    /**
     * Factory method to create a new {@link JsonConfig} stored inside
     * the given plugin's data folder.
     *
     * @param filename The name of the JSON file.
     * @param plugin   The plugin whose data folder will be used.
     * @return A {@link JsonConfig} object bound to the plugin's data folder.
     * @throws Exception If the file cannot be created or read.
     */
    public static @NotNull JsonConfig cfg(String filename, @NotNull JavaPlugin plugin) throws Exception {
        return new JsonConfig(filename, plugin.getDataFolder().getAbsolutePath());
    }

    /**
     * Creates a new JSON configuration file inside the default "secrets" folder
     * located in the current working directory.
     *
     * @param filename The name of the JSON file.
     * @throws Exception If the file cannot be created or read.
     */
    public JsonConfig(String filename) throws Exception {
        this(filename, System.getProperty("user.dir") + File.separatorChar + "secrets");
    }

    /**
     * Saves the current state of {@link #jsonObject} into the backing file.
     *
     * @throws Exception If the file cannot be written.
     */
    public void save() throws Exception {
        writeFile(file);
    }

    /**
     * Reloads the {@link #jsonObject} from the backing file.
     *
     * @throws Exception If the file cannot be read.
     */
    public void load() throws Exception {
        readFile(file);
    }

    /**
     * Writes the current JSON object to the given file.
     *
     * @param path The file to write to.
     * @throws Exception If writing fails.
     */
    private void writeFile(File path) throws Exception {
        var writer = new FileWriter(path);
        gson.toJson(jsonObject, writer);
        writer.flush();
        writer.close();
    }

    /**
     * Reads the JSON object from the given file and replaces the current {@link #jsonObject}.
     *
     * @param path The file to read from.
     * @throws Exception If reading fails.
     */
    private void readFile(@NotNull File path) throws Exception {
        var reader = Files.newBufferedReader(Paths.get(path.getPath()));
        var object = gson.fromJson(reader, JsonObject.class);
        reader.close();

        jsonObject = object;
    }

    /**
     * Convenience getter for a {@code redisUri} field in the JSON object.
     *
     * @return The Redis URI if present, or {@code null} otherwise.
     */
    public String getRedisUri() {
        var uri = jsonObject.get("redisUri");
        return uri != null ? uri.getAsString() : null;
    }
}