package agent.behaviour.dropPacket.subBehaviours;

import agent.behaviour.BehaviourChange;
import agent.behaviour.dropPacket.DropPacket;
import environment.Coordinate;

public class MoveToDestinationToPutPacket extends BehaviourChange {
    private Coordinate destination = null;
    private boolean hasCarry = false;

    @Override
    public boolean isSatisfied() {
        return hasCarry && getAgentImp().isNeighbour(destination);
    }

    @Override
    public void updateChange() {
        hasCarry = getAgentImp().hasCarry();
        destination = Coordinate.fromString(getAgentImp().getMemoryFragment(DropPacket.DESTINATION_KEY));
    }
}
