
package proyecto1;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class Grafo {
    private Map<String, List<String>> adjList;

    public Grafo() {
        adjList = new HashMap<>();
    }

    public void agregarNodo(String nodo) {
        adjList.putIfAbsent(nodo, new ArrayList<>());
    }

    public void agregarArista(String inicio, String fin) {
        adjList.get(inicio).add(fin);
        adjList.get(fin).add(inicio);
    }

    public List<String> getVecinos(String nodo) {
        return adjList.getOrDefault(nodo, new ArrayList<>());
    }

    public Set<String> getNodos() {
        return adjList.keySet();
    }

    public List<Arista> getAristas() {
        List<Arista> aristas = new ArrayList<>();
        for (String nodo : adjList.keySet()) {
            for (String vecino : adjList.get(nodo)) {
                aristas.add(new Arista(nodo, vecino));
            }
        }
        return aristas;
    }
}