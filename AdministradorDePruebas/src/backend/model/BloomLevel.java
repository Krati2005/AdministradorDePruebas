// backend/model/BloomLevel.java
package backend.model;

/**
 * Representa los niveles de la taxonomía de Bloom.
 * Cada nivel tiene un nombre que describe la habilidad cognitiva.
 */
public enum BloomLevel {
    RECORDAR("Recordar"),
    ENTENDER("Entender"),
    APLICAR("Aplicar"),
    ANALIZAR("Analizar"),
    EVALUAR("Evaluar"),
    CREAR("Crear");

    private final String name;

    /**
     * Constructor para el nivel de Bloom.
     * @param name El nombre del nivel.
     */
    BloomLevel(String name) {
        this.name = name;
    }

    /**
     * Obtiene el nombre del nivel de Bloom.
     * @return El nombre del nivel.
     */
    public String getName() {
        return name;
    }

    public static BloomLevel fromString(String name) {
        if (name == null) {
            return null;
        }
        for (BloomLevel level : BloomLevel.values()) {
            if (level.name.equalsIgnoreCase(name.trim())) {
                return level;
            }
        }
        return null;
    }
}