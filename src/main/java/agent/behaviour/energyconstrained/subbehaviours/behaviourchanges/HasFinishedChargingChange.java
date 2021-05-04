package agent.behaviour.energyconstrained.subbehaviours.behaviourchanges;

import agent.behaviour.BehaviourChange;
import agent.behaviour.autonomousbehaviour.DropPacket;
import environment.world.agent.Agent;

public class HasFinishedChargingChange extends BehaviourChange {
    int batteryState;

    @Override
    public boolean isSatisfied() {
        if (batteryState > Agent.BATTERY_SAFE_MAX) {
            getAgentImp().removeMemoryFragment(DropPacket.MOVED_AWAY_KEY);
            return true;
        }
        return false;
    }

    @Override
    public void updateChange() {
        batteryState = getAgentImp().getBatteryState();
    }
}
