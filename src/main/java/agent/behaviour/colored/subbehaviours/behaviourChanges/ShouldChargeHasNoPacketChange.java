package agent.behaviour.colored.subbehaviours.behaviourChanges;

import agent.behaviour.BehaviourChange;
import environment.CellPerception;

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
