package agent.behaviour.droppacket.subbehaviours.behaviours;

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

    private double calculateDegreeWithXAxis(Coordinate c1, Coordinate c2){
        int deltaX = c1.getX() - c2.getX();
        int deltaY = c1.getY() - c2.getY();
        double rad = Math.atan2(deltaX,deltaY)+Math.PI/2;
        if(rad<0)
            rad+=2*Math.PI;
        return rad;
    }

    //https://www.geeksforgeeks.org/program-for-point-of-intersection-of-two-lines/
    private Coordinate calcIntersectionOfLines(Coordinate A, Coordinate B, Coordinate C, Coordinate D){
        // Line AB represented as a1x + b1y = c1
        double a1 = B.getY() - A.getY();
        double b1 = A.getX() - B.getX();
        double c1 = a1*(A.getX()) + b1*(A.getY());

        // Line CD represented as a2x + b2y = c2
        double a2 = D.getY() - C.getY();
        double b2 = C.getX() - D.getX();
        double c2 = a2*(C.getX())+ b2*(C.getY());

        double determinant = a1*b2 - a2*b1;

        if (determinant == 0)
        {
            // The lines are parallel. This is simplified
            // by returning a pair of FLT_MAX
            throw new IllegalStateException("Shouldn't happen in our use case");
        }
        else
        {
            int x = (int) Math.floor((b2*c1 - b1*c2)/determinant);
            int y = (int) Math.floor((a1*c2 - a2*c1)/determinant);
            return new Coordinate(x, y);
        }
    }

    private Coordinate calcIntersectionWithPerception(Coordinate agent, Coordinate destination){
    double rad = calculateDegreeWithXAxis(agent,destination);
    if(rad<1/4*Math.PI || rad > 7/4*Math.PI) {
        //right boundary
    }else if(rad<3/4*Math.PI){
        //top boundary
    }else if(rad<5/4*Math.PI){
        //left boundary
    }else {
        //bottom boundary
    }
        return null;
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
