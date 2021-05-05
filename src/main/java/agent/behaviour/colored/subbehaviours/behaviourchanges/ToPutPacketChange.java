package agent.behaviour.colored.subbehaviours.behaviourchanges;

import agent.AgentImp;
import agent.behaviour.BehaviourChange;
import environment.Coordinate;

/**
 * The behaviour change when the agent should put a packet on a destination.
 */
public class ToPutPacketChange extends BehaviourChange {
    private Coordinate destination = null;
    private boolean hasCarry = false;

    /**
     * @return Returns true if the agent has a carry and is next to his destination.
     */
    @Override
    public boolean isSatisfied() {
        return hasCarry && getAgentImp().isNeighbour(destination);
    }

    /**
     * Updates whether the agent has a carry and updates the destination from memory.
     */
    @Override
    public void updateChange() {
        hasCarry = getAgentImp().hasCarry();
        destination = Coordinate.fromString(getAgentImp().getMemoryFragment(AgentImp.DESTINATION_KEY));
    }
}
