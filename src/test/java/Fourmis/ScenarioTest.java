package Fourmis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.BitSet;
import static org.junit.jupiter.api.Assertions.*;

public class ScenarioTest {

    static final int WIDTH = 3;
    static final int HEIGHT = 3;
    static final int FOODQUANTITY = 6000;
    static final int FOODPARAM = 1;
    static final int EVAPORATIONPARAM = 0;
    static final int PHEROMONEPARAM = 0;
    AntFacadeController appli;

    @BeforeEach
    void setUp()
    {
        appli = new ControlAnt();

        appli.createGrid(WIDTH, HEIGHT);

        appli.putObstacle(0,0);
        appli.createColony(1,0);
        appli.putObstacle(2,0);

        ((ControlAnt)appli).putPheromone(0,1,2);
        ((ControlAnt)appli).putPheromone(1,2,1);

        appli.putFood(0,2, FOODQUANTITY);
        appli.putFood(2,2,FOODQUANTITY);


        appli.setParameters(EVAPORATIONPARAM, FOODPARAM, PHEROMONEPARAM);
    }

    @Test
    @DisplayName("Scenario de test 2 : a et b")
    void testa(){
        appli.createWorkers(1);

        //Situation initiale
        BitSet[][] bitSets = appli.play(0,false);

        //Noeud où il y a la fourmillière
        BitSet bitSet = bitSets[1][0];
        //Si il se trouve bien une fourmillière
        assertTrue(bitSet.get(0));
        //Et une ouvrière sans Nourriture
        assertTrue(bitSet.get(3));

        //Noeud où il y a les phéromones, Test si ils sont bien présent
        bitSet = bitSets[0][1];
        assertTrue(bitSet.get(6));
        bitSet = bitSets[1][2];
        assertTrue(bitSet.get(6));

        //Noeud où il y a les obstacles, Test si ils sont bien présent
        bitSet = bitSets[0][0];
        assertTrue(bitSet.get(1));
        bitSet = bitSets[2][0];
        assertTrue(bitSet.get(1));

        //Noeud où il y a les nourritures, Test si ils sont bien présent
        bitSet = bitSets[0][2];
        assertTrue(bitSet.get(5));
        bitSet = bitSets[2][2];
        assertTrue(bitSet.get(5));


        //Tour 1
        bitSets = appli.play(1,false);

        //a) Mouvement en 1,1 de l'ouvrière attendu
        bitSet = bitSets[1][1];
        assertTrue(bitSet.get(3));


        //Tour 2
        bitSets = appli.play(1,false);

        //b) 3 choix de mouvement : 0,1 | 1,2 | 2,1  de l'ouvrière attendu
        boolean goodMove = false;
        if(bitSets[0][1].get(3) || bitSets[1][2].get(3) ||bitSets[2][1].get(3))
            goodMove = true;
        assertTrue(goodMove);
    }

    @Test
    @DisplayName("Scénario de test : Probabilité avec 6000 essai")
    void testc(){

        //On place un ouvrier au centre (colonie inutile ici)
        Graph graph = ((ControlAnt)this.appli).getGraph();
        Node centerNode = graph.getNoeud(1,1);
        Node topNode = graph.getNoeud(0,1);
        Node bottomNode = graph.getNoeud(2,1);
        Node rightNode = graph.getNoeud(1,2);

        Node antHillNode = graph.getNoeud(1,0);

        //Print la quantité de phéro en haut, pour voir si il y en a bien 2
        System.out.println(topNode.getPheromone().get(0).getQuantity());

        int topCount = 0;
        int bottomCount = 0;
        int rightCount = 0;

        for(int i = 0; i<6000; i++){
            Worker worker = new Worker(centerNode, null);
            worker.addToRecordsPath(antHillNode);
            worker.move();

            if(worker.getPosition() == topNode)
                topCount++;
            else if(worker.getPosition() == bottomNode)
                bottomCount++;
            else if(worker.getPosition() == rightNode)
                rightCount++;
        }

        System.out.println("Top : " + topCount + "\nBottom : " + bottomCount + "\nRight : " + rightCount);



    }

}
