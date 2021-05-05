package agent.behaviour.energyconstrained.subbehaviours.behaviourchanges;

import agent.AgentImp;
import agent.behaviour.BehaviourChange;

public class LoopDetectionChange extends BehaviourChange {

    boolean loopDetectionTriggered = false;

    @Override
    public boolean isSatisfied() {
        return loopDetectionTriggered;
    }

    @Override
    public void updateChange() {
        loopDetectionTriggered = Boolean.parseBoolean(getAgentImp().getMemoryFragment(AgentImp.LOOP_DETECTION_KEY));
        getAgentImp().removeMemoryFragment(AgentImp.LOOP_DETECTION_KEY);
    }
}
