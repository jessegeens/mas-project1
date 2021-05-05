package agent.behaviour.energyconstrained.subbehaviours.behaviourchanges;

import agent.behaviour.BehaviourChange;

/**
 * This behaviour change makes an agent move randomly. It is always true because we give it the lowest priority.
 */
public class ToMoveRandomChange extends BehaviourChange {
    @Override
    public boolean isSatisfied() {
        return true; //No specific condition but should get the lowest priority.
    }

    @Override
    public void updateChange() {

    }
}
