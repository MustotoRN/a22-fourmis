package Fourmis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkerTest {
    ControlAnt appli;
    static final int WIDTH = 13;
    static final int HEIGHT = 19;
    Graph g;

    @BeforeEach
    void setUp()
    {
        appli = new ControlAnt();
        appli.createGrid(WIDTH, HEIGHT);
        g = appli.getGraph();
        appli.createColony(2,2);
        g.putObstacle(0,1);
        g.putObstacle(1,0);
    }

    @Test
    @DisplayName("Même position que la colonie quand ils sont crée + Création seul")
    void getPosition() {
        appli.createWorkers(4);
        for(Ant worker : appli.getAntList()){
            if(worker instanceof Worker)
                assertEquals(appli.getGraph().getNoeud(2,2), worker.getPosition());
        }

        Node position = appli.getGraph().getNoeud(5,5);
        Worker worker = new Worker(position, null);
        assertEquals(position, worker.getPosition());
    }

    @Test
    void setPosition() {
        Node position = appli.getGraph().getNoeud(5,5);
        Worker worker = new Worker(position, null);
        Node direction = appli.getGraph().getNoeud(10,10);
        worker.setPosition(direction);
        assertEquals(direction, worker.getPosition());
    }

    @Test
    void move() {
        appli.putObstacle(6,5);
        appli.putObstacle(5,6);
        appli.putObstacle(5,4);
        Node position = appli.getGraph().getNoeud(5,5);
        Worker worker = new Worker(position, null);
        worker.move();

        Node newPosition = appli.getGraph().getNoeud(4,5);
        assertEquals(newPosition, worker.getPosition());
    }

    @Test
    void collect() {
        Node position = appli.getGraph().getNoeud(4,4);
        AntHill colony = new AntHill(appli.getGraph().getNoeud(0,0));
        colony.setCollectCapacity(10);
        appli.putFood(4,4, 100);

        Worker worker = new Worker(position, colony);
        assertEquals(100, position.getFood());
        worker.collect();
        assertEquals(90, position.getFood());

        //Il ne ramasse de la nourriture que si il en a pas
        worker.collect();
        assertEquals(90, position.getFood());
    }

    @Test
    void putPheromone() {
        AntHill colony = new AntHill(appli.getGraph().getNoeud(0,0));
        colony.setPheromoneQuantity(10);

        Node position = appli.getGraph().getNoeud(4,4);
        Worker worker = new Worker(position, colony);

        assertEquals(0, position.getPheromone().size());
        worker.putPheromone();
        assertEquals(1, position.getPheromone().size());
        worker.putPheromone();
        assertEquals(2, position.getPheromone().size());
    }

    @Test
    void getFoodCollected() {
        Node position = appli.getGraph().getNoeud(4,4);
        AntHill colony = new AntHill(appli.getGraph().getNoeud(0,0));
        colony.setCollectCapacity(10);
        appli.putFood(4,4, 100);

        Worker worker = new Worker(position, colony);
        assertEquals(0, worker.getFoodCollected());
        worker.collect();
        assertEquals(colony.getCollectCapicity(), worker.getFoodCollected());

        //Il ne ramasse de la nourriture que si il en a pas
        worker.collect();
        assertEquals(colony.getCollectCapicity(),worker.getFoodCollected());
    }

    @Test
    void addToRecordsPath() {
        Node position = appli.getGraph().getNoeud(4,4);
        AntHill colony = new AntHill(appli.getGraph().getNoeud(0,0));
        colony.setCollectCapacity(10);
        appli.putFood(4,4, 100);

        //Ouvriere avec de la nourriture
        Worker worker = new Worker(position, colony);
        worker.collect();

        //Ajout de 2 chemin dans son historique qu'il va devoir re-parcourir
        Node n1 = appli.getGraph().getNoeud(4,3);
        Node n2 = appli.getGraph().getNoeud(4,3);
        worker.addToRecordsPath(n1);
        worker.addToRecordsPath(n2);

        worker.move();
        assertEquals(n1, worker.getPosition());
        worker.move();
        assertEquals(n2, worker.getPosition());

    }
}