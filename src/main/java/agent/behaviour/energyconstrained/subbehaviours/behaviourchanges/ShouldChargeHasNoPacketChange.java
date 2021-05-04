package agent.behaviour.energyconstrained.subbehaviours.behaviourchanges;

import agent.behaviour.BehaviourChange;

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
