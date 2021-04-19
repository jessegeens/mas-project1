package agent.behaviour.droppacket.subbehaviours.behaviourChanges;

import agent.behaviour.BehaviourChange;
import agent.behaviour.droppacket.DropPacket;
import environment.Coordinate;

public class ToPickPacketChange extends BehaviourChange {
    private Coordinate destination = null;
    private boolean hasCarry = false;

    @Override
    public boolean isSatisfied() {
        //TODO: waarom destination soms null?
        return !hasCarry && destination != null && getAgentImp().isNeighbour(destination);
    }

    @Override
    public void updateChange() {
        hasCarry = getAgentImp().hasCarry();
        destination = Coordinate.fromString(getAgentImp().getMemoryFragment(DropPacket.DESTINATION_KEY));
    }
}
