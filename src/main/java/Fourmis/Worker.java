package Fourmis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Worker extends Ant{
    private int foodCollected;
    private ArrayList<Node> recordsPath;

    //Mis à true lors de la collect() de nourriture
    private boolean isBacktracking;

    /**
     * Créé une fourmis à un noeud donné
     *
     * @param node2D : emplacement de la fourmis
     */

    public Worker(Node node2D, AntHill colony) {
        super(node2D, colony);
        this.isBacktracking = false;
        this.foodCollected = 0;
        this.recordsPath = new ArrayList<>();
        recordsPath.add(this.getPosition());
    }

    /**
     * Permet d'obtenir une liste de noeud des noeud non visité par la fourmis ouvrière
     * @param freeVoisins la liste des noeuds voisin libre
     * @return retourne la liste des noeuds voinsins non encore visité
     */
    public ArrayList<Node> getNonVisitedNode(ArrayList<Node> freeVoisins){
        ArrayList<Node> noneVisitedNode = new ArrayList<>();

        //Obtient la liste des noeuds non parcourus
        for(Node node : freeVoisins){
            if(!recordsPath.contains(node)){
                noneVisitedNode.add(node);
            }
        }

        return new ArrayList<>(noneVisitedNode);
    }

    /**
     * Permet à l'ouvrière de bouger
     */
    @Override
    public void move() {
        Node position = this.getPosition();
        if(this.foodCollected == 0){
            ArrayList<Node> freeVoisins = new ArrayList<>(position.getFreeVoisins());

            //Si la fourmis ce trouve sur un noeud qui possède une liste de NoeudVoisin différent de 0,
            //alors elle peu bouger, sinon elle ne fait rien.
            if(freeVoisins.size() != 0){
                Random rnd = new Random();
                ArrayList<Node> noneVisitedNode = getNonVisitedNode(freeVoisins);


                //Si nous avons parcourus tout les noeuds adjacents
                if(noneVisitedNode.size() == 0){
                    //Prend un noeud au hasard parmis ceux de libre
                    Node direction = freeVoisins.get(rnd.nextInt(freeVoisins.size()));

                    this.setPosition(direction);
                    if(direction.getFood() > 0){
                        this.collect();
                    }
                    recordsPath.add(this.getPosition());
                }

                //Si il y a au moins un noeud non parcourus
                else {
                    //Melange la liste
                    Collections.shuffle(noneVisitedNode);
                    ArrayList<Node> orderedList = new ArrayList<>(noneVisitedNode);

                    int foodQuantity = 0;
                    Node mostFood = null;
                    for(Node node: orderedList){
                        if(node.getFood() > foodQuantity){
                            foodQuantity = node.getFood();
                            mostFood = node;
                        }
                    }

                    //Si il y a au moins un noeud qui a de la nourriture
                    if(foodQuantity != 0){
                        this.setPosition(mostFood);
                        this.collect();
                        this.recordsPath.add(mostFood);
                    }

                    //Si aucun voisin non visité n'ont pas de nourriture, algorithme de phéromone
                    else{


                        //Trie la liste des noeuds possèdant de la plus petite quantité de phéromone à la plus grande
                        //à l'aide la méthode compareTo override dans Node (ne prend pas en compte les différentes phéromones provenant de différente colonnie)
                        Collections.sort(orderedList);

                        int index = this.getIndexAlgorithme(orderedList);
                        Node direction = orderedList.get(index);

                        this.setPosition(direction);
                        this.recordsPath.add(direction);
                    }


                }
            }
        }

        //Lorsque l'ouvrière a de la nourriture
        else {

            //Chemin arrière
            Node backNode = recordsPath.get(recordsPath.size()-1);

            //Si le noeud est un obstacle
            if(backNode.getNodeState() == State.OBSTACLE){
                this.isBacktracking = false;
            }

            //Si il est dans un état de "retour sur ces pas" (qu'il n'a pas encore vu d'obstacle dans son retour)
            if(isBacktracking){
                this.setPosition(backNode);

                //Phéromone sur les noeuds où il n'y a pas de la nourriture/fourmillière
                if(this.getPosition().getFood() == 0)
                    this.putPheromone();

                recordsPath.remove(recordsPath.size() - 1);

                //Vide la nourriture lorsqu'il est sur une Fourmillière
                if(this.getPosition().getNodeState() == State.ANTHILL){
                    this.foodCollected = 0;

                    //Ajoute la fourmillière à son historique de noeud parcourus lorsqu'elle depose sa nourriture
                    //Sinon il s'arretera devant la fourmillière à la prochaine itération sans savoir quoi faire
                    recordsPath.add(this.getPosition());
                }
            }
            //Si il n'est PAS dans un état de "retour sur ces pas"
            //Il devra donc se déplacer de manière aléatoire
            else{

                //On vide notre historique de chemin
                recordsPath.clear();

                ArrayList<Node> freeVoisins = new ArrayList<>(position.getFreeVoisins());

                //Si la fourmis ce trouve sur un noeud qui possède une liste de NoeudVoisin différent de 0,
                //alors elle peu bouger, sinon elle ne fait rien.
                if(freeVoisins.size() != 0) {

                    Random rnd = new Random();
                    ArrayList<Node> noneVisitedNode = getNonVisitedNode(freeVoisins);


                    //Si nous avons parcourus tout les noeuds adjacents
                    if (noneVisitedNode.size() == 0) {

                        //Prend un noeud au hasard parmis ceux de libre
                        Node direction = freeVoisins.get(rnd.nextInt(freeVoisins.size()));
                        this.setPosition(direction);
                        recordsPath.add(this.getPosition());

                    }else{

                        //Prend un noeud au hasard parmis ceux non parcours et libre
                        Node direction = noneVisitedNode.get(rnd.nextInt(noneVisitedNode.size()));
                        this.setPosition(direction);
                        recordsPath.add(this.getPosition());
                    }

                    //Phéromone sur les noeuds où il n'y a pas de la nourriture/fourmillière
                    if(this.getPosition().getFood() == 0)
                        this.putPheromone();

                    //Vide la nourriture lorsqu'il est sur une Fourmillière
                    if(this.getPosition().getNodeState() == State.ANTHILL){
                        this.foodCollected = 0;

                        //Ajoute la fourmillière à son historique de noeud parcourus lorsqu'elle depose sa nourriture
                        //Sinon il s'arretera devant la fourmillière à la prochaine itération sans savoir quoi faire
                        recordsPath.add(this.getPosition());
                    }
                }
            }


        }

    }

    /**
     * Permet à la fourmis ouvrière de récuperer de la nourriture sur un noeud lorsqu'il y en a
     */
    public void collect(){

        //Collecte seulement si il n'a rien collecté
        if(this.foodCollected == 0){

            int foodQuantity = this.getPosition().getFood();

            //Si le noeud contient de la nourriture
            if(foodQuantity != 0){
                if(foodQuantity < this.colony.getCollectCapacity()){
                    this.setFoodCollected(foodQuantity);
                    this.getPosition().setFood(0);
                }

                else {
                    this.setFoodCollected(this.colony.getCollectCapacity());
                    this.getPosition().setFood(foodQuantity - this.foodCollected);
                }
            }
            this.isBacktracking = true;
        }
    }

    /**
     * Permet d'établir de la phéromone sur un noeud
     */
    public void putPheromone(){
        Pheromone pheromone = new Pheromone(super.colony.getPheromoneQuantity(), this.colony);
        this.getPosition().addPheromone(pheromone);
    }

    /**
     * Récupère le nombre de nourriture collecté par la fourmis ouvrière
     * @return la quantité de nourriture récuperé par la fourmi ouvrière
     */
    public int getFoodCollected(){
        return this.foodCollected;
    }

    /**
     * Permet de récuperer au hasard un noeud d'une liste trié par ordre croissant de phéromone
     * Merci à Mazo d'avoir fournit cet algorithme !
     * @param nodesPheromone la liste des phéromones trié par ordre croissant
     * @return l'index
     */
    public int getIndexAlgorithme(ArrayList<Node> nodesPheromone){
        Random rnd = new Random();
        //l'index à i(index) fois plude chance d'être choisi que le premier élement
        int k = 1 + rnd.nextInt(nodesPheromone.size() * (nodesPheromone.size() + 1) / 2);
        int index = 0;
        for(int i = 1; i <= nodesPheromone.size(); i++){
            if(k <= i*(i+1)/2){
                index = i - 1;
                break;
            }
        }
        return index;
    }

    /**
     * Ajoute un noeud à la liste historique
     * @param addedNode2D noeud à ajouter dans l'historique
     */
    public void addToRecordsPath(Node addedNode2D){
        this.recordsPath.add(addedNode2D);
    }

    /**
     * Définis la capacité de recolte de nourriture
     * @param foodCollected la capacité de récolte de nourriture
     */
    public void setFoodCollected(int foodCollected) {
        this.foodCollected = foodCollected;
    }
}
