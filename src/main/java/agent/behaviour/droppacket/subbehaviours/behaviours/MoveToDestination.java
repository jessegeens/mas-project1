package agent.behaviour.droppacket.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import agent.behaviour.droppacket.DropPacket;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import util.Pair;

import java.sql.SQLOutput;
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
        /*var perception = agent.getPerception();
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
        else agent.step(agent.getX() + currentBestMove.getX(), agent.getY() + currentBestMove.getY());*/
        List<Coordinate> next = calculateDijkstra(agent, destination);
        if(next.size() == 0) System.out.println("BOOOEEEEE STOMME JAVA");
        agent.step(next.get(0).getX(), next.get(0).getY());
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

    private Coordinate calcIntersectionWithPerception(AgentImp agent, Coordinate destination){
        Coordinate agentCoord = new Coordinate(agent.getX(), agent.getY());
        double rad = calculateDegreeWithXAxis(agentCoord,destination);
        if (rad < 1/4 * Math.PI || rad > 7/4 * Math.PI) {
            //TODO: check for off-by-one errors
            //right boundary
            return calcIntersectionOfLines(agentCoord, destination,
                    new Coordinate(agent.getPerception().getOffsetX() + agent.getPerception().getWidth() - 1, agent.getPerception().getOffsetY()),
                    new Coordinate(agent.getPerception().getOffsetX() + agent.getPerception().getWidth() - 1, agent.getPerception().getOffsetY() + agent.getPerception().getHeight() - 1));

        } else if (rad < 3/4 * Math.PI) {
            //top boundary
            return calcIntersectionOfLines(agentCoord, destination,
                    new Coordinate(agent.getPerception().getOffsetX(), agent.getPerception().getOffsetY()),
                    new Coordinate(agent.getPerception().getOffsetX() + agent.getPerception().getWidth() - 1, agent.getPerception().getOffsetY()));
        } else if (rad< 5/4 * Math.PI) {
            // left boundary
            return calcIntersectionOfLines(agentCoord, destination,
                    new Coordinate(agent.getPerception().getOffsetX(), agent.getPerception().getOffsetY()),
                    new Coordinate(agent.getPerception().getOffsetX(), agent.getPerception().getOffsetY() + agent.getPerception().getHeight() - 1));
        } else {
            //bottom boundary
            return calcIntersectionOfLines(agentCoord, destination,
                    new Coordinate(agent.getPerception().getOffsetX(), agent.getPerception().getOffsetY() + agent.getPerception().getHeight() - 1),
                    new Coordinate(agent.getPerception().getOffsetX() + agent.getPerception().getWidth() - 1, agent.getPerception().getOffsetY() + agent.getPerception().getHeight() - 1));
        }
    }

    private List<Coordinate> calculateDijkstra(AgentImp agent, Coordinate destination) {
        Perception perception = agent.getPerception();
        PriorityQueue<DijkstraTuple> pq = new PriorityQueue<>(new DijkstraComparator());
        Set<DijkstraCoordinate> visited = new HashSet<>();
        ArrayList<DijkstraTuple> grid = new ArrayList<>();
        pq.add(new DijkstraTuple(new Coordinate(agent.getX(), agent.getY()), 0));
        while(!pq.isEmpty()){
            DijkstraTuple next = pq.remove();
            int currDist = next.distance;
            List<Coordinate> neighbours = next.coordinate.getNeighbours();
            for (Coordinate neighbour : neighbours) {
                CellPerception cellPerception = perception.getCellAt(neighbour.getX(), neighbour.getY());
                if(visited.contains(neighbour) || cellPerception == null || !cellPerception.isWalkable()) continue;
                visited.add(new DijkstraCoordinate(neighbour));
                System.out.println("Visited: " + visited.toString());
                if(neighbour.equalsCoordinate(destination)){
                    pq.clear();
                    break;
                }
                pq.add(new DijkstraTuple(neighbour, currDist + 1));
                grid.add(new DijkstraTuple(neighbour, currDist + 1));
            }
        }
        ArrayList<Coordinate> path = new ArrayList<>();
        int dist = grid.get(grid.size() - 1).distance;
        Coordinate current = grid.get(grid.size() - 1).coordinate;
        path.add(current);
        while(dist > 0){
            //path.add(grid.get(grid.size() - 1).coordinate);
            for(DijkstraTuple tuple : grid){
                if (tuple.distance != dist - 1) continue;
                if (isNeighbour(tuple.coordinate, current)){
                    dist = dist - 1;
                    current = tuple.coordinate;
                    path.add(current);
                    break;
                }
            }

        }
        return path;
    }

    boolean isNeighbour(Coordinate c1, Coordinate c2){
        if(Math.abs(c1.getY() - c2.getY()) > 1) return false;
        if(Math.abs(c1.getX() - c2.getX()) > 1) return false;
        if(c1.equalsCoordinate(c2)) return false;
        return true;
    }
}

class DijkstraTuple {
    Coordinate coordinate;
    int distance;

    DijkstraTuple(Coordinate coordinate, int distance) {
        this.coordinate = coordinate;
        this.distance = distance;
    }
}

class DijkstraCoordinate{
    int x;
    int y;

    DijkstraCoordinate(Coordinate coordinate){
        this.x = coordinate.getX();
        this.y = coordinate.getY();
    }

    @Override
    public boolean equals(Object o){

        if (!(o instanceof DijkstraCoordinate)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        DijkstraCoordinate c2 = (DijkstraCoordinate) o;
        return this.x == c2.x && this.y == c2.y;
    }

    @Override
    public String toString(){
        return "(" + this.x + ", " + this.y + ")";
    }
}

class DijkstraComparator implements Comparator<DijkstraTuple> {
    @Override
    public int compare(DijkstraTuple a, DijkstraTuple b) {
        return a.distance- b.distance;
    }
}

        /*PriorityQueue<ArrayList<Coordinate>> pq = new PriorityQueue<>(Comparator.comparingInt(ArrayList::size));
        ArrayList<Coordinate> list = new ArrayList<>();
        list.add(currentPos);
        pq.add(list);
        System.out.println("Step 1");

        if(perception.getCellAt(destination.getX(), destination.getY()) == null){
            //TODO: verify if destination is reachable
            destination = calcIntersectionWithPerception(agent, destination);
        }
        System.out.println("Step 2");
        ArrayList<Coordinate> visited = new ArrayList();
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
                if(visited.contains(neighbour)) continue;
                visited.add(neighbour);
                CellPerception cellPerception = perception.getCellAt(neighbour.getX(), neighbour.getY());
                if (cellPerception != null && cellPerception.isWalkable() && !next.contains(neighbour) ) {
                    next.add(neighbour);
                    pq.add(next);
                    //System.out.printf("pq: " + pq.toArray().toString());
                    if (neighbour.equalsCoordinate(destination)) {
                        return next;
                    }
                }
            }
            System.out.println("PQ has size " + pq.size());
            if(pq.size() > 40){
                System.out.println(pq.toArray().toString());
                return null;
            }
        }
        System.out.println("Step 3");
        return null;*/