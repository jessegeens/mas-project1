package agent.behaviour.droppacket.subbehaviours.behaviourChanges;

import agent.AgentImp;
import agent.behaviour.BehaviourChange;
import environment.CellPerception;
import environment.Perception;

public class ToChargeChange extends BehaviourChange {
    Perception agentPerception;
    int xAgent;
    int yAgent;

    @Override
    public boolean isSatisfied() {
        CellPerception perception = agentPerception.getCellPerceptionOnAbsPos(xAgent, yAgent + 1);
        return perception != null && perception.containsEnergyStation();
    }

    @Override
    public void updateChange() {
        AgentImp agent = getAgentImp();
        xAgent = agent.getX();
        yAgent = agent.getY();
        agentPerception = agent.getPerception();
    }
}
