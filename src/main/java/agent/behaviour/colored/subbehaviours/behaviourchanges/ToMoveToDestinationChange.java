package agent.behaviour.colored.subbehaviours.behaviourchanges;

import agent.AgentImp;
import agent.behaviour.BehaviourChange;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * The behaviour change when the agent should start to move towards a destination.
 */
public class ToMoveToDestinationChange extends BehaviourChange {

    /**
     * The new destination of the agent.
     */
    private Coordinate newDestination = null;

    /**
     * True if the agent has a help message in memory.
     */
    private Boolean hasHelpMessage = false;

    /**
     *
     * @return Returns true if the agent has a new destination and has no help message.
     */
    @Override
    public boolean isSatisfied() {
        return !hasHelpMessage && newDestination != null;
    }

    /**
     * Checks whether the agent has a help message in memory.
     * Searches for a new destination for the agent and adds it to memory.
     */
    @Override
    public void updateChange() {
        AgentImp agent = getAgentImp();
        hasHelpMessage = getAgentImp().getMemoryFragment(AgentImp.HELP_MESSAGE_KEY) != null;
        var perception = agent.getPerception();
        List<CellPerception> toSearch;
        if (hasToSearchAll(agent)) {
            toSearch = searchAll(perception, perception.getWidth(), perception.getHeight());
            agent.addMemoryFragment(AgentImp.SEARCH_ALL_KEY, "false");
        } else {
            toSearch = searchRange(agent, perception.getCellPerceptionOnAbsPos(agent.getX(), agent.getY()), perception);
        }
        Coordinate destination = findDestination(agent, toSearch);
        newDestination = destination;
        if (destination != null) agent.addMemoryFragment(AgentImp.DESTINATION_KEY, destination.toString());
        else agent.removeMemoryFragment(AgentImp.DESTINATION_KEY);
    }

    /**
     * Finds a destination in the agent perception. Searches for a destination in memory or in his
     * perception with a colour if the agent has a carry with that colour.
     * @return Returns the found destination.
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
     *
     * @return Returns true if the agent has a carry and the cellperception contains a destination
     * with the right colour to put packets on or true if the agent has no carry and the
     * cellperception contains a packet with the right colour, false otherwise.
     */
    private Boolean containsDestination(AgentImp agent, CellPerception cell) {
        if (agent.hasCarry() && cell.containsDestination(agent.getCarry().getColor())) return true;
        else return !agent.hasCarry() && cell.containsPacketWithColor(agent.getAgentColor());
    }

    /**
     * @return Returns a list of all cellperceptions in the given perception.
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
     * @return Returns a list of cellperceptions that are in the range of an agent and that were not in his
     * range in the previous cycle.
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

    /**
     * @return Returns true if the agent should search his whole perception, false if he should
     * only search for destinations in positions that became visible since the previous cycle.
     */
    private Boolean hasToSearchAll(AgentImp agent) {
        String searchAll = agent.getMemoryFragment(AgentImp.SEARCH_ALL_KEY);
        if (searchAll == null) return true;
        return Boolean.parseBoolean(searchAll);
    }
}
