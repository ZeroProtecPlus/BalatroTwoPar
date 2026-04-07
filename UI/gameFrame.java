package UI;

import LogicGame.logicGame;
import java.awt.*;
import java.awt.event.*;
import java.util.function.IntConsumer;
import javax.swing.*;

/**
 * Ventana principal del juego: renderiza el tablero, aplica puntajes y
 * coordina la logica de parejas segun la configuracion del nivel.
 */
public class gameFrame extends JFrame {

    // --- Componentes de la Interfaz ---
    private JPanel panelCartas; // Contenedor para la cuadrícula de botones
    private JButton[] cartas; // Arreglo de botones que representan las cartas
    private JButton btnReiniciar; // Botón para resetear el juego
    private JLabel lblTitulo; // Etiqueta para mostrar pares encontrados
    private JLabel lblPuntuacion; // Etiqueta para mostrar el puntaje actual

    // --- Variables de Estado del Juego ---
    public static final int BASE_SCORE = 20;
    public static final int SCORE_PER_MATCH = 100;
    public static final int SCORE_PENALTY = 10;

    private int puntuacion = BASE_SCORE; // Puntaje inicial
    private logicGame log; // Instancia de la lógica del juego (mezcla, validación)

    private int primerIndex = -1; // Guarda el índice de la primera carta volteada (-1 = ninguna)
    private int paresEncontrados = 0; // Contador de parejas logradas
    private boolean esperando = false; // Bloqueo para evitar clicks mientras se muestran cartas erróneas

    // --- Constantes y Recursos Visuales ---
    private static final int CARTA_W = 142; // Ancho de la carta
    private static final int CARTA_H = 190; // Alto de la carta
    private ImageIcon[] imagenesCartas; // Almacena las imágenes frontales
    private ImageIcon imagenTapada; // Almacena la imagen del reverso (Joker)

    private final LevelConfig levelConfig;
    private final int requiredScore;
    private final IntConsumer onComplete;
    private final Runnable onExit;

    /**
     * Constructor por defecto para mantener compatibilidad.
     */
    public gameFrame() {
        this(LevelConfig.defaultLevel(), 0, null, null);
    }

    public gameFrame(
        LevelConfig levelConfig,
        int requiredScore,
        IntConsumer onComplete,
        Runnable onExit
    ) {
        this.levelConfig = levelConfig;
        this.requiredScore = requiredScore;
        this.onComplete = onComplete;
        this.onExit = onExit;
        this.log = new logicGame(
            levelConfig.getCardCount(),
            levelConfig.getPairCount()
        );
        cargarImagenes(levelConfig.getPairCount()); // 1. Cargar fotos de disco a memoria
        configurarVentana(); // 2. Título, cierre y redimensión
        inicializarComponentes(); // 3. Crear botones, paneles y labels
        pack(); // 4. Ajustar tamaño de ventana a los elementos
        setLocationRelativeTo(null); // 5. Centrar en pantalla
    }

