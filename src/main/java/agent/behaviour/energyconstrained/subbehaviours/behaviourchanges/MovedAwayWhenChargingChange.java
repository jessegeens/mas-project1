package agent.behaviour.energyconstrained.subbehaviours.behaviourchanges;

import agent.AgentImp;
import agent.behaviour.BehaviourChange;
import environment.world.agent.Agent;

/**
 * This behaviour change checks in the agent's memory if the agent previously moved away from a charging point
 * because another agent nearby had a critical battery state. If that's the case, and the agent does not yet have sufficient
 * battery, he should move back when he is able to.
 */
public class MovedAwayWhenChargingChange extends BehaviourChange {

    int batteryState;
    boolean movedAway;

    @Override
    public boolean isSatisfied() {
        if (batteryState < Agent.BATTERY_SAFE_MAX && movedAway) {
            getAgentImp().removeMemoryFragment(AgentImp.MOVED_AWAY_KEY);
            return true;
        }
        return false;
    }

    @Override
    public void updateChange() {
        batteryState = getAgentImp().getBatteryState();
        movedAway = Boolean.parseBoolean(getAgentImp().getMemoryFragment(AgentImp.MOVED_AWAY_KEY));
    }
}
