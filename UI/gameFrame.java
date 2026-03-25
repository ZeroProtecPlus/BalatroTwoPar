package UI;

import java.awt.*;
import javax.swing.*;

public class gameFrame extends JFrame {

    private JPanel panelCartas;
    private JButton[] cartas;
    private JButton btnReiniciar;
    private JLabel lblTitulo;

    public gameFrame() {
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("Concentrese");
        setSize(800, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // El setLayout de aquí ya no es estrictamente necesario porque lo reemplazaremos con el contentPane, pero no hace daño.
    }

    private void inicializarComponentes() {
        // 1. CREAMOS EL FONDO PRIMERO para poder pegarle cosas encima
        vFondo panelFondoPrincipal = new vFondo(800, 1000);
        panelFondoPrincipal.setLayout(new java.awt.BorderLayout(10, 10));
        panelFondoPrincipal.setBorder(
            javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        // 2. Zona N, titulo
        lblTitulo = new JLabel("Bienvenido", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE); // Opcional: para que se lea mejor sobre el fondo
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        // CORRECCIÓN 1: Lo agregamos al panelFondoPrincipal
        panelFondoPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // 3. Tablero de cartas
        panelCartas = new JPanel();
        panelCartas.setLayout(new GridLayout(4, 4, 10, 10));
        panelCartas.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        panelCartas.setOpaque(false);

        cartas = new JButton[16];

        for (int i = 0; i < 16; i++) {
            cartas[i] = new JButton();

            java.net.URL imgUrl = getClass().getResource(
                "/SourceImages/Joker.png"
            );

            if (imgUrl != null) {
                cartas[i].setIcon(new ImageIcon(imgUrl));
            } else {
                cartas[i].setText("?");
                System.out.println("No se encontró la imagen");
            }

            cartas[i].setFocusPainted(false);
            cartas[i].setContentAreaFilled(false);
            cartas[i].setBorderPainted(false);

            panelCartas.add(cartas[i]);
        }

        panelFondoPrincipal.add(panelCartas, java.awt.BorderLayout.CENTER);

        // 4. Zona Sur, reiniciar
        btnReiniciar = new JButton("Reiniciar Juego");
        btnReiniciar.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel panelSur = new JPanel();
        panelSur.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // CORRECCIÓN 2: Hacemos el panel del botón transparente
        panelSur.setOpaque(false);
        panelSur.add(btnReiniciar);

        // CORRECCIÓN 3: Lo agregamos al panelFondoPrincipal
        panelFondoPrincipal.add(panelSur, BorderLayout.SOUTH);

        // 5. Establecemos el fondo como la ventana maestra
        setContentPane(panelFondoPrincipal);
    }
}
