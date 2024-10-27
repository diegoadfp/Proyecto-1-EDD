
package proyecto1;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.json.JSONArray;
import org.json.JSONObject;

public class InterfazGrafos extends JPanel {
    private Grafo grafo;
    private Graph graph;
    private int t;
    private Set<String> sucursales;

    public InterfazGrafos() {
        setLayout(new BorderLayout());
        t = 3;  // Valor inicial de t para Caracas
        sucursales = new HashSet<>();
        initComponents();
    }

    private void initComponents() {
    // Panel superior para botones de carga y agregar sucursales
    JPanel topPanel = new JPanel(new FlowLayout());
    JButton btnCargar = new JButton("Cargar Archivo");
    btnCargar.addActionListener(evt -> cargarArchivo());
    topPanel.add(btnCargar);

    JButton btnAgregarSucursal = new JButton("Agregar Sucursal");
    btnAgregarSucursal.addActionListener(evt -> agregarSucursal());
    topPanel.add(btnAgregarSucursal);

     JButton btnMostrarCobertura = new JButton("Mostrar Cobertura");
        btnMostrarCobertura.addActionListener(evt -> seleccionarYSacarCobertura());
        topPanel.add(btnMostrarCobertura);
    
    add(topPanel, BorderLayout.NORTH);

    // Slider para cambiar el valor de 't'
    JSlider sliderT = new JSlider(1, 10, t);
    sliderT.addChangeListener(e -> cambiarValorT(sliderT.getValue()));
    add(sliderT, BorderLayout.SOUTH);
}

