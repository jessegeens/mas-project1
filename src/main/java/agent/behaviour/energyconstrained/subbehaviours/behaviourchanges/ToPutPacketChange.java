package agent.behaviour.energyconstrained.subbehaviours.behaviourchanges;

import agent.AgentImp;
import agent.behaviour.BehaviourChange;
import environment.Coordinate;

/**
 * This behaviour change makes an agent drop off a packet, if he is next to a drop-off point
 */
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
        destination = Coordinate.fromString(getAgentImp().getMemoryFragment(AgentImp.DESTINATION_KEY));
    }
}
