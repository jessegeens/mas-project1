package agent.behaviour.droppacket.subbehaviours;

import agent.behaviour.BehaviourChange;
import agent.behaviour.droppacket.DropPacket;
import environment.Coordinate;

public class ToPutPacketChange extends BehaviourChange {
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
