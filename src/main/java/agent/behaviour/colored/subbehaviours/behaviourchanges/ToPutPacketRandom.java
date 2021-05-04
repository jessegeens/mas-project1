package agent.behaviour.colored.subbehaviours.behaviourchanges;

import agent.AgentImp;
import agent.behaviour.BehaviourChange;

/**
 * The behaviour change when the agent should put a packet on a random position next to him.
 */
public class ToPutPacketRandom extends BehaviourChange {

    private Boolean hasRandomPutDestination = false;

    /**
     * @return Returns true if the agent has a carry and has a random put destination.
     */
    @Override
    public boolean isSatisfied() {
        return hasRandomPutDestination && getAgentImp().hasCarry();
    }

    /**
     * Updates whether the agent has a random put destination in memory or not.
     */
    @Override
    public void updateChange() {
        hasRandomPutDestination = getAgentImp().getMemoryFragment(AgentImp.RANDOM_PUT_COORDINATE_KEY) != null;
    }
}
