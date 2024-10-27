
package proyecto1;

import org.graphstream.graph.Graph;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;
import java.awt.*;
        
public class GraphViewPanel extends JPanel{
    private SwingViewer viewer;
    
    public GraphViewPanel(Graph graph){
        setLayout(new BorderLayout());
        viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();
        Component view = (Component) viewer.addDefaultView(false);
        add(view, BorderLayout.CENTER);
    }  
}
