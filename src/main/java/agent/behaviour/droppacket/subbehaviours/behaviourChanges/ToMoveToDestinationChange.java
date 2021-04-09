package agent.behaviour.droppacket.subbehaviours.behaviourChanges;

import agent.AgentImp;
import agent.behaviour.BehaviourChange;
import agent.behaviour.droppacket.DropPacket;
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
                }
                return new Coordinate(cell.getX(), cell.getY());
            }
        }
        return null;
    }

    private Boolean containsDestination(AgentImp agent, CellPerception cell) {
        if (agent.hasCarry() && cell.containsDestination(agent.getCarry().getColor())) return true;
        else if (!agent.hasCarry() && cell.containsPacket()) return true;
        return false;
    }

    private List<CellPerception> searchAll(Perception perception, int width, int height) {
        List<CellPerception> perceptions = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                perceptions.add(perception.getCellAt(x, y));
            }
        }
        return perceptions;
    }

    private List<CellPerception> searchRange(@NotNull AgentImp agent, CellPerception curr, Perception perception) {
        int width = perception.getWidth();
        int height = perception.getHeight();
        int offsetX = perception.getOffsetX();
        int offsetY = perception.getOffsetY();

        Set<CellPerception> perceptions = new HashSet<>();
        if (agent.getLastArea() == null) return searchAll(agent.getPerception(), width, height);
        //horizontal new
        int x_diff = curr.getX() - agent.getLastArea().getX();
        if (x_diff > 0) {//step right
            for (int i=0;i<height; i++) {
                perceptions.add(agent.getPerception().getCellPerceptionOnAbsPos(offsetX+width-1, offsetY+i)); //height -1 because we start to count from 0.
            }
        }
        if (x_diff < 0) {//step left
            for (int i=0;i<height; i++) {
                perceptions.add(agent.getPerception().getCellPerceptionOnAbsPos(offsetX, offsetY+i));
            }
        }

        //vertical new
        int y_diff = curr.getY() - agent.getLastArea().getY();
        if (y_diff > 0) {//step down
            for (int i=0;i<width; i++) {
                perceptions.add(agent.getPerception().getCellPerceptionOnAbsPos(offsetX+i, offsetY+height-1)); //height -1 because we start to count from 0.
            }
        }
        if (y_diff < 0) {//step up
            for (int i=0;i<width; i++) {
                perceptions.add(agent.getPerception().getCellPerceptionOnAbsPos(offsetX+i, offsetY));
            }
        }

        return new ArrayList<CellPerception>(perceptions);
    }

    private Boolean hasToSearchAll(AgentImp agent) {
        String searchAll = agent.getMemoryFragment(DropPacket.SEARCH_ALL_KEY);
        if (searchAll == null) return true;
        return Boolean.parseBoolean(searchAll);
    }
}