package agent.behaviour.dropPacket.subBehaviours;

import agent.AgentImp;
import agent.behaviour.BehaviourChange;
import agent.behaviour.dropPacket.DropPacket;
import environment.CellPerception;
import environment.Coordinate;

import java.util.Arrays;
import java.util.List;

public class MoveToDestinationToPickPacket extends BehaviourChange {
    private Coordinate destination = null;
    private boolean hasCarry = false;

    @Override
    public boolean isSatisfied() {
        return !hasCarry && getAgentImp().isNeighbour(destination);
    }

    @Override
    public void updateChange() {
        hasCarry = getAgentImp().hasCarry();
        destination = Coordinate.fromString(getAgentImp().getMemoryFragment(DropPacket.DESTINATION_KEY));
    }
}
