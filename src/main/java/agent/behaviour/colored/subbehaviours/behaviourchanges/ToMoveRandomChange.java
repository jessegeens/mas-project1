package agent.behaviour.colored.subbehaviours.behaviourchanges;

import agent.behaviour.BehaviourChange;

/**
 * The behaviour change when the agent should move randomly.
 */
public class ToMoveRandomChange extends BehaviourChange {

    /**
     * An agent can always start to move randomly.
     * @return Returns true.
     */
    @Override
    public boolean isSatisfied() {
        return true; //No specific condition but should get the lowest priority.
    }

    @Override
    public void updateChange() {

    }
}
