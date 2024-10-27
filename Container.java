
package proyecto1;

import javax.swing.*;
import java.awt.*;

public class Container extends JFrame {
  private JPanel mainPanel;
    private InterfazGrafos interfazGrafos;

    public Container() {
        setTitle("Proyecto de Grafos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel(new BorderLayout());
        interfazGrafos = new InterfazGrafos();

        mainPanel.add(interfazGrafos, BorderLayout.CENTER);
        add(mainPanel);
    }
}