    private void cargarArchivo() {
    JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                grafo = new Grafo();
                StringBuilder jsonContent = new StringBuilder();
                String linea;
                while ((linea = br.readLine()) != null) {
                    jsonContent.append(linea);
                }
                System.out.println("Contenido del archivo JSON: " + jsonContent.toString());
                JSONObject jsonObject = new JSONObject(jsonContent.toString());
                if (jsonObject.has("Metro de Caracas")) {
                    JSONArray lineas = jsonObject.getJSONArray("Metro de Caracas");
                    for (int j = 0; j < lineas.length(); j++) {
                        JSONObject lineaObject = lineas.getJSONObject(j);
                        for (String lineaNombre : lineaObject.keySet()) {
                            JSONArray paradas = lineaObject.getJSONArray(lineaNombre);
                            for (int i = 0; i < paradas.length(); i++) {
                                Object paradaObj = paradas.get(i);
                                if (paradaObj instanceof String) {
                                    String parada = (String) paradaObj;
                                    grafo.agregarNodo(parada);
                                } else if (paradaObj instanceof JSONObject) {
                                    JSONObject parada = (JSONObject) paradaObj;
                                    for (String paradaInicio : parada.keySet()) {
                                        String paradaFin = parada.getString(paradaInicio);
                                        grafo.agregarNodo(paradaInicio);
                                        grafo.agregarNodo(paradaFin);
                                        grafo.agregarArista(paradaInicio, paradaFin);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    System.out.println("La clave 'Metro de Caracas' no se encontró en el archivo JSON.");
                }
                visualizarGrafo();
            } catch (IOException | org.json.JSONException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void visualizarGrafo(){
    graph = new SingleGraph("Grafo");

        // Añadir nodos
        for (String nodo : grafo.getNodos()) {
            if (graph.getNode(nodo) == null) {
                Node node = graph.addNode(nodo);
                node.setAttribute("ui.label", nodo);
                node.setAttribute("ui.class", "parada");
            }
        }

        // Añadir aristas
        for (Arista arista : grafo.getAristas()) {
            String id = arista.getInicio() + "-" + arista.getFin();
            String reverseId = arista.getFin() + "-" + arista.getInicio();
            if (graph.getEdge(id) == null && graph.getEdge(reverseId) == null) {
                try {
                    graph.addEdge(id, arista.getInicio(), arista.getFin());
                } catch (org.graphstream.graph.IdAlreadyInUseException | org.graphstream.graph.EdgeRejectedException e) {
                    System.err.println("Arista duplicada o rechazada: " + id);
                }
            }
        }

        // Configuración del estilo del grafo
        graph.setAttribute("ui.stylesheet", 
        "node.parada { fill-color: blue; size: 20px; text-mode: normal; text-color: black; text-size: 16px; } " +
        "node.sucursal { fill-color: green; size: 20px; text-mode: normal; text-color: black; text-size: 16px; } " +
        "edge { fill-color: grey; }");

        // Mostrar el grafo con SwingViewer
        Viewer viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();
        ViewPanel viewPanel = (ViewPanel) viewer.addDefaultView(false);
        viewPanel.setPreferredSize(new Dimension(800, 600));
        add(viewPanel, BorderLayout.CENTER);
        revalidate();
}

    private void cambiarValorT(int nuevoValor) {
    t = nuevoValor;

   }  

    private void agregarSucursal() {
    String nodo = JOptionPane.showInputDialog(this, "Ingrese el nombre de la sucursal:");
    if (nodo != null) {
        if (graph.getNode(nodo) == null) {
            Node newNode = graph.addNode(nodo);
            newNode.setAttribute("ui.label", nodo);
            seleccionarParada(nodo);
        } else {
            seleccionarParada(nodo);
        }
    } else {
        JOptionPane.showMessageDialog(this, "Nombre inválido.");
    }
}
 
    private void seleccionarYSacarCobertura() {
        String nodo = JOptionPane.showInputDialog(this, "Ingrese el nombre de la sucursal:");
        if (nodo != null && graph.getNode(nodo) != null) {
            mostrarCobertura(nodo);
        } else {
            JOptionPane.showMessageDialog(this, "Nodo no encontrado o nombre inválido.");
        }
    }
    
    private void seleccionarParada(String nodo) {
    Node node = graph.getNode(nodo);
    if (node != null) {
        node.setAttribute("ui.class", "sucursal");
        node.setAttribute("ui.style", "fill-color: green;");
        sucursales.add(nodo);
        mostrarCobertura(nodo);
    }
 }

    private void mostrarCobertura(String nodoSucursal) {
      Set<String> cubiertas = obtenerParadasCubiertas(nodoSucursal);
        StringBuilder paradasCubiertas = new StringBuilder("Paradas cubiertas: \n");

        for (String parada : grafo.getNodos()) {
            Node node = graph.getNode(parada);
            if (cubiertas.contains(parada)) {
                node.setAttribute("ui.style", "fill-color: yellow; size: 20px; text-color: black; text-size: 16px; text-alignment: under;");
                paradasCubiertas.append(parada).append("\n");
            } else {
                if (!sucursales.contains(parada)) {
                    node.setAttribute("ui.style", "fill-color: blue; size: 20px; text-color: black; text-size: 16px; text-alignment: under;");
                }
            }
        }

        // Mostrar las paradas cubiertas en un recuadro
        JOptionPane.showMessageDialog(this, paradasCubiertas.toString(), "Cobertura de " + nodoSucursal, JOptionPane.INFORMATION_MESSAGE);

        // Mostrar área circular de cobertura
        Node sucursalNode = graph.getNode(nodoSucursal);
        if (sucursalNode != null) {
            sucursalNode.setAttribute("ui.style", "fill-color: green; size: 20px; text-color: black; text-size: 16px; text-alignment: under;");
            sucursalNode.setAttribute("xyz", 0, 0, 0); // Asegurarse de que esté centrado

            // Dibujar un círculo alrededor de la sucursal para indicar el área de cobertura
            for (double angle = 0; angle < 360; angle += 10) {
                double x = Math.cos(Math.toRadians(angle)) * t;
                double y = Math.sin(Math.toRadians(angle)) * t;
                String edgeId = nodoSucursal + "_circle_" + angle;
                graph.addEdge(edgeId, nodoSucursal, nodoSucursal).setAttribute("ui.style", "size: 1px; fill-color: rgba(0, 255, 0, 0.5);");
            }
        }
    }

    private Set<String> obtenerParadasCubiertas(String sucursal) {
        Set<String> cubiertas = new HashSet<>();
        for (String nodo : grafo.getNodos()) {
            if (calcularDistancia(sucursal, nodo) <= t) {
                cubiertas.add(nodo);
            }
        }
        return cubiertas;
    }

    private int calcularDistancia(String inicio, String fin) {
        // Implementar BFS para calcular la distancia mínima entre dos nodos
    if (inicio.equals(fin)) {
        return 0;
    }
    Queue<String> cola = new LinkedList<>();
    Set<String> visitados = new HashSet<>();
    Map<String, Integer> distancias = new HashMap<>();
    cola.add(inicio);
    visitados.add(inicio);
    distancias.put(inicio, 0);
    while (!cola.isEmpty()) {
        String nodoActual = cola.poll();
        int distanciaActual = distancias.get(nodoActual);
        for (String vecino : grafo.getVecinos(nodoActual)) {
            if (!visitados.contains(vecino)) {
                visitados.add(vecino);
                distancias.put(vecino, distanciaActual + 1);
                cola.add(vecino);
                if (vecino.equals(fin)) {
                    return distancias.get(vecino);
                }
            }
        }
    }
    return Integer.MAX_VALUE; // Si no se encuentra un camino, devolver un valor grande
}

    private void verificarCoberturaTotal() {
      Set<String> paradasCubiertas = new HashSet<>();
        for (String sucursal : sucursales) {
            paradasCubiertas.addAll(obtenerParadasCubiertas(sucursal));
        }
        if (paradasCubiertas.containsAll(grafo.getNodos())) {
            System.out.println("Todas las paradas están cubiertas.");
        } else {
            System.out.println("No todas las paradas están cubiertas.");
        }
    }
}
    
    
 
