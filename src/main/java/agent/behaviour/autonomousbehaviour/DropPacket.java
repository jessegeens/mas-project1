package agent.behaviour.autonomousbehaviour;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;

public class DropPacket extends LTDBehaviour {

    private final static Logger LOGGER = Logger.getLogger(DropPacket.class.getName());
    public final static String DESTINATION_KEY = "destination";
    public final static String SEARCH_ALL_KEY = "searchAll";
    public final static String MOVED_AWAY_KEY = "movedAway";
    public final static String LOOP_DETECTION_KEY = "loopDetected";

    @Override
    public void act(AgentImp agent) {
        Coordinate destination = Coordinate.fromString(agent.getMemoryFragment(DESTINATION_KEY));

        try {
            // We first determine what the agent should do

            // If the agent has a destination, we check if he is next to it
            // If he is next to it, he will drop the packet, otherwise he will move towards his destination
            if (destination != null) {
                if (isNeighbour(agent, destination)) {
                    pickOrPutPacket(agent);
                    return;
                } else {
                    setStep(agent, destination);
                    return;
                }
            } else { // destination is null
                setStep(agent);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            agent.removeMemoryFragment(DESTINATION_KEY);
            agent.addMemoryFragment(SEARCH_ALL_KEY, "true");
            agent.skip();
        }
    }


    /**
        If the agent is carrying a packet, he will drop it off at his destination, otherwise he will pick up a packet
        The destination is then removed from memory, and "search_all" is activated so that the agent searches his
        whole perception for a new destination.
     */
    private void pickOrPutPacket(AgentImp agent) {

        Coordinate destination = Coordinate.fromString(agent.getMemoryFragment(DESTINATION_KEY));
        if (agent.hasCarry()) {
            agent.putPacket(destination.getX(), destination.getY());
        } else {
            agent.pickPacket(destination.getX(), destination.getY());
        }
        agent.removeMemoryFragment(DESTINATION_KEY);
        agent.addMemoryFragment(SEARCH_ALL_KEY, "true");
    }

    /**
     * @return true iff the agent is next to coordinate c (their manhattan distance is atmost 1)
     */
    private boolean isNeighbour(AgentImp agent, Coordinate c) {
        var perception = agent.getPerception();
        List<CellPerception> neighbours = Arrays.asList(perception.getNeighboursInOrder());
        for (CellPerception neighbour : neighbours) {
            if (neighbour == null) continue; // neighbours can be null when you're at the border of the world
            if (neighbour.getX() == c.getX() && neighbour.getY() == c.getY()) {
                return true;
            }
        }
        return false;
    }


    /**
        SetStep makes the agent either:
        - search his whole perception if SEARCH_ALL is enabled in memory
        - otherwise, a search range is determined based on the agents perception and his previous position
          to only search newly added cellperceptions
        then, a destination is searched and either:
        - a destination is found, and the agent moves there
        - or the agent moves randomly
     */
    private void setStep(AgentImp agent) {
        var perception = agent.getPerception();
        List<CellPerception> toSearch;
        if (searchAll(agent)) {
            toSearch = searchAll(perception, perception.getWidth(), perception.getHeight());
            agent.addMemoryFragment(SEARCH_ALL_KEY, "false");
        } else {
            toSearch = searchRange(agent, perception.getCellPerceptionOnAbsPos(agent.getX(), agent.getY()), perception);
        }
        Coordinate destination = findDestination(agent, toSearch);
        if (destination != null) {
            agent.addMemoryFragment(DESTINATION_KEY, destination.toString());
            setStep(agent, destination);
        } else {
            agent.removeMemoryFragment(DESTINATION_KEY);
            moveRandomly(agent);
        }
    }

    private void setStep(AgentImp agent, @NotNull Coordinate destination) {
        moveTo(destination, agent);
    }

    /**
     * @return the coordinate of a destination if `cells` contains a destination, null otherwise
     */
    private Coordinate findDestination(AgentImp agent, List<CellPerception> cells) {
        for (CellPerception cell : cells) {
            if (cell == null) continue;
            if (containsDestination(agent, cell)) return new Coordinate(cell.getX(), cell.getY());
        }
        return null;

    }


    /**
     * @return true iff `cell` contains a drop-off point if the agent is carrying a packet,
     *            or if `cell` contains a packet if the agent is not carrying anything
     */
    private Boolean containsDestination(AgentImp agent, CellPerception cell) {
        if (agent.hasCarry() && cell.containsDestination(agent.getCarry().getColor())) {
            return true;
        } else if (!agent.hasCarry() && cell.containsPacket()) {
            return true;
        }
        return false;
    }

    /**
     * @return a list of all cellperception in the agent's perception
     */
    private List<CellPerception> searchAll(Perception perception, int width, int height) {
        List<CellPerception> perceptions = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                perceptions.add(perception.getCellAt(x, y));
            }
        }
        return perceptions;
    }

    /**
     * @return a list off cellperceptions that have newly entered the agent's perception
     */
    private List<CellPerception> searchRange(@NotNull AgentImp agent, CellPerception curr, Perception perception) {
        int width = perception.getWidth();
        int height = perception.getHeight();
        int offsetX = perception.getOffsetX();
        int offsetY = perception.getOffsetY();

        Set<CellPerception> perceptions = new HashSet<>();
        if (agent.getLastArea() == null) return searchAll(agent.getPerception(), width, height);
        //horizontal new
        int x_diff = curr.getX() - agent.getLastArea().getX();
        if (x_diff > 0) { //step right
            for (int i = 0; i < height; i++) {
                perceptions.add(agent.getPerception().getCellPerceptionOnAbsPos(offsetX + width - 1, offsetY + i)); //height -1 because we start to count from 0.
            }
        }
        if (x_diff < 0) { //step left
            for (int i = 0; i < height; i++) {
                perceptions.add(agent.getPerception().getCellPerceptionOnAbsPos(offsetX, offsetY + i));
            }
        }

        //vertical new
        int y_diff = curr.getY() - agent.getLastArea().getY();
        if (y_diff > 0) { //step down
            for (int i = 0; i < width; i++) {
                perceptions.add(agent.getPerception().getCellPerceptionOnAbsPos(offsetX + i, offsetY + height - 1)); //height -1 because we start to count from 0.
            }
        }
        if (y_diff < 0) { //step up
            for (int i = 0; i < width; i++) {
                perceptions.add(agent.getPerception().getCellPerceptionOnAbsPos(offsetX + i, offsetY));
            }
        }
        return new ArrayList<>(perceptions);
    }


    private final static List<Coordinate> POSSIBLE_MOVES = new ArrayList<Coordinate>(List.of(
            new Coordinate(1, 1), new Coordinate(-1, -1),
            new Coordinate(1, 0), new Coordinate(-1, 0),
            new Coordinate(0, 1), new Coordinate(0, -1),
            new Coordinate(1, -1), new Coordinate(-1, 1)
    ));

    /**
     * This function makes the agent move towards his destination using a very simple approach -
     * the agent moves to the tile that minimizes the distance to the destination the most
     */
    private void moveTo(Coordinate destination, AgentImp agent) {
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

    /**
     * @return true iff `first` is closer to `dest` than `second`
     */
    private boolean isCloser(Coordinate first, Coordinate second, Coordinate dest) {
        if (first == null) {
            return false;
        } else if (second == null) {
            return true;
        } else {
            int distDestToFirst = Perception.distance(dest.getX(), dest.getY(), first.getX(), first.getY());
            int distDestToSecond = Perception.distance(dest.getX(), dest.getY(), second.getX(), second.getY());
            return distDestToFirst < distDestToSecond;
        }
    }

    /**
     * moveRandomly makes the agent move one random step
     */
    private void moveRandomly(AgentImp agent) {
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
                if (agent.getLastArea() != null && agent.getLastArea().getX() == agent.getX() + x && agent.getLastArea().getY() == agent.getY() + y) continue; // Don't undo a move
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

    private Boolean searchAll(AgentImp agent) {
        String searchAll = agent.getMemoryFragment(SEARCH_ALL_KEY);
        if (searchAll == null) return true;
        return Boolean.parseBoolean(searchAll);
    }
}
