package agent.behaviour.colored.subbehaviours.behaviourchanges;

import agent.AgentImp;
import agent.behaviour.BehaviourChange;

public class ToPutPacketRandom extends BehaviourChange {

    private Boolean hasRandomPutDestination = false;

    @Override
    public boolean isSatisfied() {
        return hasRandomPutDestination && getAgentImp().hasCarry();
    }

    @Override
    public void updateChange() {
        hasRandomPutDestination = getAgentImp().getMemoryFragment(AgentImp.RANDOM_PUT_COORDINATE_KEY) != null;
    }
}
