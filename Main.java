import UI.LevelSelectFrame;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
            new Runnable() {
                @Override
                public void run() {
                    LevelSelectFrame selector = new LevelSelectFrame();
                    selector.setVisible(true);
                }
            }
        );
    }
}
