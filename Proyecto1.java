
package proyecto1;

import javax.swing.*;

public class Proyecto1 {
    public static void main(String[] args) {
        // Configurar el paquete de UI de GraphStream
        System.setProperty("org.graphstream.ui", "swing");

        java.awt.EventQueue.invokeLater(() -> {
            Container frame = new Container();
            frame.setVisible(true);
        });
    }
}