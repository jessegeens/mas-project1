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


    @Override
    public void act(AgentImp agent) {
        // Potential moves an agent can make (radius of 1 around the agent)
        List<Coordinate> moves = new ArrayList<>(List.of(
                new Coordinate(1, 1), new Coordinate(-1, -1),
                new Coordinate(1, 0), new Coordinate(-1, 0),
                new Coordinate(0, 1), new Coordinate(0, -1),
                new Coordinate(1, -1), new Coordinate(-1, 1)
        ));

        var perception = agent.getPerception();
        // Shuffle moves randomly
        Collections.shuffle(moves);


        List<CellPerception> neighbours = Arrays.asList(perception.getNeighboursInOrder());

        if (!agent.hasCarry()) { // Agent is looking for a packet
            for (CellPerception neighbour : neighbours) { // If neighbouring packet, pick it up
                if (neighbour == null) continue;
                if (neighbour.containsPacket()) {
                    agent.pickPacket(neighbour.getX(), neighbour.getY());
                    return;
                }
            }
            if (agent.seePacket()) { // Packet is in visible range

            }
        } else { // Agent is carrying a packet
            for (CellPerception neighbour : neighbours) {
                if (neighbour == null) continue;
                if (neighbour.containsDestination(agent.getCarry().getColor())) {
                    agent.putPacket(neighbour.getX(), neighbour.getY());
                    return;
                }
            }

        }
        for (var move : moves) {
            int x = move.getX();
            int y = move.getY();
            if (perception.getCellPerceptionOnRelPos(x, y) != null && perception.getCellPerceptionOnRelPos(x, y).isWalkable()) {
                if(agent.getLastArea().getX() == agent.getX() + x && agent.getLastArea().getY() == agent.getY() + y) continue; // Don't undo a move
                agent.step(agent.getX() + x, agent.getY() + y);
                return;
            }
        }

        // No viable moves, skip turn
        agent.skip();
    }

    private void moveTo(CellPerception destination, AgentImp agent){
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

    @Override
    public void communicate(AgentImp agent) {
        // No communication
    }
}
