package LogicGame;

import java.util.Random;

public class logicGame {

    private int[] cardNumbers;

    public logicGame() {
        cardNumbers = generateCardNumbers();
    }

    public int[] generateCardNumbers() {
        int[] numbers = new int[16];

        int count = 0;

        while (count < 16) {
            Random r = new Random();

            int na = r.nextInt(8) + 1;

            int nvr = 0;

            for (int i = 0; i < 16; i++) {
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

    // Devuelve el arreglo actual de cartas
    public int[] getCardNumbers() {
        return cardNumbers;
    }

    // Reinicia el juego generando nuevos valores
    public void reiniciar() {
        cardNumbers = generateCardNumbers();
    }

    // Verifica si dos índices son pareja
    public boolean esPar(int indexA, int indexB) {
        return cardNumbers[indexA] == cardNumbers[indexB];
    }
}
