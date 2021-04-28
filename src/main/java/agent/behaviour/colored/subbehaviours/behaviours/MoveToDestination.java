package agent.behaviour.colored.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import agent.behaviour.colored.Dijkstra;

import agent.behaviour.colored.subbehaviours.Path;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import util.CommunicateDropoff;

import java.util.*;
import java.util.List;

public class MoveToDestination extends LTDBehaviour {
    @Override
    public void act(AgentImp agent) {
        moveTo(agent, Coordinate.fromString(agent.getMemoryFragment(AgentImp.DESTINATION_KEY)));
    }

    @Override
    public void communicate(AgentImp agent) {
        CommunicateDropoff.communicateDropOff(agent);

    }

    private final static List<Coordinate> POSSIBLE_MOVES = new ArrayList<Coordinate>(List.of(
            new Coordinate(1, 1), new Coordinate(-1, -1),
            new Coordinate(1, 0), new Coordinate(-1, 0),
            new Coordinate(0, 1), new Coordinate(0, -1),
            new Coordinate(1, -1), new Coordinate(-1, 1)
    ));

    private void moveTo(AgentImp agent, Coordinate destination) {
        Path path = Dijkstra.calculateDijkstra(agent, destination, false);
        System.out.println(path.toString());
        if(path.getPathCoordinate().size() == 0){
            agent.skip(); //TODO: delete...
            System.out.println("BOOOEEEEE STOMME JAVA");
        } else {
            agent.step(path.getPathCoordinate().get(0).getX(), path.getPathCoordinate().get(0).getY());
        }

    }

}