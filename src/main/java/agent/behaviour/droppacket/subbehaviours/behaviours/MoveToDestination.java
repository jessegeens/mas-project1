package agent.behaviour.droppacket.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import agent.behaviour.droppacket.DropPacket;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import util.CommunicateDropoff;

import java.util.*;
import java.util.List;

public class MoveToDestination extends LTDBehaviour {
    @Override
    public void act(AgentImp agent) {
        moveTo(agent, Coordinate.fromString(agent.getMemoryFragment(DropPacket.DESTINATION_KEY)));
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
        var perception = agent.getPerception();
        Coordinate agentCoord = new Coordinate(agent.getX(), agent.getY());
        Coordinate currentBestMove = null;
        for (var move : POSSIBLE_MOVES) {
            int x = move.getX();
            int y = move.getY();
            if (perception.getCellPerceptionOnRelPos(x, y) != null && perception.getCellPerceptionOnRelPos(x, y).isWalkable()) {
                if (currentBestMove == null) currentBestMove = move;
                else if (isCloser(Coordinate.getSum(agentCoord, move), Coordinate.getSum(agentCoord, currentBestMove), destination))
                    currentBestMove = move;
            }
        }

        if (currentBestMove == null) agent.skip();
        else {
            Coordinate newCoord = new Coordinate(agent.getX() + currentBestMove.getX(), agent.getY() + currentBestMove.getY());
            if (agent.getLastArea() != null && newCoord.equalsCoordinate(new Coordinate(agent.getLastArea().getX(), agent.getLastArea().getY()))) {
                System.out.println("Loop detection triggered: " + agent.getID());
                agent.addMemoryFragment(DropPacket.LOOP_DETECTION_KEY, "true"); //TODO: eventueel destination aanpassen als packetje naast agent
                /*agent.removeMemoryFragment(DropPacket.DESTINATION_KEY);
                for (CellPerception cell : agent.getPerception().getNeighbours()){
                    if(cell != null && cell.containsPacket()){
                        agent.addMemoryFragment(DropPacket.DESTINATION_KEY, new Coordinate(cell.getX(), cell.getY()).toString());
                        agent.skip();
                    }*/

                // }

            }
            agent.step(agent.getX() + currentBestMove.getX(), agent.getY() + currentBestMove.getY());
        }
        /*List<Coordinate> next = calculateDijkstra(agent, destination);
        if(next.size() == 0) System.out.println("BOOOEEEEE STOMME JAVA");
        agent.step(next.get(0).getX(), next.get(0).getY());*/
    }

    private boolean isCloser(Coordinate first, Coordinate second, Coordinate dest) {
        if (first == null) return false;
        else if (second == null) return true;
        else {
            int distDestToFirst = Perception.distance(dest.getX(), dest.getY(), first.getX(), first.getY());
            int distDestToSecond = Perception.distance(dest.getX(), dest.getY(), second.getX(), second.getY());
            return distDestToFirst < distDestToSecond;
        }
    }
}