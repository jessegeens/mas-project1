package agent.behaviour.droppacket.subbehaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import agent.behaviour.droppacket.DropPacket;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import util.Pair;

import java.util.*;

public class MoveToDestination extends LTDBehaviour {
    @Override
    public void act(AgentImp agent) {
        moveTo(agent, Coordinate.fromString(agent.getMemoryFragment(DropPacket.DESTINATION_KEY)));
    }

    @Override
    public void communicate(AgentImp agent) {

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
        else agent.step(agent.getX() + currentBestMove.getX(), agent.getY() + currentBestMove.getY());
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

    private List<Coordinate> calculateDijkstra(Coordinate destination, Perception perception) {
        Coordinate currentPos = new Coordinate(perception.getSelfX(), perception.getSelfY());
        PriorityQueue<ArrayList<Coordinate>> pq = new PriorityQueue<>(Comparator.comparingInt(ArrayList::size));
        ArrayList<Coordinate> list = new ArrayList<>();
        list.add(currentPos);
        pq.add(list);

        while(!pq.isEmpty()) {
            //remove die voorlopig de kortste geeft
            //neighbours zoeken
            //al die buren toevoegen met afstand uit uw second + 1, enkel toevoegen als walkable
            // checken dat neighbour destination is of niet

            ArrayList<Coordinate> next = pq.remove();
            int distance = next.size()-1;
            Coordinate current = next.get(distance);

            ArrayList<Coordinate> neighbours = current.getNeighbours();
            for (Coordinate neighbour: neighbours) {
                CellPerception cellPerception = perception.getCellAt(neighbour.getX(), neighbour.getY());
                if (cellPerception != null && cellPerception.isWalkable() && !next.contains(neighbour) ) {
                    next.add(neighbour);
                    pq.add(next);
                    if (neighbour.equalsCoordinate(destination)) {
                        return next;
                    }
                }
            }
        }
        return null;
    }
}
