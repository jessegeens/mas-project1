package agent.behaviour.droppacket.subbehaviours.behaviourChanges;

import agent.behaviour.BehaviourChange;

public class ShouldChargeHasPacketChange extends BehaviourChange {

    boolean putPacketAndCharge = false;

    @Override
    public boolean isSatisfied() {
        return putPacketAndCharge;
    }

    @Override
    public void updateChange() {
        putPacketAndCharge = (getAgentImp().hasCarry() && getAgentImp().shouldCharge());
    }
}
