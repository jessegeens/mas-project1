package agent.behaviour.colored.subbehaviours.behaviourChanges;

import agent.AgentImp;
import agent.behaviour.BehaviourChange;
import agent.behaviour.colored.CoordinateQueue;
import environment.Coordinate;

public class ToMoveToHelpDestination extends BehaviourChange {

    private boolean hasHelpDestination = false;

    @Override
    public boolean isSatisfied() {
        return hasHelpDestination && !getAgentImp().hasCarry();
    }

    @Override
    public void updateChange() {
        String firstFromHelpQueue = CoordinateQueue.getFirst(getAgentImp());
        hasHelpDestination = firstFromHelpQueue != null;
        if (hasHelpDestination && !getAgentImp().hasCarry()) {
            getAgentImp().addMemoryFragment(AgentImp.DESTINATION_KEY, firstFromHelpQueue);
        }
    }
}
