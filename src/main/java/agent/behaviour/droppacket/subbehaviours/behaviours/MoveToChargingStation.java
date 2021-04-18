package agent.behaviour.droppacket.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import environment.CellPerception;
import environment.Coordinate;
import environment.Item;
import environment.Mail;
import environment.world.agent.Agent;
import environment.world.agent.AgentRep;
import environment.world.gradient.GradientRep;
import util.MemoryParser;

import java.awt.*;
import java.util.List;
import java.util.Vector;

public class MoveToChargingStation extends LTDBehaviour {

    @Override
    public void act(AgentImp agent) {
        CellPerception cell = agent.getCellWithBestGradient();
        //System.out.println("x: " + cell.getX() + "   y: "+ cell.getY());
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
                agent.sendMessage(other, Agent.CRITICAL_BATTERY_STATE_MESSAGE);
                //System.out.println("Sent message to: " + other.getName());

            }
        }
        for (Mail mail: agent.getMessages()){
            String msg = mail.getMessage();
            if(msg.length() > 4 && msg.substring(0,4) == "dest"){
                System.out.println(agent.getName() + ": received a destination from " + mail.getFrom());
                String color = MemoryParser.parseColorFromMessage(msg).toString();
                if (agent.getMemoryFragment(color) == null)
                    agent.addMemoryFragment(color, MemoryParser.parseCoordinateFromMessage(msg).toString());
            }
        }
        List<AgentRep> agents = agent.getPerception().findNearbyAgents();
        if(agent.getMemoryFragment("colors") != null){
            for(AgentRep receiver: agents){
                System.out.println(agent.getName() + ": notifying " + receiver.getName() + " about location of colors");
                agent.getPerception();
                String[] colors = agent.getMemoryFragment("colors").split(";");
                for(String color : colors) {
                    String coord = agent.getMemoryFragment(color);
                    agent.sendMessage(receiver, "dest;" + color + ";" + coord);
                }
            }
        }
        //agent.closeCommunication();

    }

    private AgentRep findChargingAgent(AgentImp agent) {
        for (CellPerception neighbour: agent.getPerception().getNeighbours()) {
            if (neighbour != null && neighbour.getGradientRepresentation().isPresent() && neighbour.getGradientRepresentation().get().getValue() == 0) {
                if (neighbour.getAgentRepresentation().isPresent()) {
                    return neighbour.getAgentRepresentation().get();
                }
            }
        }
        return null;
    }
}
