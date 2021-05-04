package agent.behaviour.energyconstrained.subbehaviours.behaviourchanges;

import agent.AgentImp;
import agent.behaviour.BehaviourChange;
import agent.behaviour.autonomousbehaviour.DropPacket;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ToMoveToDestinationChange extends BehaviourChange {

    private Coordinate newDestination = null;

    @Override
    public boolean isSatisfied() {
        return newDestination != null;
    }

    /**
     The agent either:
     - searches his whole perception if SEARCH_ALL is enabled in memory
     - otherwise, a search range is determined based on the agents perception and his previous position
     to only search newly added cellperceptions
     then, a destination if a destination is found, this behaviour changes is active
     */
    @Override
    public void updateChange() {
        AgentImp agent = getAgentImp();
        var perception = agent.getPerception();
        List<CellPerception> toSearch;
        if (hasToSearchAll(agent)) {
            toSearch = searchAll(perception, perception.getWidth(), perception.getHeight());
            agent.addMemoryFragment(DropPacket.SEARCH_ALL_KEY, "false");
        } else {
            toSearch = searchRange(agent, perception.getCellPerceptionOnAbsPos(agent.getX(), agent.getY()), perception);
        }
        Coordinate destination = findDestination(agent, toSearch);
        newDestination = destination;
        if (destination != null) agent.addMemoryFragment(DropPacket.DESTINATION_KEY, destination.toString());
        else agent.removeMemoryFragment(DropPacket.DESTINATION_KEY);
    }

    /**
     * @return the coordinate of a destination if `cells` contains a destination, null otherwise
     */
    private Coordinate findDestination(AgentImp agent, List<CellPerception> cells) {
        if (agent.hasCarry() && agent.getMemoryFragment(agent.getCarry().getColor().toString()) != null) {
            return Coordinate.fromString(agent.getMemoryFragment(agent.getCarry().getColor().toString()));
        }
        cells.removeIf(Objects::isNull);
        cells.sort(Comparator.comparingInt((CellPerception c) -> Perception.manhattanDistance(agent.getX(), agent.getY(), c.getX(), c.getY())));

        for (CellPerception cell : cells) {
            if (cell == null) continue;
            if (containsDestination(agent, cell)) {
                if (agent.hasCarry()) {
                   // System.out.println("Agent " + agent.getName() + ": adding " + agent.getCarry().getColor() + " to memory");
                    agent.addMemoryFragment(agent.getCarry().getColor().toString(), new Coordinate(cell.getX(), cell.getY()).toString());
                    if (agent.getMemoryFragment("colors") != null) {
                        String colors = agent.getMemoryFragment("colors");
                        if (!colors.contains(agent.getCarry().getColor().toString()))
                            agent.addMemoryFragment("colors", colors + ";" + agent.getCarry().getColor().toString());
                    } else {
                        agent.addMemoryFragment("colors", agent.getCarry().getColor().toString());
                    }
                }
                return new Coordinate(cell.getX(), cell.getY());
            }
        }
        return null;
    }


    /**
     * @return true iff `cell` contains a drop-off point if the agent is carrying a packet,
     *            or if `cell` contains a packet if the agent is not carrying anything
     */
    private Boolean containsDestination(AgentImp agent, CellPerception cell) {
        if (agent.hasCarry() && cell.containsDestination(agent.getCarry().getColor())) return true;
        else if (!agent.hasCarry() && cell.containsPacket()) return true;
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

    private Boolean hasToSearchAll(AgentImp agent) {
        String searchAll = agent.getMemoryFragment(DropPacket.SEARCH_ALL_KEY);
        if (searchAll == null) return true;
        return Boolean.parseBoolean(searchAll);
    }
}
