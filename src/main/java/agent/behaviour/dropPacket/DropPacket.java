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
        System.out.println(toSearch);
        for(Coordinate coord : toSearch){
            CellPerception cell = perception.getCellAt(coord.getX(), coord.getY());
            if (cell == null) {
                continue;
            }
            if(agent.hasCarry()){
                System.out.println("blub1");
                if (cell.containsDestination(agent.getCarry().getColor())) {
                    System.out.println("DESTINA");
                    destination = new Coordinate(cell.getX(), cell.getY());
                    moveTo(destination, agent);
                    return;
                }
            } else {
                if (cell.containsPacket()) {
                    System.out.println("PACKET");
                    destination = new Coordinate(cell.getX(), cell.getY());
                    moveTo(destination, agent);
                    return;
                }
            }
        }

        // If no packet/destination is set nor visible, move randomly
        moveRandomly(agent);
    }

    private List<Coordinate> searchAll(CellPerception curr, int width, int height) {
        System.out.println("searchAll");
        List<Coordinate> coords = new ArrayList<>();
        for (int x=-(width-1)/2; x<(width-1)/2; x++) {
            for(int y=-(height-1)/2; y<(height-1)/2; y++) {
                coords.add(new Coordinate(curr.getX()+x, curr.getY()+y));
            }
        }
        return coords;
    }

    private List<Coordinate> searchRange(CellPerception prev, CellPerception curr, int width, int height){

        List<Coordinate> coords = new ArrayList<>();

        if (prev == null) {
            return searchAll(curr, width, height);
        }
        int x_range = (width-1)/2;
        int y_range = (height-1)/2;

        //horizontal
        int x_diff = curr.getX() - prev.getX();
        int h = curr.getY() - (height - 1) / 2;
        if (x_diff != 0) {
            for (int i = 0; i < height; i++) {
                coords.add(new Coordinate(curr.getX() + x_diff * x_range, h + i));
            }
        }

        // vertical
        int y_diff = curr.getY() - prev.getY();
        int w = curr.getX() - (width - 1) / 2;
        if (y_diff != 0) {
            for (int i = 0; i < width; i++) {
                coords.add(new Coordinate(w+i, curr.getY() + y_diff * y_range));
            }
        }

        if (x_diff != 0 && y_diff != 0) {
            coords.add(new Coordinate(curr.getX()+x_diff*x_range, curr.getY()+y_diff*y_range));
        }
        return coords;
    }

    // Move to a specified location
    private void moveTo(Coordinate destination, AgentImp agent){
        var perception = agent.getPerception();
        List<Coordinate> moves = new ArrayList<Coordinate>(List.of(
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
                    int minDistCurr = Perception.distance(destination.getX(), destination.getY(), agent.getX() + minimum.getX(), agent.getY() + minimum.getX());
                    int minDistPoss = Perception.distance(destination.getX(), destination.getY(), agent.getX() + move.getX(), agent.getY() +  move.getY());
                    if (minDistPoss < minDistCurr) minimum = move;
                }
            }
        }
        if (minimum == null) agent.skip(); //TODO: Zou dit mogen voorvallen
        else agent.step(agent.getX() + minimum.getX(), agent.getY() + minimum.getY());
        System.out.println(destination);
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
