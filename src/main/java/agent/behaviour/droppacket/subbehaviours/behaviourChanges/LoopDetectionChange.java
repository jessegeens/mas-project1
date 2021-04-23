package agent.behaviour.droppacket.subbehaviours.behaviourChanges;

import agent.behaviour.BehaviourChange;
import agent.behaviour.droppacket.DropPacket;

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
