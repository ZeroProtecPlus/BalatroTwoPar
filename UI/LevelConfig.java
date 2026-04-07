package UI;

/**
 * Configuracion inmutable de un nivel: etiqueta y tamanio del tablero.
 */
public class LevelConfig {

    private final int levelId;
    private final String label;
    private final int cardCount;
    private final int pairCount;

    /**
     * Construye la configuracion a partir del numero de pares.
     */
    public LevelConfig(int levelId, String label, int pairCount) {
        this.levelId = levelId;
        this.label = label;
        this.pairCount = pairCount;
        this.cardCount = pairCount * 2;
    }

    /**
     * Identificador numerico del nivel.
     */
    public int getLevelId() {
        return levelId;
    }

    /**
     * Texto mostrado en la seleccion de nivel.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Numero total de cartas (pares * 2).
     */
    public int getCardCount() {
        return cardCount;
    }

    /**
     * Numero de pares utilizados en la partida.
     */
    public int getPairCount() {
        return pairCount;
    }

    /**
     * Configuracion por defecto usada en modo compatibilidad.
     */
    public static LevelConfig defaultLevel() {
        return new LevelConfig(1, "Nivel 1", 8);
    }
}
