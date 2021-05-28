package Fourmis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Worker extends Ant{
    private int foodCollected;
    private ArrayList<Node> recordsPath;

    /**
     * Créé une fourmis à un noeud donné
     *
     * @param node : emplacement de la fourmis
     */
    public Worker(Node node, AntHill colony) {
        super(node, colony);
        this.foodCollected = 0;
    }

    @Override
    public void move() {
        Node position = this.getPosition();
        if(this.foodCollected == 0){
            ArrayList<Node> freeVoisins = new ArrayList<>(position.getFreeVoisins());

            //Si la fourmis ce trouve sur un noeud qui possède une liste de NoeudVoisin différent de 0,
            //alors elle peu bouger, sinon elle ne fait rien.
            if(freeVoisins.size() != 0){
                Random rnd = new Random();

                ArrayList<Node> noneVistedNode = new ArrayList<>();
                //Ajoute les noeuds parcourue à l'historique
                for(Node node : freeVoisins){
                    if(!recordsPath.contains(node)){
                        noneVistedNode.add(node);
                    }
                }

                if(noneVistedNode.size() == 0){
                    //Prend un noeud au hasard parmis ceux de libre
                    Node direction = freeVoisins.get(rnd.nextInt(freeVoisins.size()));
                }
                else{
                    //Melange la liste
                    Collections.shuffle(noneVistedNode);
                    ArrayList<Node> orderedList = new ArrayList<>(noneVistedNode);
                    //Trie la liste des noeuds possèdant de la plus petite quantité de phéromone à la plus grande
                    //à l'aide la méthode compareTo override dans Node (ne prend pas en compte les différentes phéromones provenant de différente colonnie)
                    Collections.sort(orderedList);
                    Random rndNode = new Random();
                    int index = rndNode.nextInt(orderedList.size()) ;
                    //l'index à i(index) fois plude chance d'être choisi que le premier élement
                    index = ((1/orderedList.size()) * index) - 1;
                    Node newDirection = orderedList.get(index);
                    this.setPosition(newDirection);
                }


                //Prend un noeud au hasard parmis ceux de libre
                Node direction = freeVoisins.get(rnd.nextInt(freeVoisins.size()));

                //Va en direction de ce noeud là
                this.setPosition(direction);
                //Ajout des noeuds parcourues
                recordsPath.add(this.getPosition());
            }
        }
        else {
            ArrayList<Node> recordsPathReverse = new ArrayList<>(recordsPath);
            Collections.reverse(recordsPathReverse);

            for(Node node : recordsPathReverse){
                this.setPosition(node);
                this.putPheromone();
            }
        }

    }

    public void collect(){
        int foodQuantity = this.getPosition().getFood();
        if(foodQuantity != 0){
            if(foodQuantity < this.colony.getCollectCapicty()){
                this.foodCollected = foodQuantity;
                this.getPosition().setFood(0);
            }
            else{
                this.foodCollected = this.colony.getCollectCapicty();
                this.getPosition().setFood(foodQuantity - this.foodCollected);
            }
        }

    }

    public void putPheromone(){
        Pheromone pheromone = new Pheromone(super.colony.getPheromoneQuantity(), this.colony);
        this.getPosition().addPheromone(pheromone);
    }


}
