package agent.behaviour.energyconstrained.subbehaviours.behaviourchanges;

import agent.behaviour.BehaviourChange;

public class ToMoveRandomChange extends BehaviourChange {
    @Override
    public boolean isSatisfied() {
        return true; //No specific condition but should get the lowest priority.
    }

    @Override
    public void updateChange() {

    }
}
