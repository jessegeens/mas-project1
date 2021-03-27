package agent.behaviour.dropPacket.subBehaviours;

import agent.AgentImp;
import agent.behaviour.BehaviourChange;
import agent.behaviour.dropPacket.DropPacket;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import environment.world.agent.Agent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
        if (searchAll(agent)){
            toSearch = searchAll(perception, perception.getWidth(), perception.getHeight());
            agent.addMemoryFragment(DropPacket.SEARCH_ALL_KEY, "false");

        }
        else
            toSearch = searchRange(agent, perception.getCellPerceptionOnAbsPos(agent.getX(), agent.getY()), perception.getWidth(), perception.getHeight());
        Coordinate destination = findDestination(agent, toSearch);
        newDestination = destination;
        if (destination != null) {
            agent.addMemoryFragment(DropPacket.DESTINATION_KEY, destination.toString());
        }
//        else { //TODO: we denken dat dit niet nodig is
//            agent.removeMemoryFragment(DropPacket.DESTINATION_KEY);
//        }
    }

    private Coordinate findDestination(AgentImp agent, List<CellPerception> cells){
        for(CellPerception cell : cells){
            if(cell==null) {
                continue;
            }
            if(containsDestination(agent, cell))
                return new Coordinate(cell.getX(), cell.getY());
        }
        return null;

    }

    private Boolean containsDestination(AgentImp agent, CellPerception cell) {
        if(agent.hasCarry() && cell.containsDestination(agent.getCarry().getColor())){
            return true;
        }else if (!agent.hasCarry() && cell.containsPacket()){
            return true;
        }
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

    private List<CellPerception> searchRange(@NotNull AgentImp agent, CellPerception curr, int width, int height){

        List<CellPerception> perceptions = new ArrayList<>();

        int x_range = (width-1)/2;
        int y_range = (height-1)/2;

        //horizontal
        if (agent.getLastArea() == null) {
        }
        int x_diff = curr.getX() - agent.getLastArea().getX();
        int h = curr.getY() - (height - 1) / 2;
        if (x_diff != 0) {
            for (int i = 0; i < height; i++) {
                perceptions.add(agent.getPerception().getCellPerceptionOnAbsPos(curr.getX() + x_diff * x_range, h + i));
            }
        }

        // vertical
        int y_diff = curr.getY() - agent.getLastArea().getY();
        int w = curr.getX() - (width - 1) / 2;
        if (y_diff != 0) {
            for (int i = 0; i < width; i++) {
                perceptions.add(agent.getPerception().getCellPerceptionOnAbsPos(w+i, curr.getY() + y_diff * y_range));
            }
        }

        if (x_diff != 0 && y_diff != 0) {
            perceptions.add(agent.getPerception().getCellPerceptionOnAbsPos(curr.getX()+x_diff*x_range, curr.getY()+y_diff*y_range));
        }
        return perceptions;
    }

    private Boolean searchAll(AgentImp agent) {
        String searchAll = agent.getMemoryFragment(DropPacket.SEARCH_ALL_KEY);
        if (searchAll == null) return true;
        return Boolean.parseBoolean(searchAll);
    }
}
