package agent.behaviour.colored;

import agent.AgentImp;
import agent.behaviour.colored.subbehaviours.Path;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;

import java.util.*;

public class Dijkstra {

    /**
     * This is the main path finding method. This is first called with `allowPacketsOnPath` set to false, to find a normal
     * path. We then use the well-known Dijkstra algorithm using a PQ based on distance from the starting point.
     * If we are unable to find a path, we will try again but will allow packets on our path (this indicates that the agent
     * should ask for help later). If this fails as well, we will use a local estimate of the best position in the perception
     * and will move there, so that we can hopefully calculate a path after some further movements. If the agent were to become
     * stuck because of a local minimum, the deadlock detection will be activated and the agent will start to move randomly.
     */
    public static Path calculateDijkstra(AgentImp agent, Coordinate destination, Boolean allowPacketsOnPath) {
        boolean foundPath = false;
        Perception perception = agent.getPerception();
        PriorityQueue<DijkstraTuple> pq = new PriorityQueue<>(new DijkstraComparator());
        Set<DijkstraCoordinate> visited = new HashSet<>();
        ArrayList<DijkstraTuple> grid = new ArrayList<>();
        pq.add(new DijkstraTuple(new Coordinate(agent.getX(), agent.getY()), 0));
        grid.add(new DijkstraTuple(new Coordinate(agent.getX(), agent.getY()), 0));
        while (!pq.isEmpty()) {
            DijkstraTuple next = pq.remove();
            int currDist = next.distance;
            List<Coordinate> neighbours = next.coordinate.getNeighbours();
            for (Coordinate neighbour : neighbours) {
                CellPerception cellPerception = perception.getCellPerceptionOnAbsPos(neighbour.getX(), neighbour.getY());
                if (neighbour != null && destination != null && neighbour.equalsCoordinate(destination)) {
                    grid.add(new DijkstraTuple(neighbour, currDist + 1));
                    foundPath = true;
                    pq.clear();
                    break;
                }
                if (visited.contains(new DijkstraCoordinate(neighbour)) || cellPerception == null) continue;
                if (!cellPerception.isWalkable() && (!allowPacketsOnPath || !cellPerception.containsPacket())) continue;
                visited.add(new DijkstraCoordinate(neighbour));
                pq.add(new DijkstraTuple(neighbour, currDist + 1));
                grid.add(new DijkstraTuple(neighbour, currDist + 1));
            }
        }
        if (foundPath) {
            Path path = calculatePath(agent, grid, grid.get(grid.size() - 1), destination);
            return path;
        } else if (!allowPacketsOnPath) {
            return calculateDijkstra(agent, destination, true);
        } else {
            // Estimate of temp destination in perception if destination is outside, only if packets are allowed
            DijkstraTuple estimate = findBestCoordInPercept(grid, destination);
            return calculatePath(agent, grid, estimate, destination);
        }
    }

    /**
     * This function returns a DijkstraTuple with the best coordinate in the grid
     */
    private static DijkstraTuple findBestCoordInPercept(ArrayList<DijkstraTuple> grid, Coordinate destination) {
        DijkstraTuple best = null;
        int distToDest = 0;
        for (DijkstraTuple gridElement : grid) {
            int distance = Perception.manhattanDistance(destination.getX(), destination.getY(), gridElement.coordinate.getX(), gridElement.coordinate.getY());
            if (best == null || distToDest > distance) {
                best = gridElement;
                distToDest = distance;
            }
        }
        return best;
    }

    /**
     * Given a calculated grid, a destination and a final destination, this function calculates the path object for the agent
     * The grid consists of a DijkstraTuple for every cell in the agents perception that is walkable and reachable
     */
    private static Path calculatePath(AgentImp agent, ArrayList<DijkstraTuple> grid, DijkstraTuple destination, Coordinate finalDestination) {
        ArrayList<Coordinate> packetsCoords = new ArrayList<>();
        ArrayList<Coordinate> pathCoords = new ArrayList<>();
        int dist = destination.distance; //grid.get(grid.size() - 1).distance;
        Coordinate current = destination.coordinate; //grid.get(grid.size() - 1).coordinate;
        pathCoords.add(current);
        while (dist > 0) {
            for (DijkstraTuple tuple : grid) {
                if (tuple.distance != dist - 1) continue;
                if (isNeighbour(tuple.coordinate, current)) {
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
        Path path = new Path(agent, finalDestination, pathCoords, packetsCoords);
        return path;
    }

    /**
     * @return true iff coordinate c1 is a neighbour of coordinate c2
     */
    static boolean isNeighbour(Coordinate c1, Coordinate c2) {
        if (Math.abs(c1.getY() - c2.getY()) > 1) return false;
        if (Math.abs(c1.getX() - c2.getX()) > 1) return false;
        if (c1.equalsCoordinate(c2)) return false;
        return true;
    }



}

/**
 * The DijkstraTuple consists of a coordinate for a cell and a distance from the starting point of the agent to that cell
 * -> Distance is distance from starting point
 */
class DijkstraTuple {
    Coordinate coordinate;
    int distance;

    DijkstraTuple(Coordinate coordinate, int distance) {
        this.coordinate = coordinate;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "(" + distance + ", " + coordinate.toString() + ")";
    }

}

/**
 * We implemented a custom DijkstraCoordinate that is very similar to the existing coordinate class
 * We used this so we can override the equals and hashcode functions without causing bugs in the existing code
 */
class DijkstraCoordinate {
    int x;
    int y;

    DijkstraCoordinate(Coordinate coordinate) {
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
    public boolean equals(Object o) {

        if (!(o instanceof DijkstraCoordinate)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        DijkstraCoordinate c2 = (DijkstraCoordinate) o;
        return this.x == c2.x && this.y == c2.y;
    }

    @Override
    public int hashCode() {
        return x * 31 + y;
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
}

/**
 * This comparator puts closest cells first in the PriorityQueue of the dijkstra algorithm
 */
class DijkstraComparator implements Comparator<DijkstraTuple> {
    @Override
    public int compare(DijkstraTuple a, DijkstraTuple b) {
        return a.distance - b.distance;
    }
}
