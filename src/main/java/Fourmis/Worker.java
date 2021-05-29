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
        this.recordsPath = new ArrayList<>();
        recordsPath.add(this.getPosition());
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
                ArrayList<Node> noneVisitedNode = new ArrayList<>();
                //Obtient la liste des noeuds non parcourus
                for(Node node : freeVoisins){
                    if(!recordsPath.contains(node)){
                        noneVisitedNode.add(node);
                    }
                }

                //Si nous avons parcourus tout les noeuds adjacents
                if(noneVisitedNode.size() == 0){
                    //Prend un noeud au hasard parmis ceux de libre
                    Node direction = freeVoisins.get(rnd.nextInt(freeVoisins.size()));

                    this.setPosition(direction);
                    this.collect();
                    recordsPath.add(this.getPosition());
                }

                //Si il y a au moins un noeud non parcourus
                else {
                    //Melange la liste
                    Collections.shuffle(noneVisitedNode);
                    ArrayList<Node> orderedList = new ArrayList<>(noneVisitedNode);

                    //Trie la liste des noeuds possèdant de la plus petite quantité de phéromone à la plus grande
                    //à l'aide la méthode compareTo override dans Node (ne prend pas en compte les différentes phéromones provenant de différente colonnie)
                    Collections.sort(orderedList);

                    //l'index à i(index) fois plude chance d'être choisi que le premier élement
                    int k = 1 + rnd.nextInt(orderedList.size() * (orderedList.size() + 1) / 2);
                    int index = 0;
                    for(int i = 1; i <= orderedList.size(); i++){
                        if(k <= i*(i+1)/2){
                            index = i - 1;
                            break;
                        }
                    }
                    Node newDirection = orderedList.get(index);
                    if(this.getPosition().getNodeState() == Node.STATE.ANTHILL)
                        System.out.print("ANTHILL : " + this.getPosition());

                    System.out.print("     up : " + this.getPosition());
                    this.setPosition(newDirection);
                    System.out.println("    to : " + this.getPosition());
                    if(newDirection.getFood() > 0)
                        this.collect();
                    this.recordsPath.add(newDirection);
                }
            }
        }

        //Lorsque l'ouvrière a de la nourriture
        else {

            //Chemin arrière
            this.setPosition(recordsPath.get(recordsPath.size() - 1));

            //Phéromone sur les noeuds où il n'y a pas de la nourriture/fourmillière
            if(this.getPosition().getFood() == 0 || this.getPosition().getNodeState() != Node.STATE.ANTHILL)
                this.putPheromone();

            recordsPath.remove(recordsPath.size() - 1);

            //Vide la nourriture lorsqu'il est sur une Fourmillière
            if(this.getPosition().getNodeState() == Node.STATE.ANTHILL){
                this.foodCollected = 0;

                //Ajoute la fourmillière à son historique de noeud parcourus lorsqu'elle depose sa nourriture
                //Sinon il s'arretera devant la fourmillière à la prochaine itération sans savoir quoi faire
                recordsPath.add(this.getPosition());
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

            else {
                this.foodCollected = this.colony.getCollectCapicty();
                this.getPosition().setFood(foodQuantity - this.foodCollected);
            }
        }

    }

    public void putPheromone(){
        Pheromone pheromone = new Pheromone(super.colony.getPheromoneQuantity(), this.colony);
        this.getPosition().addPheromone(pheromone);
    }

    public int getFoodCollected(){
        return this.foodCollected;
    }
}
