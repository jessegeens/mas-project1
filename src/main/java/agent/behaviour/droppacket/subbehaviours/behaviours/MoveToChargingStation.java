package agent.behaviour.droppacket.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import environment.CellPerception;
import environment.world.agent.Agent;
import environment.world.agent.AgentRep;
import util.CommunicateDropoff;

import java.util.ArrayList;

public class MoveToChargingStation extends LTDBehaviour {

    @Override
    public void act(AgentImp agent) {
        CellPerception cell = agent.getCellWithBestGradient();
        if(!cell.isWalkable()){
            agent.skip();
        } else {
            agent.step(cell.getX(), cell.getY());
        }
        //System.out.println("x: " + cell.getX() + "   y: "+ cell.getY());

    }



    @Override
    public void communicate(AgentImp agent) {
        //
        if (!agent.hasCriticalBatteryState() || agent.getBatteryState() < Agent.BATTERY_DECAY_STEP) {
            return;
        }
        System.out.println("communicate critical bat state");
        CellPerception cell = agent.getPerception().getCellPerceptionOnRelPos(0, 0);
        if (cell.getGradientRepresentation().isPresent() && cell.getGradientRepresentation().get().getValue() == 1) {
            AgentRep other = findChargingAgent(agent);
            //System.out.println("other = "+other);
            if (other != null) {
                //System.out.println("send comms");
                agent.sendMessage(other, Agent.CRITICAL_BATTERY_STATE_MESSAGE);
                System.out.println(agent.getName() + ": sent CRIT_BATT message to " + other.getName());
            }
        }
        CommunicateDropoff.communicateDropOff(agent);
    }

    private AgentRep findChargingAgent(AgentImp agent) {
        //System.out.println("findChargingAgent");
        for (CellPerception neighbour: agent.getPerception().getNeighbours()) {
            if (neighbour != null && neighbour.getGradientRepresentation().isPresent() && neighbour.getGradientRepresentation().get().getValue() == 0) {
                if (neighbour.getAgentRepresentation().isPresent()) {
                    System.out.println("return = "+neighbour.getAgentRepresentation().get());
                    return neighbour.getAgentRepresentation().get();
                }
            }
        }
        return null;
    }
}
