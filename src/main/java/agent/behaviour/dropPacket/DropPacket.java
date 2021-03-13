package agent.behaviour.dropPacket;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;

// assumes the current class is called MyLogger
public class DropPacket extends LTDBehaviour {

    private final static Logger LOGGER = Logger.getLogger(DropPacket.class.getName());

    private Coordinate destination;

    @Override
    public void act(AgentImp agent) {
        var perception = agent.getPerception();
        List<CellPerception> neighbours = Arrays.asList(perception.getNeighboursInOrder());

        // If the agent has a destination, either pick up/drop off or move more towards the destination
        if(destination != null){
            for(CellPerception neigbour : neighbours){
                if (neigbour.getX() == destination.getX() && neigbour.getY() == destination.getY()) {
                    if(agent.hasCarry()) agent.putPacket(destination.getX(), destination.getY());
                    else agent.pickPacket(destination.getX(), destination.getY());
                    destination = null;
                    return;
                }
            }
            moveTo(destination, agent);
            return;
        }

        // If a packet/destination is not set, see if there is one visible and pinpoint it
        List<Coordinate> toSearch = searchRange(agent.getLastArea(), perception.getCellAt(agent.getX(), agent.getY()), perception.getWidth(), perception.getHeight());
        for(Coordinate coord : toSearch){
            CellPerception cell = perception.getCellAt(coord.getX(), coord.getY());
            if(agent.hasCarry()){
                if (cell.containsDestination(agent.getCarry().getColor())) {
                    destination = new Coordinate(cell.getX(), cell.getY());
                    moveTo(destination, agent);
                    return;
                }
            } else {
                if (cell.containsPacket()) {
                    destination = new Coordinate(cell.getX(), cell.getY());
                    moveTo(destination, agent);
                    return;
                }
            }
        }

        // If no packet/destination is set nor visible, move randomly
        moveRandomly(agent);
    }

    private List<Coordinate> searchRange(CellPerception prev, CellPerception curr, int width, int height){
        List<Coordinate> coords = new ArrayList<>();
        // TODO joris
        if(curr.getX() - prev.getX() == 1){
            int ymin;
            int x = curr.getX() + ((width - 1) / 2);
        }
    }

    // Move to a specified location
    private void moveTo(Coordinate destination, AgentImp agent){
        var perception = agent.getPerception();
        List<Coordinate> moves = new ArrayList<>(List.of(
                new Coordinate(1, 1), new Coordinate(-1, -1),
                new Coordinate(1, 0), new Coordinate(-1, 0),
                new Coordinate(0, 1), new Coordinate(0, -1),
                new Coordinate(1, -1), new Coordinate(-1, 1)
        ));

        Coordinate minimum = null;
        for (var move : moves) {
            int x = move.getX();
            int y = move.getY();
            if (perception.getCellPerceptionOnRelPos(x, y) != null && perception.getCellPerceptionOnRelPos(x, y).isWalkable()) {
                if (minimum == null) {
                    minimum = move;
                } else {
                    int minDistCurr = Perception.distance(destination.getX(), destination.getY(), minimum.getX(), minimum.getY());
                    int minDistPoss = Perception.distance(destination.getX(), destination.getY(), minimum.getX(), minimum.getY());
                    if (minDistPoss < minDistCurr) minimum = move;
                }
            }
        }
        if (minimum == null) agent.skip();
        else agent.step(agent.getX() + minimum.getX(), agent.getY() + minimum.getY());
    }

    // Move randomly
    private void moveRandomly(AgentImp agent){
        List<Coordinate> moves = new ArrayList<>(List.of(
                new Coordinate(1, 1), new Coordinate(-1, -1),
                new Coordinate(1, 0), new Coordinate(-1, 0),
                new Coordinate(0, 1), new Coordinate(0, -1),
                new Coordinate(1, -1), new Coordinate(-1, 1)
        ));

        // Shuffle moves randomly
        Collections.shuffle(moves);

        var perception = agent.getPerception();

        for (var move : moves) {
            int x = move.getX();
            int y = move.getY();
            if (perception.getCellPerceptionOnRelPos(x, y) != null && perception.getCellPerceptionOnRelPos(x, y).isWalkable()) {
                if(agent.getLastArea() != null && agent.getLastArea().getX() == agent.getX() + x && agent.getLastArea().getY() == agent.getY() + y) continue; // Don't undo a move
                agent.step(agent.getX() + x, agent.getY() + y);
                return;
            }
        }
        agent.skip();
    }

    @Override
    public void communicate(AgentImp agent) {
        // No communication
    }
}
