import UI.gameFrame;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
            new Runnable() {
                @Override
                public void run() {
                    gameFrame juego = new gameFrame();
                    juego.setVisible(true);
                }
            }
        );
    }
}
