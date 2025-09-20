package org.cataclysm.api.data;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.Cataclysm;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Utilidad estática para simplificar el acceso a {@link PersistentDataContainer}
 * de Bukkit/Paper mediante claves {@link NamespacedKey}.
 * <p>
 * Proporciona operaciones genéricas de lectura, escritura, comprobación y
 * comparación de valores almacenados en el contenedor persistente de
 * cualquier {@link PersistentDataHolder} (por ejemplo, entidades, items, bloques).
 * </p>
 *
 * <h2>Uso básico</h2>
 * <pre>{@code
 * // Escribir un entero
 * PersistentData.set(player, "kills", PersistentDataType.INTEGER, 5);
 *
 * // Leer un entero (o null si no existe)
 * Integer kills = PersistentData.get(player, "kills", PersistentDataType.INTEGER);
 *
 * // Comprobar si existe
 * boolean hasKills = PersistentData.has(player, "kills", PersistentDataType.INTEGER);
 *
 * // Comparar valor actual con uno esperado
 * boolean isFive = PersistentData.equals(player, "kills", PersistentDataType.INTEGER, 5);
 * }</pre>
 *
 * <p><b>Nota:</b> Las claves se generan bajo el {@code NamespacedKey} del plugin
 * principal ({@link Cataclysm#getInstance()}).</p>
 */
public class PersistentData {

    /**
     * Obtiene el valor almacenado para la clave y tipo indicados.
     *
     * @param holder el {@link PersistentDataHolder} que posee el contenedor
     * @param key    nombre legible de la clave (se namespacéa con el plugin)
     * @param type   tipo persistente (ver {@link PersistentDataType})
     * @param <T>    tipo primario almacenado (en crudo)
     * @param <Z>    tipo envuelto devuelto por el contenedor
     * @return el valor encontrado o {@code null} si no existe o no coincide el tipo
     */
    @Nullable
    public static <T, Z> Z get(PersistentDataHolder holder, String key, PersistentDataType<T, Z> type) {
        if (!PersistentData.has(holder, key, type)) return null;
        return PersistentData.getDataContainer(holder).get(PersistentData.key(key), type);
    }

    /**
     * Comprueba si existe un valor para la clave y tipo indicados.
     *
     * @param holder el {@link PersistentDataHolder}
     * @param key    nombre legible de la clave
     * @param type   tipo persistente
     * @param <T>    tipo primario
     * @param <Z>    tipo envuelto
     * @return {@code true} si existe un valor para esa clave y tipo; en caso contrario {@code false}
     */
    public static <T, Z> boolean has(PersistentDataHolder holder, String key, PersistentDataType<T, Z> type) {
        if (holder == null) return false;
        return holder.getPersistentDataContainer().has(PersistentData.key(key), type);
    }

    /**
     * Compara el valor almacenado con un valor esperado.
     *
     * @param holder el {@link PersistentDataHolder}
     * @param key    nombre legible de la clave
     * @param type   tipo persistente
     * @param value  valor esperado (puede ser {@code null})
     * @param <T>    tipo primario
     * @param <Z>    tipo envuelto
     * @return {@code true} si el valor almacenado es igual al esperado; {@code false} en caso contrario
     */
    public static <T, Z> boolean equals(PersistentDataHolder holder, String key, PersistentDataType<T, Z> type, Z value) {
        if (!PersistentData.has(holder, key, type)) return false;
        return Objects.equals(PersistentData.get(holder, key, type), value);
    }

    /**
     * Establece (crea o sobrescribe) el valor para la clave y tipo indicados.
     *
     * @param holder el {@link PersistentDataHolder}
     * @param key    nombre legible de la clave
     * @param type   tipo persistente
     * @param value  valor a guardar
     * @param <T>    tipo primario
     * @param <Z>    tipo envuelto
     */
    public static <T, Z> void set(PersistentDataHolder holder, String key, PersistentDataType<T, Z> type, Z value) {
        PersistentData.getDataContainer(holder).set(PersistentData.key(key), type, value);
    }

    /**
     * Crea un {@link NamespacedKey} para este plugin usando el nombre dado.
     * <p>
     * El namespace utilizado es el del plugin principal obtenido por {@link Cataclysm#getInstance()}.
     * </p>
     *
     * @param string nombre legible de la clave (sin namespace)
     * @return una nueva instancia de {@link NamespacedKey}
     */
    @NotNull
    @Contract("_ -> new")
    public static NamespacedKey key(String string) {
        return new NamespacedKey(Cataclysm.getInstance(), string);
    }

    /**
     * Devuelve el {@link PersistentDataContainer} asociado al holder.
     *
     * @param holder el {@link PersistentDataHolder}
     * @return el contenedor persistente del holder
     */
    @NotNull
    private static PersistentDataContainer getDataContainer(@NotNull PersistentDataHolder holder) {
        return holder.getPersistentDataContainer();
    }

}