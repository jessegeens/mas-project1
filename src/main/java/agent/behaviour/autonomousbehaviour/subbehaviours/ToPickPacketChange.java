package agent.behaviour.autonomousbehaviour.subbehaviours;

import agent.behaviour.BehaviourChange;
import agent.behaviour.autonomousbehaviour.DropPacket;
import environment.Coordinate;

/**
 * This behaviour change makes an agent pick up a packet, if he is next to it
 */
public class ToPickPacketChange extends BehaviourChange {
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
