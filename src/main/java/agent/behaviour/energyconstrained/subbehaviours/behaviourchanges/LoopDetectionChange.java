package agent.behaviour.energyconstrained.subbehaviours.behaviourchanges;

import agent.behaviour.BehaviourChange;
import agent.behaviour.autonomousbehaviour.DropPacket;

public class LoopDetectionChange extends BehaviourChange {

    boolean loopDetectionTriggered = false;

    @Override
    public boolean isSatisfied() {
        return loopDetectionTriggered;
    }

    @Override
    public void updateChange() {
        loopDetectionTriggered = Boolean.parseBoolean(getAgentImp().getMemoryFragment(DropPacket.LOOP_DETECTION_KEY));
        getAgentImp().removeMemoryFragment(DropPacket.LOOP_DETECTION_KEY);
    }
}
