package agent.behaviour.colored.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import agent.behaviour.colored.Dijkstra;

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
        List<Coordinate> next = Dijkstra.calculateDijkstra(agent, destination);
        if(next.size() == 0){
            agent.skip();
            System.out.println("BOOOEEEEE STOMME JAVA");
        } else {
            agent.step(next.get(0).getX(), next.get(0).getY());
        }

    }

}