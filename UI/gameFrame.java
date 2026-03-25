package UI;

import LogicGame.logicGame;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class gameFrame extends JFrame {

    private JPanel panelCartas;
    private JButton[] cartas;
    private JButton btnReiniciar;
    private JLabel lblTitulo;

    // NUEVO: Etiqueta y variable para la puntuación
    private JLabel lblPuntuacion;
    private int puntuacion = 20;

    private logicGame log = new logicGame();

    private int primerIndex = -1;
    private int paresEncontrados = 0;
    private boolean esperando = false;

    // Tamaño de cada carta
    private static final int CARTA_W = 142;
    private static final int CARTA_H = 190;

    private ImageIcon[] imagenesCartas = new ImageIcon[9];
    private ImageIcon imagenTapada;

    public gameFrame() {
        cargarImagenes(); // ← Se llama PRIMERO
        configurarVentana();
        inicializarComponentes();
        pack(); // ← Ajusta la ventana al contenido
        setLocationRelativeTo(null);
    }

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

    private void cargarImagenes() {
        imagenTapada = escalarIcono("/SourceImages/Joker.png");
        for (int i = 1; i <= 8; i++) {
            imagenesCartas[i] = escalarIcono("/SourceImages/" + i + ".png");
        }
    }

    private void configurarVentana() {
        setTitle("Concentrese");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    private void inicializarComponentes() {
        // 1. Ajuste de cálculos de tamaño
        int cols = 4,
            rows = 4,
            gap = 10,
            margen = 20;
        int tableroW = cols * CARTA_W + (cols - 1) * gap + margen * 2;
        int tableroH = rows * CARTA_H + (rows - 1) * gap;
        int ventanaW = tableroW + 20;

        // REDUCIDO: De 150 a 100 para que el botón de abajo suba
        int ventanaH = tableroH + 110;

        vFondo panelFondoPrincipal = new vFondo(ventanaW, ventanaH);
        panelFondoPrincipal.setLayout(new BorderLayout(5, 5)); // Menos espacio entre zonas
        panelFondoPrincipal.setBorder(
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );
        panelFondoPrincipal.setPreferredSize(new Dimension(ventanaW, ventanaH));

        // 2. Panel Norte: CAMBIADO a 1 fila, 2 columnas para ponerlos lado a lado
        JPanel panelNorte = new JPanel(new GridLayout(1, 2));
        panelNorte.setOpaque(false);

        // Título / Pares (Alineado a la izquierda)
        lblTitulo = new JLabel("Pares: 0 / 8", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);

        // Puntuación (Alineado a la derecha)
        lblPuntuacion = new JLabel("Puntos: 20", SwingConstants.RIGHT);
        lblPuntuacion.setFont(new Font("Arial", Font.BOLD, 18));
        lblPuntuacion.setForeground(Color.YELLOW);

        panelNorte.add(lblTitulo);
        panelNorte.add(lblPuntuacion);
        panelFondoPrincipal.add(panelNorte, BorderLayout.NORTH);

        // 3. Tablero de cartas (Sin cambios, solo ajuste de margen)
        panelCartas = new JPanel(new GridLayout(rows, cols, gap, gap));
        panelCartas.setBorder(
            BorderFactory.createEmptyBorder(5, margen, 5, margen)
        );
        panelCartas.setOpaque(false);

        cartas = new JButton[16];
        for (int i = 0; i < 16; i++) {
            cartas[i] = new JButton();
            cartas[i].setPreferredSize(new Dimension(CARTA_W, CARTA_H));
            // ... resto del código de cartas igual ...
            final int idx = i;
            cartas[i].addActionListener(e -> manejarClick(idx));
            panelCartas.add(cartas[i]);
            tapar(i);
        }

        panelFondoPrincipal.add(panelCartas, BorderLayout.CENTER);

        // 4. Panel Sur (Botón Reiniciar)
        btnReiniciar = new JButton("Reiniciar Juego");
        btnReiniciar.setFont(new Font("Arial", Font.BOLD, 14));
        btnReiniciar.addActionListener(e -> reiniciarJuego());

        JPanel panelSur = new JPanel();
        panelSur.setOpaque(false);
        // Margen pequeño para no empujar el botón fuera de la pantalla
        panelSur.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        panelSur.add(btnReiniciar);
        panelFondoPrincipal.add(panelSur, BorderLayout.SOUTH);

        setContentPane(panelFondoPrincipal);
    }

    private void manejarClick(int idx) {
        if (esperando) return;
        if (!cartas[idx].isEnabled()) return;
        if (primerIndex == idx) return;

        destapar(idx);

        if (primerIndex == -1) {
            primerIndex = idx;
        } else {
            esperando = true;
            int segundoIndex = idx;

            if (log.esPar(primerIndex, segundoIndex)) {
                paresEncontrados++;

                // NUEVO: Sumar 100 puntos y actualizar texto
                puntuacion += 100;
                lblPuntuacion.setText("Puntos: " + puntuacion);

                lblTitulo.setText("Pares: " + paresEncontrados + " / 8");

                Timer timer = new Timer(600, e -> {
                    cartas[primerIndex].setEnabled(false);
                    cartas[segundoIndex].setEnabled(false);
                    primerIndex = -1;
                    esperando = false;

                    if (paresEncontrados == 8) {
                        JOptionPane.showMessageDialog(
                            this,
                            "¡Felicitaciones! ¡Encontraste todos los pares!\nPuntuación final: " +
                                puntuacion,
                            "¡Ganaste!",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                // NUEVO: Restar 10 puntos y actualizar texto
                puntuacion -= 10;
                lblPuntuacion.setText("Puntos: " + puntuacion);

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

    private void destapar(int idx) {
        int valor = log.getCardNumbers()[idx];
        if (imagenesCartas[valor] != null) {
            cartas[idx].setIcon(imagenesCartas[valor]);
        } else {
            cartas[idx].setText(String.valueOf(valor));
            cartas[idx].setForeground(Color.WHITE);
        }
    }

    private void tapar(int idx) {
        if (imagenTapada != null) {
            cartas[idx].setIcon(imagenTapada);
        } else {
            cartas[idx].setText("?");
        }
    }

    private void reiniciarJuego() {
        log.reiniciar();
        paresEncontrados = 0;
        primerIndex = -1;
        esperando = false;

        // NUEVO: Reiniciar puntos
        puntuacion = 20;
        lblPuntuacion.setText("Puntos: 20");
        lblTitulo.setText("¡Concéntrese!");

        for (int i = 0; i < 16; i++) {
            cartas[i].setEnabled(true);
            tapar(i);
        }
    }
}
