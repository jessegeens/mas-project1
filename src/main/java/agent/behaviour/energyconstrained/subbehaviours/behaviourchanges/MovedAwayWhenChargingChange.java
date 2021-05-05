package agent.behaviour.energyconstrained.subbehaviours.behaviourchanges;

import agent.AgentImp;
import agent.behaviour.BehaviourChange;
import environment.world.agent.Agent;

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
