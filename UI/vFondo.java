package UI;

import java.awt.*;
import javax.swing.*;

/*
 * Clase VFondo
 *
 * Panel personalizado que actúa como fondo del juego.
 * Carga la imagen "fondo.JPG" desde la carpeta de recursos y la dibuja
 * escalada para cubrir todo el área del panel, sin importar su tamaño.
 */
public class vFondo extends javax.swing.JPanel {

    /**
     * Constructor de VFondo.
     * Inicializa el panel con el tamaño especificado.
     *
     * @param w Ancho del panel en píxeles (generalmente el ancho de la ventana)
     * @param h Alto del panel en píxeles (generalmente el alto de la ventana)
     */
    public vFondo(int w, int h) {
        initComponents(); // Inicializa los componentes generados por el Form Editor
        setSize(w, h); // Establece el tamaño del panel
    }

    /**
     * Sobreescribe el método de pintado del panel para dibujar la imagen de fondo.
     * Este método es llamado automáticamente por Swing cada vez que el panel
     * necesita ser repintado (al abrirse, redimensionarse, etc.).
     *
     * @param g Objeto Graphics que proporciona los métodos de dibujo
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Llama al método padre para limpiar el área antes de dibujar

        // Carga la imagen de fondo desde la carpeta "imagenes" dentro del classpath
        // getClass().getResource() busca el archivo relativo al paquete actual
        Image im = new ImageIcon(
            getClass().getResource("/SourceImages/Background.png")
        ).getImage();

        // Dibuja la imagen escalada para ocupar todo el ancho y alto del panel
        // Parámetros: imagen, posición X, posición Y, ancho, alto, observador de imagen
        g.drawImage(im, 0, 0, getWidth(), getHeight(), null);

        setOpaque(false); // Hace el panel no opaco para que no tape los componentes encima
        setVisible(true); // Asegura que el panel sea visible
    }

    /**
     * Método generado automáticamente por el Form Editor de NetBeans.
     * Configura el layout del panel con un GroupLayout vacío de tamaño 400x300.
     * NO modificar manualmente.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 300, Short.MAX_VALUE)
        );
    } // </editor-fold>
    // Variables declaration - do not modify
    // End of variables declaration
}