    /**
     * Carga una imagen, la escala al tamaño de la carta y devuelve un ImageIcon.
     */
    private ImageIcon escalarIcono(String ruta) {
        java.net.URL url = getClass().getResource(ruta);
        if (url == null) {
            System.out.println("Imagen no encontrada: " + ruta);
            return null;
        }
        Image img = new ImageIcon(url)
            .getImage()
            .getScaledInstance(CARTA_W, CARTA_H, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    /**
     * Llena el arreglo de imagenes segun el numero de pares del nivel.
     */
    private void cargarImagenes(int pairCount) {
        imagenTapada = escalarIcono("/SourceImages/Joker.png");
        imagenesCartas = new ImageIcon[pairCount + 1];
        for (int i = 1; i <= pairCount; i++) {
            // Se asume que las fotos se llaman 1.png, 2.png...
            imagenesCartas[i] = escalarIcono("/SourceImages/" + i + ".png");
        }
    }

    private void configurarVentana() {
        setTitle("Concentrese");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    /**
     * Crea la estructura visual y el tablero segun la cantidad de cartas.
     */
    private void inicializarComponentes() {
        // --- Cálculos de Dimensiones ---
        int rows = 4,
            cols = Math.max(1, levelConfig.getCardCount() / rows),
            gap = 10,
            margen = 20;
        int tableroW = cols * CARTA_W + (cols - 1) * gap + margen * 2;
        int tableroH = rows * CARTA_H + (rows - 1) * gap;
        int ventanaW = tableroW + 20;
        int ventanaH = tableroH + 110;

        // Panel principal con fondo (clase personalizada vFondo)
        vFondo panelFondoPrincipal = new vFondo(ventanaW, ventanaH);
        panelFondoPrincipal.setLayout(new BorderLayout(5, 5));
        panelFondoPrincipal.setBorder(
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );
        panelFondoPrincipal.setPreferredSize(new Dimension(ventanaW, ventanaH));

        // --- Panel Superior (Norte): Información de Juego ---
        JPanel panelNorte = new JPanel(new GridLayout(1, 2));
        panelNorte.setOpaque(false);

        lblTitulo = new JLabel(
            "Pares: 0 / " + levelConfig.getPairCount(),
            SwingConstants.LEFT
        );
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);

        lblPuntuacion = new JLabel(
            "Puntos: " + BASE_SCORE,
            SwingConstants.RIGHT
        );
        lblPuntuacion.setFont(new Font("Arial", Font.BOLD, 18));
        lblPuntuacion.setForeground(Color.YELLOW);

        panelNorte.add(lblTitulo);
        panelNorte.add(lblPuntuacion);
        panelFondoPrincipal.add(panelNorte, BorderLayout.NORTH);

        // --- Panel Central: El Tablero de Cartas ---
        panelCartas = new JPanel(new GridLayout(rows, cols, gap, gap));
        panelCartas.setBorder(
            BorderFactory.createEmptyBorder(5, margen, 5, margen)
        );
        panelCartas.setOpaque(false);

        cartas = new JButton[levelConfig.getCardCount()];
        for (int i = 0; i < cartas.length; i++) {
            cartas[i] = new JButton();
            cartas[i].setPreferredSize(new Dimension(CARTA_W, CARTA_H));
            final int idx = i; // Variable final para usarla dentro del Lambda
            cartas[i].addActionListener(e -> manejarClick(idx)); // Evento de click
            panelCartas.add(cartas[i]);
            tapar(i); // Iniciar con el reverso
        }
        panelFondoPrincipal.add(panelCartas, BorderLayout.CENTER);

        // --- Panel Inferior (Sur): Botón Reiniciar ---
        btnReiniciar = new JButton("Reiniciar Juego");
        btnReiniciar.setFont(new Font("Arial", Font.BOLD, 14));
        btnReiniciar.addActionListener(e -> reiniciarJuego());

        JPanel panelSur = new JPanel();
        panelSur.setOpaque(false);
        panelSur.add(btnReiniciar);
        panelFondoPrincipal.add(panelSur, BorderLayout.SOUTH);

        setContentPane(panelFondoPrincipal);
    }

    /**
     * Lógica principal del turno: revela, compara y puntua.
     */
    private void manejarClick(int idx) {
        // Validaciones preventivas
        if (esperando) return; // Si hay un timer corriendo, no hacer nada
        if (!cartas[idx].isEnabled()) return; // Si la carta ya fue encontrada, ignorar
        if (primerIndex == idx) return; // Si hace click en la misma carta ya abierta

        destapar(idx); // Mostrar la imagen frontal

        if (primerIndex == -1) {
            // Es la primera carta que voltea en este turno
            primerIndex = idx;
        } else {
            // Es la segunda carta, hay que comparar
            esperando = true; // Bloquear otros clicks
            int segundoIndex = idx;

            if (log.esPar(primerIndex, segundoIndex)) {
                // --- CASO: ACIERTO ---
                paresEncontrados++;
                puntuacion += SCORE_PER_MATCH;
                lblPuntuacion.setText("Puntos: " + puntuacion);
                lblTitulo.setText(
                    "Pares: " +
                        paresEncontrados +
                        " / " +
                        levelConfig.getPairCount()
                );

                // Breve pausa para que el usuario vea la carta antes de desactivarla
                Timer timer = new Timer(600, e -> {
                    cartas[primerIndex].setEnabled(false); // "Quitar" del juego
                    cartas[segundoIndex].setEnabled(false);
                    primerIndex = -1;
                    esperando = false;

                    if (paresEncontrados == levelConfig.getPairCount()) {
                        String titulo = (puntuacion >= requiredScore)
                            ? "¡Victoria!"
                            : "Derrota";
                        JOptionPane.showMessageDialog(
                            this,
                            titulo + "\nPuntuación: " + puntuacion
                        );
                        if (onComplete != null) {
                            onComplete.accept(puntuacion);
                        }
                        dispose();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                // --- CASO: ERROR ---
                puntuacion -= SCORE_PENALTY;
                lblPuntuacion.setText("Puntos: " + puntuacion);

                // Pausa más larga (900ms) para que memorice el error, luego tapar
                Timer timer = new Timer(900, e -> {
                    tapar(primerIndex);
                    tapar(segundoIndex);
                    primerIndex = -1;
                    esperando = false;
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }

    /**
     * Muestra la imagen correspondiente a la carta destapada.
     */
    private void destapar(int idx) {
        int valor = log.getCardNumbers()[idx]; // Obtener qué número/imagen hay en esa posición
        if (imagenesCartas[valor] != null) {
            cartas[idx].setIcon(imagenesCartas[valor]);
        } else {
            cartas[idx].setText(String.valueOf(valor)); // Backup si fallan las imágenes
        }
    }

    /**
     * Vuelve a poner el reverso de la carta.
     */
    private void tapar(int idx) {
        if (imagenTapada != null) {
            cartas[idx].setIcon(imagenTapada);
        } else {
            cartas[idx].setText("?");
        }
    }

    /**
     * Finaliza la sesion actual y notifica salida al menu.
     */
    private void reiniciarJuego() {
        if (onExit != null) {
            onExit.run();
        }
        dispose();
    }
}
