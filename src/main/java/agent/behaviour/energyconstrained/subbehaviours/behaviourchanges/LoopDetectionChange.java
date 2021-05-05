package agent.behaviour.energyconstrained.subbehaviours.behaviourchanges;

import agent.AgentImp;
import agent.behaviour.BehaviourChange;

/**
 * This behaviourChange checks if the `loop_detection` key is set in memory, and if so, it activates this change.
 * This makes the agent go to a MoveRandom behaviour so that he is not stuck anymore on a local minimum.
 */
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
