package agent.behaviour.colored.subbehaviours.behaviourChanges;

import agent.behaviour.BehaviourChange;
import agent.behaviour.droppacket.DropPacket;
import environment.Coordinate;
import environment.world.agent.Agent;

public class MovedAwayWhenChargingChange extends BehaviourChange {

    int batteryState;
    boolean movedAway;

    @Override
    public boolean isSatisfied() {
        if (batteryState < Agent.BATTERY_SAFE_MAX && movedAway){
            getAgentImp().removeMemoryFragment(DropPacket.MOVED_AWAY_KEY);
            return true;
        }
        return false;
    }

    @Override
    public void updateChange() {
        batteryState = getAgentImp().getBatteryState();
        movedAway = Boolean.parseBoolean(getAgentImp().getMemoryFragment(DropPacket.MOVED_AWAY_KEY));
    }
}
