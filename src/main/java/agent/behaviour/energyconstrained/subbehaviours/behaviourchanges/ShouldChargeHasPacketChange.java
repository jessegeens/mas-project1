package agent.behaviour.energyconstrained.subbehaviours.behaviourchanges;

import agent.behaviour.BehaviourChange;

/**
 * This behaviour change goes into effect when an agent should go charge because he has not sufficient battery and
 * when the agent is carrying a packet. He then needs to put the packet down first before he goes charge.
 */
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
