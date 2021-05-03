package environment.world.energystation;

import environment.*;
import environment.world.agent.AgentRep;
import environment.world.gradient.Gradient;
import environment.world.gradient.GradientWorld;
import support.Influence;

import java.util.*;
import java.util.stream.Collectors;

/**
 *  A class for an EnergyStationWorld, being a layer of the total world that contains
 *  EnergyStations.
 */

public class EnergyStationWorld extends World<EnergyStation> {

    //--------------------------------------------------------------------------
    //		CONSTRUCTOR
    //--------------------------------------------------------------------------

    /**
     *  Initializes a new EnergyStationWorld instance
     */
    public EnergyStationWorld() {
        super();
    }

    //--------------------------------------------------------------------------
    //		INSPECTORS
    //--------------------------------------------------------------------------

    /**
     *  Gets the total amount of EnergyStations that are in this EnergyStationWorld
     *
     * @return    This EnergyStationWorld's number of EnergyStations
     */
    public int getNbEnergyStations() {
        return (int) this.items.stream()
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .count();
    }

    public ArrayList<EnergyStation> getListOfEnergyStations() {
        return this.items.stream()
                .flatMap(Collection::stream)
                .filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));
    }

    public String toString() {
        return "EnergyStationWorld";
    }

    //--------------------------------------------------------------------------
    //		MUTATORS
    //--------------------------------------------------------------------------

    //--------------------------------------------------------------------------

    /**
     *  Brings a given influence in effect in this world.
     *  This method knows the effects of certain influences and realizes them
     *  in this world.
     *
     * @param inf  the influence to bring in effect
     */
    @Override
    protected void effectuate(Influence inf) {
        //NO-OP
    }

    /**
     * Adds EnergyStations to this EnergyStationWorld.
     *
     * @param energyStations  the energyStations to place in this world
     */
    @Override
    public void placeItems(Collection<EnergyStation> energyStations) {
        energyStations.forEach(this::placeItem);
    }

    /**
     * Adds a EnergyStation to this EnergyStationWorld.
     *
     * @param energyStation  the energyStation to place in this world
     */
    @Override
    public void placeItem(EnergyStation energyStation) {
        putItem(energyStation);
        getEnvironment().addActiveItem(energyStation);
        addGradientField(energyStation, getEnvironment(), true);
    }
//    private void addGradientField(EnergyStation energyStation, Environment environment){
//        int x = energyStation.getX();
//        int y = energyStation.getY() - 1 ; // Charging point is above the energy station
//        ArrayList<Gradient> allGradients = new ArrayList<>();
//        int height = environment.getHeight();
//        int width = environment.getWidth();
//        for (int i = 0; i < width; i++) {
//            for (int j = 0; j < height; j++) {
//                int value = distance(x, y, i, j);
//                Gradient grad = new Gradient(i, j, value);
//                allGradients.add(grad);
//            }
//        }
//        environment.getWorld(GradientWorld.class).addGradients(allGradients);
//    }

    //TODO: voorlopig hier
    private void addGradientField(EnergyStation energyStation, Environment environment, boolean checkIfSmaller) {
        int x = energyStation.getX();
        int y = energyStation.getY() - 1 ; // Charging point is above the energy station
        int height = environment.getHeight();
        int width = environment.getWidth();

        Gradient initialGradient = new Gradient(x, y, 0);
        int initialKey = y * width + x;
        HashMap<Integer, Gradient> newGradients = new HashMap<>();
        newGradients.put(initialKey, initialGradient);
        ArrayList<Gradient> loopingGradients = new ArrayList<>();
        loopingGradients.add(initialGradient);



        while (!loopingGradients.isEmpty()) {
            ArrayList<Gradient> recentlyAdded = new ArrayList<>();
            for (Gradient gradient: loopingGradients) {
                HashMap<Integer, Gradient> addedGradients = calculateNextGradients(newGradients, gradient, width, height);
                recentlyAdded.addAll(addedGradients.values());
                newGradients.putAll(addedGradients);
            }
            loopingGradients = recentlyAdded;
        }
        environment.getWorld(GradientWorld.class).addGradients(newGradients.values(), checkIfSmaller);
    }

    private HashMap<Integer, Gradient> calculateNextGradients(HashMap<Integer, Gradient> currentGradients, Gradient previouslyAdded, int worldWidth, int worldHeight) {
        Coordinate coordinate = new Coordinate(previouslyAdded.getX(), previouslyAdded.getY());
        ArrayList<Coordinate> neighbours = coordinate.getNeighboursInWorld(worldWidth, worldHeight);
        HashMap<Integer, Gradient> recentlyAdded = new HashMap<>();
        for (Coordinate neighbour: neighbours) {
            int key = neighbour.getY() * worldWidth + neighbour.getX();
            if (!currentGradients.containsKey(key) && isWalkablePos(previouslyAdded.getX(), previouslyAdded.getY())) {
                Gradient gradient = new Gradient(neighbour.getX(), neighbour.getY(), previouslyAdded.getValue() + 1);
                recentlyAdded.put(key, gradient);
            }
        }
        return recentlyAdded;
    }

    private boolean isWalkablePos(int x, int y) {
        Vector<Item<?>> items = getEnvironment().getItemsOnPos(x, y);
        return items.stream().allMatch(item -> item == null || item.getRepresentation().isWalkable() || item.getRepresentation() instanceof AgentRep);
    }

    public void updateGradientField() {
        System.out.println("update field");
        boolean checkIfSmaller = false;
        ArrayList<EnergyStation> energyStations = getListOfEnergyStations();
        for (EnergyStation energyStation: energyStations) {
            addGradientField(energyStation, getEnvironment(), checkIfSmaller);//Only the first time we want to reset the gradient field.
            checkIfSmaller = true;
        }
    }
}
