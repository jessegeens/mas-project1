package agent.behaviour.energyconstrained.subbehaviours.behaviourchanges;

import agent.AgentImp;
import agent.behaviour.BehaviourChange;
import environment.Coordinate;

public class ToPickPacketChange extends BehaviourChange {
    private Coordinate destination = null;
    private boolean hasCarry = false;

    @Override
    public boolean isSatisfied() {
        //TODO: waarom destination soms null? zouden we niet beter opnemen vanaf dat we packetje tegenkomen en dan gwn destination op null zetten?
        return !hasCarry && destination != null && getAgentImp().isNeighbour(destination);
    }

    @Override
    public void updateChange() {
        hasCarry = getAgentImp().hasCarry();
        destination = Coordinate.fromString(getAgentImp().getMemoryFragment(AgentImp.DESTINATION_KEY));
    }
}
