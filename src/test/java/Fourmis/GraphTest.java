package Fourmis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {
    Graph graph;
    public int WIDTH = 13;
    public int HEIGHT = 19;
    ControlAnt appli;

    @BeforeEach
    void setUp() {
        appli = new ControlAnt();
        appli.createGrid(WIDTH,HEIGHT);
        graph = appli.getGraph();
    }

    @Test
    @DisplayName("Récuperer un noeud pas dans le graphe")
    void getNoeud() {
        assertThrows(IndexOutOfBoundsException.class, ()->{
            graph.getNode(HEIGHT, WIDTH);
        });

        assertThrows(IndexOutOfBoundsException.class, ()->{
            graph.getNode(-1, 0);
        });
    }

    @Test
    void putObstacle() {
        graph.putObstacle(2,1);
        Node2D actual = graph.getNode(2,1);
        assertEquals(State.OBSTACLE, actual.getNodeState());

        assertThrows(IndexOutOfBoundsException.class, ()->{
            graph.putObstacle(HEIGHT, WIDTH);
        });
    }


    @Test
    @DisplayName("NoeudList contient tout les noeuds du Graphe")
    void getNoeudList() {
        List<Node2D> node2DList = graph.getNodeList();

        for(int i = 0; i<WIDTH;i++){
            for(int j = 0; j<HEIGHT;j++){
                assertTrue(node2DList.contains(graph.getNode(i,j)));
            }
        }

    }

    @Test
    @DisplayName(("Longueur du graphe"))
    void getWidth() {
        assertEquals(WIDTH, graph.getWidth());
    }

    @Test
    @DisplayName(("Hauteur du graphe"))
    void getHeight() {
        assertEquals(HEIGHT, graph.getHeight());
    }


    @Test
    @DisplayName("Nombre de noeud dans le Graphe")
    void nbNoeud()
    {
        List<Node2D> node2DList = graph.getNodeList();
        assertEquals(WIDTH*HEIGHT, node2DList.size());
    }

}