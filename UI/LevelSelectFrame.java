package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Ventana de seleccion de niveles que valida recursos, bloquea niveles y
 * abre una sesion de juego con configuracion parametrizada.
 */
public class LevelSelectFrame extends JFrame {

    private static final int REQUIRED_SCORE_LEVEL_1 = 700;
    private static final int REQUIRED_SCORE_LEVEL_2 = 800;

    private static final Color COLOR_TITLE = Color.WHITE;
    private static final Color COLOR_SUBTITLE = new Color(230, 230, 230);
    private static final Color COLOR_ENABLED_BG = new Color(255, 245, 220);
    private static final Color COLOR_ENABLED_TEXT = new Color(40, 40, 40);
    private static final Color COLOR_DISABLED_BG = new Color(120, 120, 120, 160);
    private static final Color COLOR_DISABLED_TEXT = new Color(210, 210, 210);

    private final LevelConfig[] levels;
    private int unlockedLevel = 1;

    private JButton[] levelButtons;
    private boolean[] levelAvailable;

    public LevelSelectFrame() {
        this.levels = buildLevels();
        this.levelAvailable = computeAvailability();
        this.unlockedLevel = 1;
        configurarVentana();
        inicializarComponentes();
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Define los niveles disponibles con su cantidad de pares.
     */
    private LevelConfig[] buildLevels() {
        return new LevelConfig[] {
            new LevelConfig(1, "Nivel 1", 8),
            new LevelConfig(2, "Nivel 2", 10),
            new LevelConfig(3, "Nivel 3", 12)
        };
    }

    /**
     * Configura el contenedor principal de la ventana de seleccion.
     */
    private void configurarVentana() {
        setTitle("Seleccion de Nivel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    /**
     * Construye el layout y los controles interactivos de la ventana.
     */
    private void inicializarComponentes() {
        Dimension frameSize = new Dimension(640, 600);
        vFondo panelFondo = new vFondo(frameSize.width, frameSize.height);
        panelFondo.setLayout(new BorderLayout(10, 10));
        panelFondo.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
        panelFondo.setPreferredSize(frameSize);

        JPanel panelHeader = new JPanel();
        panelHeader.setOpaque(false);
        panelHeader.setLayout(new GridLayout(3, 1, 4, 4));

        JLabel lblBienvenida = new JLabel("Bienvenida", SwingConstants.CENTER);
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 20));
        lblBienvenida.setForeground(COLOR_TITLE);
        panelHeader.add(lblBienvenida);

        JLabel lblTitulo = new JLabel("Seleccion de nivel", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(COLOR_TITLE);
        panelHeader.add(lblTitulo);

        JLabel lblSubtitulo = new JLabel(
            "Elige el set de imagenes para jugar",
            SwingConstants.CENTER
        );
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitulo.setForeground(COLOR_SUBTITLE);
        panelHeader.add(lblSubtitulo);

        JPanel panelNiveles = new JPanel(new GridLayout(levels.length, 1, 12, 12));
        panelNiveles.setOpaque(false);
        panelNiveles.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        levelButtons = new JButton[levels.length];
        for (int i = 0; i < levels.length; i++) {
            LevelConfig config = levels[i];
            JButton btnNivel = new JButton();
            btnNivel.setFont(new Font("Arial", Font.BOLD, 16));
            btnNivel.setMargin(new Insets(12, 16, 12, 16));
            btnNivel.setFocusPainted(false);
            btnNivel.setContentAreaFilled(true);
            btnNivel.setOpaque(true);
            btnNivel.setBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60), 1)
            );
            final int idx = i;
            btnNivel.addActionListener(e -> seleccionarNivel(idx));
            levelButtons[i] = btnNivel;
            panelNiveles.add(btnNivel);
        }

        panelFondo.add(panelHeader, BorderLayout.NORTH);
        panelFondo.add(panelNiveles, BorderLayout.CENTER);
        setContentPane(panelFondo);

        actualizarEstados();
    }

    /**
     * Actualiza estado visual y habilitacion de cada nivel.
     */
    private void actualizarEstados() {
        for (int i = 0; i < levelButtons.length; i++) {
            boolean unlocked = (i + 1) <= unlockedLevel;
            boolean available = levelAvailable[i];
            boolean enabled = unlocked && available;
            levelButtons[i].setEnabled(enabled);
            levelButtons[i].setForeground(
                enabled ? COLOR_ENABLED_TEXT : COLOR_DISABLED_TEXT
            );
            levelButtons[i].setBackground(
                enabled ? COLOR_ENABLED_BG : COLOR_DISABLED_BG
            );
            levelButtons[i].setText(buildButtonText(levels[i], unlocked, available));
        }
    }

    /**
     * Abre la partida del nivel seleccionado si esta desbloqueado y disponible.
     */
    private void seleccionarNivel(int index) {
        LevelConfig config = levels[index];
        if ((index + 1) > unlockedLevel) {
            return;
        }
        if (!levelAvailable[index]) {
            return;
        }

        gameFrame juego = new gameFrame(
            config,
            requiredScoreFor(config),
            score -> onLevelCompleted(config, score),
            this::returnToMenu
        );
        juego.addWindowListener(
            new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    setVisible(true);
                }
            }
        );
        juego.setVisible(true);
        setVisible(false);
    }

    /**
     * Regresa al menu de niveles despues de cerrar la partida.
     */
    private void returnToMenu() {
        actualizarEstados();
        setVisible(true);
    }

    /**
     * Evalua el puntaje final y desbloquea el siguiente nivel.
     */
    private void onLevelCompleted(LevelConfig config, int finalScore) {
        int nextLevel = config.getLevelId() + 1;
        if (
            finalScore >= requiredScoreFor(config) &&
            nextLevel > unlockedLevel &&
            nextLevel <= levels.length
        ) {
            unlockedLevel = nextLevel;
        }
        actualizarEstados();
        setVisible(true);
    }

    /**
     * Calcula disponibilidad segun recursos de imagen requeridos.
     */
    private boolean[] computeAvailability() {
        boolean[] available = new boolean[levels.length];
        for (int i = 0; i < levels.length; i++) {
            available[i] = hasRequiredImages(levels[i]);
        }
        return available;
    }

    /**
     * Verifica la existencia de imagenes necesarias para el nivel.
     */
    private boolean hasRequiredImages(LevelConfig config) {
        for (int i = 1; i <= config.getPairCount(); i++) {
            URL url = getClass().getResource("/SourceImages/" + i + ".png");
            if (url == null) {
                return false;
            }
        }
        URL fondo = getClass().getResource("/SourceImages/Background.png");
        URL joker = getClass().getResource("/SourceImages/Joker.png");
        return fondo != null && joker != null;
    }

    /**
     * Construye el texto del boton con estado de bloqueo/disponibilidad.
     */
    private String buildButtonText(
        LevelConfig config,
        boolean unlocked,
        boolean available
    ) {
        String base = config.getLabel() + " - " + config.getCardCount() + " cartas";
        if (!available) {
            return base + " (No disponible)";
        }
        if (!unlocked) {
            return base + " (Bloqueado)";
        }
        return base;
    }

    /**
     * Devuelve el puntaje minimo exigido para aprobar el nivel.
     */
    private int requiredScoreFor(LevelConfig config) {
        if (config.getLevelId() <= 1) {
            return REQUIRED_SCORE_LEVEL_1;
        }
        return REQUIRED_SCORE_LEVEL_2;
    }
}
