package agent.behaviour.droppacket.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import environment.CellPerception;
import environment.Item;
import environment.world.agent.Agent;
import environment.world.agent.AgentRep;
import environment.world.gradient.GradientRep;

import java.util.Vector;

public class MoveToChargingStation extends LTDBehaviour {

    @Override
    public void act(AgentImp agent) {
        CellPerception cell = agent.getCellWithBestGradient();
        System.out.println("x: " + cell.getX() + "   y: "+ cell.getY());
        agent.step(cell.getX(), cell.getY());
    }



    @Override
    public void communicate(AgentImp agent) {
        if (!agent.hasCriticalBatteryState()) {
            return;
        }
        CellPerception cell = agent.getPerception().getCellPerceptionOnRelPos(0, 0);
        if (cell.getGradientRepresentation().isPresent() && cell.getGradientRepresentation().get().getValue() == 1) {
            AgentRep other = findChargingAgent(agent);
            if (other != null) {
                agent.sendMessage(other, "Critical BatteryState");
            }
        }

    }

    private AgentRep findChargingAgent(AgentImp agent) {
        for (CellPerception neighbour: agent.getPerception().getNeighbours()) {
            if (neighbour.getGradientRepresentation().isPresent() && neighbour.getGradientRepresentation().get().getValue() == 0) {
                if (neighbour.getAgentRepresentation().isPresent()) {
                    AgentRep other = neighbour.getAgentRepresentation().get();
                    return other;
                }
            }
        }
        return null;
    }
}
