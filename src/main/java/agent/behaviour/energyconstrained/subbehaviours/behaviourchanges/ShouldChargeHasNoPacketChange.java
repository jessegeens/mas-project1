package agent.behaviour.energyconstrained.subbehaviours.behaviourchanges;

import agent.behaviour.BehaviourChange;

/**
 * This behaviour change goes into effect when an agent should go charge because he has not sufficient battery and
 * when the agent is not carrying a packet (otherwise, he should put the packet down first).
 */
public class ShouldChargeHasNoPacketChange extends BehaviourChange {

    boolean hasToCharge = false;

    @Override
    public boolean isSatisfied() {
        return hasToCharge;
    }

    @Override
    public void updateChange() {
        hasToCharge = (!getAgentImp().hasCarry() && getAgentImp().shouldCharge());
    }
}
