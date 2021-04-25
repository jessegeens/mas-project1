package agent.behaviour.colored.subbehaviours.behaviourChanges;

import agent.AgentImp;
import agent.behaviour.BehaviourChange;
import environment.CellPerception;
import environment.Perception;
import environment.world.energystation.EnergyStationWorld;

public class ToChargeChange extends BehaviourChange {
    Perception agentPerception;
    int xAgent;
    int yAgent;

    @Override
    public boolean isSatisfied() {
        CellPerception perception = agentPerception.getCellPerceptionOnAbsPos(xAgent, yAgent + 1);
        if (perception != null && perception.containsEnergyStation()) {
            EnergyStationWorld energyStationWorld = getAgentImp().getEnvironment().getWorld(EnergyStationWorld.class);
            energyStationWorld.updateGradientField();
            return true;
        }
        return false;
    }

    @Override
    public void updateChange() {
        AgentImp agent = getAgentImp();
        xAgent = agent.getX();
        yAgent = agent.getY();
        agentPerception = agent.getPerception();
    }
}
