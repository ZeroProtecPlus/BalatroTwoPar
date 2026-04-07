package LogicGame;

import java.util.Random;

/**
 * Motor de logica del juego: genera el arreglo de pares y valida coincidencias.
 */
public class logicGame {

    private int[] cardNumbers;
    private final int cardCount;
    private final int pairCount;

    public logicGame() {
        this(16, 8);
    }

    public logicGame(int cardCount, int pairCount) {
        this.cardCount = cardCount;
        this.pairCount = pairCount;
        cardNumbers = generateCardNumbers();
    }

    /**
     * Genera un arreglo con pares distribuidos aleatoriamente.
     */
    public int[] generateCardNumbers() {
        int[] numbers = new int[cardCount];

        int count = 0;

        while (count < cardCount) {
            Random r = new Random();

            int na = r.nextInt(pairCount) + 1;

            int nvr = 0;

            for (int i = 0; i < cardCount; i++) {
                if (numbers[i] == na) {
                    nvr++;
                }
            }

            if (nvr < 2) {
                numbers[count] = na;
                count++;
            }
        }

        return numbers;
    }

    /**
     * Devuelve el arreglo actual de cartas.
     */
    public int[] getCardNumbers() {
        return cardNumbers;
    }

    /**
     * Reinicia la partida regenerando el arreglo de cartas.
     */
    public void reiniciar() {
        cardNumbers = generateCardNumbers();
    }

    /**
     * Verifica si dos indices apuntan a la misma imagen.
     */
    public boolean esPar(int indexA, int indexB) {
        return cardNumbers[indexA] == cardNumbers[indexB];
    }
}
