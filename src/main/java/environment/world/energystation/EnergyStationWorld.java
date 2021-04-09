package environment.world.energystation;

import environment.Environment;
import environment.World;
import environment.world.gradient.Gradient;
import environment.world.gradient.GradientWorld;
import support.Influence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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
        addGradientField(energyStation, getEnvironment());
    }

    //TODO: voorlopig hier
    private void addGradientField(EnergyStation energyStation, Environment environment){
        int x = energyStation.getX();
        int y = energyStation.getY() - 1 ; // Charging point is above the energy station

        int height = environment.getHeight();
        int width = environment.getWidth();

        ArrayList<Gradient> allGradients = new ArrayList<>();
        for (int i = 0; i < width; i++){
            for (int j = 0; j < height; j ++){
                int value = distance(x, y, i, j);
                Gradient grad = new Gradient(i, j, value);
                allGradients.add(grad);
            }
        }
        environment.getWorld(GradientWorld.class).placeItems(allGradients);
    }
}
