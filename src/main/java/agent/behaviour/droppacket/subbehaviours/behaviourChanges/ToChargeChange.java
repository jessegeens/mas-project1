package agent.behaviour.droppacket.subbehaviours.behaviourChanges;

import agent.AgentImp;
import agent.behaviour.BehaviourChange;
import environment.Perception;

public class ToChargeChange extends BehaviourChange {
    Perception agentPerception;
    int xAgent;
    int yAgent;

    @Override
    public boolean isSatisfied() {
        return agentPerception.getCellPerceptionOnAbsPos(xAgent, yAgent + 1).containsEnergyStation();
    }

    @Override
    public void updateChange() {
        AgentImp agent = getAgentImp();
        xAgent = agent.getX();
        yAgent = agent.getY();
        agentPerception = agent.getPerception();
    }
}
