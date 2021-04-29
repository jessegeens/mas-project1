package agent.behaviour.colored;

import agent.AgentImp;
import agent.behaviour.colored.subbehaviours.Path;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import environment.world.agent.Agent;

import java.util.*;

public class Dijkstra{

    private static void print(AgentImp agent, String msg){
        System.out.println("agent " + agent.getName() + ": " + msg);
    }

    public static Path calculateDijkstra(AgentImp agent, Coordinate destination, Boolean allowPacketsOnPath) {
        boolean foundPath = false;
        //print(agent, "destination is: " + destination);
        Perception perception = agent.getPerception();
        PriorityQueue<DijkstraTuple> pq = new PriorityQueue<>(new DijkstraComparator());
        Set<DijkstraCoordinate> visited = new HashSet<>();
        ArrayList<DijkstraTuple> grid = new ArrayList<>();
        pq.add(new DijkstraTuple(new Coordinate(agent.getX(), agent.getY()), 0));
        grid.add(new DijkstraTuple(new Coordinate(agent.getX(), agent.getY()), 0));
        while(!pq.isEmpty()){
            //System.out.println(agent.getName() + " looping pq, size: " + pq.size());
            DijkstraTuple next = pq.remove();
            int currDist = next.distance;
            List<Coordinate> neighbours = next.coordinate.getNeighbours();
           // print(agent, "neighbours: " + neighbours);
            for (Coordinate neighbour : neighbours) {
                CellPerception cellPerception = perception.getCellPerceptionOnAbsPos(neighbour.getX(), neighbour.getY());
                if(neighbour != null && destination != null && neighbour.equalsCoordinate(destination)){
                    grid.add(new DijkstraTuple(neighbour, currDist + 1));
                   // print(agent, "found destination: " + destination);
                    foundPath = true;
                    pq.clear();
                    break;
                }
                if(visited.contains(new DijkstraCoordinate(neighbour)) || cellPerception == null) continue;
                if(!cellPerception.isWalkable() && (!allowPacketsOnPath || !cellPerception.containsPacket())) continue;
                //print(agent, "Adding to pq: " + neighbour);
                visited.add(new DijkstraCoordinate(neighbour));
                pq.add(new DijkstraTuple(neighbour, currDist + 1));
                grid.add(new DijkstraTuple(neighbour, currDist + 1));
            }
        }
        if(foundPath){
            Path path = calculatePath(agent, grid, grid.get(grid.size() - 1), destination);
            //print(agent, coords.toString());
            return path;
        }
        else if (!allowPacketsOnPath){
            return calculateDijkstra(agent, destination, true);
        }
        else{
            // Estimate of temp destination in perception if destination is outside, only if packets are allowed
            DijkstraTuple estimate = findBestCoordInPercept(grid, destination);
            //print(agent, "estimate is " + estimate.coordinate.toString());
            return calculatePath(agent, grid, estimate, destination);
        }
    }

    private static DijkstraTuple findBestCoordInPercept(ArrayList<DijkstraTuple> grid, Coordinate destination){
        DijkstraTuple best = null;
        int distToDest = 0;
        for(DijkstraTuple gridElement : grid){
            int distance = Perception.manhattanDistance(destination.getX(), destination.getY(), gridElement.coordinate.getX(), gridElement.coordinate.getY());
            if(best == null || distToDest > distance){
                best = gridElement;
                distToDest = distance;
            }
        }
        return best;
    }

    private static Path calculatePath(AgentImp agent, ArrayList<DijkstraTuple> grid, DijkstraTuple destination, Coordinate finalDestination){

        ArrayList<Coordinate> packetsCoords = new ArrayList<>();
        ArrayList<Coordinate> pathCoords = new ArrayList<>();
        int dist = destination.distance; //grid.get(grid.size() - 1).distance;
        Coordinate current = destination.coordinate; //grid.get(grid.size() - 1).coordinate;
        pathCoords.add(current);
        while(dist > 0){
            for(DijkstraTuple tuple : grid){
                if (tuple.distance != dist - 1) continue;
                if (isNeighbour(tuple.coordinate, current)){
                    dist = dist - 1;
                    current = tuple.coordinate;
                    pathCoords.add(current);
                    if (agent.getPerception().getCellPerceptionOnAbsPos(current.getX(), current.getY()).containsPacket())
                        packetsCoords.add(current);
                    break;
                }
            }

        }
        Collections.reverse(pathCoords);
        pathCoords.remove(0);
        //System.out.println("Path: " + pathCoords);
        Path path = new Path(agent, finalDestination, pathCoords, packetsCoords);
        return path;
    }

    static boolean isNeighbour(Coordinate c1, Coordinate c2){
        if(Math.abs(c1.getY() - c2.getY()) > 1) return false;
        if(Math.abs(c1.getX() - c2.getX()) > 1) return false;
        if(c1.equalsCoordinate(c2)) return false;
        return true;
    }



}

/**
 Distance is distance from starting point
 */
class DijkstraTuple {
    Coordinate coordinate;
    int distance;

    DijkstraTuple(Coordinate coordinate, int distance) {
        this.coordinate = coordinate;
        this.distance = distance;
    }

    @Override
    public String toString(){
        return "(" + distance + ", " + coordinate.toString() + ")";
    }

}

class DijkstraCoordinate{
    int x;
    int y;

    DijkstraCoordinate(Coordinate coordinate){
        this.x = coordinate.getX();
        this.y = coordinate.getY();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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
    public int hashCode(){
        return x * 31 + y;
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

/*
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
 */