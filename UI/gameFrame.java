package UI;

import LogicGame.logicGame;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Clase principal de la interfaz de usuario que extiende de JFrame (Ventana).
 * Maneja tanto la parte visual como la interacción directa con el usuario.
 */
public class gameFrame extends JFrame {

    // --- Componentes de la Interfaz ---
    private JPanel panelCartas; // Contenedor para la cuadrícula de botones
    private JButton[] cartas; // Arreglo de 16 botones que representan las cartas
    private JButton btnReiniciar; // Botón para resetear el juego
    private JLabel lblTitulo; // Etiqueta para mostrar pares encontrados
    private JLabel lblPuntuacion; // Etiqueta para mostrar el puntaje actual

    // --- Variables de Estado del Juego ---
    private int puntuacion = 20; // Puntaje inicial
    private logicGame log = new logicGame(); // Instancia de la lógica del juego (mezcla, validación)

    private int primerIndex = -1; // Guarda el índice de la primera carta volteada (-1 = ninguna)
    private int paresEncontrados = 0; // Contador de parejas logradas
    private boolean esperando = false; // Bloqueo para evitar clicks mientras se muestran cartas erróneas

    // --- Constantes y Recursos Visuales ---
    private static final int CARTA_W = 142; // Ancho de la carta
    private static final int CARTA_H = 190; // Alto de la carta
    private ImageIcon[] imagenesCartas = new ImageIcon[9]; // Almacena las imágenes frontales (1-8)
    private ImageIcon imagenTapada; // Almacena la imagen del reverso (Joker)

    /**
     * Constructor: Configura el orden de encendido de la aplicación.
     */
    public gameFrame() {
        cargarImagenes(); // 1. Cargar fotos de disco a memoria
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
     * Llena el arreglo de imágenes con los archivos locales.
     */
    private void cargarImagenes() {
        imagenTapada = escalarIcono("/SourceImages/Joker.png");
        for (int i = 1; i <= 8; i++) {
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
     * Crea toda la estructura visual usando Layouts.
     */
    private void inicializarComponentes() {
        // --- Cálculos de Dimensiones ---
        int cols = 4,
            rows = 4,
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

        lblTitulo = new JLabel("Pares: 0 / 8", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);

        lblPuntuacion = new JLabel("Puntos: 20", SwingConstants.RIGHT);
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

        cartas = new JButton[16];
        for (int i = 0; i < 16; i++) {
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
     * Lógica principal al hacer click en una carta.
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
                puntuacion += 100;
                lblPuntuacion.setText("Puntos: " + puntuacion);
                lblTitulo.setText("Pares: " + paresEncontrados + " / 8");

                // Breve pausa para que el usuario vea la carta antes de desactivarla
                Timer timer = new Timer(600, e -> {
                    cartas[primerIndex].setEnabled(false); // "Quitar" del juego
                    cartas[segundoIndex].setEnabled(false);
                    primerIndex = -1;
                    esperando = false;

                    if (paresEncontrados == 8) {
                        JOptionPane.showMessageDialog(
                            this,
                            "¡Victoria!\nPuntuación: " + puntuacion
                        );
                    }
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                // --- CASO: ERROR ---
                puntuacion -= 10;
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
     * Cambia el icono del botón por la imagen que corresponde según la lógica.
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
     * Restablece todas las variables y la interfaz al estado inicial.
     */
    private void reiniciarJuego() {
        log.reiniciar(); // Mezcla las cartas de nuevo en la lógica
        paresEncontrados = 0;
        primerIndex = -1;
        esperando = false;
        puntuacion = 20;

        lblPuntuacion.setText("Puntos: 20");
        lblTitulo.setText("¡Concéntrese!");

        for (int i = 0; i < 16; i++) {
            cartas[i].setEnabled(true);
            tapar(i);
        }
    }
}
