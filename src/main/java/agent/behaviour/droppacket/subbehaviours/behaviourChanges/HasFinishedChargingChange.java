package agent.behaviour.droppacket.subbehaviours.behaviourChanges;

import agent.behaviour.BehaviourChange;
import environment.world.agent.Agent;

public class HasFinishedChargingChange extends BehaviourChange {
    int batteryState;

    @Override
    public boolean isSatisfied() {
        return batteryState > Agent.BATTERY_SAFE_MAX;
    }

    @Override
    public void updateChange() {
        batteryState = getAgentImp().getBatteryState();
    }
}
