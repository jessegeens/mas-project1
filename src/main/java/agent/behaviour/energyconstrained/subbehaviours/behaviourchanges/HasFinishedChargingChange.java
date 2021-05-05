package agent.behaviour.energyconstrained.subbehaviours.behaviourchanges;

import agent.AgentImp;
import agent.behaviour.BehaviourChange;
import environment.world.agent.Agent;

/**
 * This behaviour change is activated when an agent has enough battery, and he doesn't need to charge anymore.
 */
public class HasFinishedChargingChange extends BehaviourChange {
    int batteryState;

    @Override
    public boolean isSatisfied() {
        if (batteryState > Agent.BATTERY_SAFE_MAX) {
            getAgentImp().removeMemoryFragment(AgentImp.MOVED_AWAY_KEY);
            return true;
        }
        return false;
    }

    @Override
    public void updateChange() {
        batteryState = getAgentImp().getBatteryState();
    }
}
