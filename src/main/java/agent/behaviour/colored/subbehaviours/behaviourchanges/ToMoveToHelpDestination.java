package agent.behaviour.colored.subbehaviours.behaviourchanges;

import agent.AgentImp;
import agent.behaviour.BehaviourChange;
import agent.behaviour.colored.CoordinateQueue;

/**
 * The behaviour change when the agent should start to move towards a help destination to help
 * another agent.
 */
public class ToMoveToHelpDestination extends BehaviourChange {

    /**
     * True if the agent has a help destination.
     */
    private boolean hasHelpDestination = false;

    /**
     * True if the agent has a help destination and no carry, false otherwise
     */
    @Override
    public boolean isSatisfied() {
        return hasHelpDestination && !getAgentImp().hasCarry();
    }

    /**
     * Checks whether the agent has a help queue entry and puts the first entry of this queue
     * in his destination if necessary.
     */
    @Override
    public void updateChange() {
        String firstFromHelpQueue = CoordinateQueue.getFirst(getAgentImp());
        hasHelpDestination = firstFromHelpQueue != null;
        if (hasHelpDestination && !getAgentImp().hasCarry()) {
            getAgentImp().addMemoryFragment(AgentImp.DESTINATION_KEY, firstFromHelpQueue);
        }
    }
}
