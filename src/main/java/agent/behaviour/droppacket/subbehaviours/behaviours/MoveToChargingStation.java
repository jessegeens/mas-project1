package agent.behaviour.droppacket.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import agent.behaviour.droppacket.DropPacket;
import environment.CellPerception;
import environment.Coordinate;
import environment.Mail;
import environment.world.agent.Agent;
import environment.world.agent.AgentRep;
import util.CommunicateDropoff;

import java.util.ArrayList;

public class MoveToChargingStation extends LTDBehaviour {

    @Override
    public void act(AgentImp agent) {
        CellPerception cell = agent.getCellWithBestGradient();
        CellPerception currentCell = agent.getPerception().getCellPerceptionOnRelPos(0, 0);

        // In case the agent is stuck between an other agent and cant move anywhere
        if(cell == null){
            agent.skip();
            return;
        }

        // this if-statement prevents agents from walking around while being next to the charging station
        if (currentCell.getGradientRepresentation().isPresent() && currentCell.getGradientRepresentation().get().getValue() == 1
            && cell.getGradientRepresentation().isPresent() && cell.getGradientRepresentation().get().getValue() != 0){
            agent.skip(); // if the agent is on gradient 1 but gradient 0 is currently occupied.
            return;
        }

        // If another agent is waiting with critical battery, don't take his place
        if ( !(agent.hasCriticalBatteryState()) && currentCell.getGradientRepresentation().isPresent() && currentCell.getGradientRepresentation().get().getValue() == 1){
            ArrayList<Mail> toRemove = new ArrayList<Mail>();
           for(Mail mail : agent.getMessages()){
               if(mail.getMessage().equals(Agent.CRITICAL_BATTERY_STATE_MESSAGE)){
                 toRemove.add(mail);
               }
           }
           if (!toRemove.isEmpty()){
               agent.removeMessages(toRemove);
               agent.skip();
               System.out.println("Skipping turn to give priority to agent with crit battery state");
               return;
           }
        }

        if(!cell.isWalkable()){
            agent.skip();
        } else {

            Coordinate newCoord = new Coordinate(cell.getX(), cell.getY());
            if (agent.getLastArea() != null && newCoord.equalsCoordinate(new Coordinate(agent.getLastArea().getX(), agent.getLastArea().getY()))) {
                System.out.println("Loop detection triggered[to battery]: " + agent.getID());
                agent.addMemoryFragment(DropPacket.LOOP_DETECTION_KEY, "true");
            }
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
            /*AgentRep other = findChargingAgent(agent);
            //System.out.println("other = "+other);
            if (other != null) {
                //System.out.println("send comms");
                agent.sendMessage(other, Agent.CRITICAL_BATTERY_STATE_MESSAGE);
                System.out.println(agent.getName() + ": sent CRIT_BATT message to " + other.getName());
            }*/
            agent.broadcastMessage(Agent.CRITICAL_BATTERY_STATE_MESSAGE);
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
