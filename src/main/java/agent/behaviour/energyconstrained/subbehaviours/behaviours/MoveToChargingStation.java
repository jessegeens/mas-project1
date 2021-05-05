package agent.behaviour.energyconstrained.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import environment.CellPerception;
import environment.Coordinate;
import environment.Mail;
import environment.world.agent.Agent;
import agent.behaviour.energyconstrained.CommunicateDropoff;

import java.util.ArrayList;

/**
 * This behaviour directs an agent towards a charging station, using the gradients to guide his path
 */
public class MoveToChargingStation extends LTDBehaviour {

    @Override
    public void act(AgentImp agent) {
        CellPerception cell = agent.getCellWithBestGradient();
        CellPerception currentCell = agent.getPerception().getCellPerceptionOnRelPos(0, 0);

        // In case the agent is stuck between an other agent and cant move anywhere
        if (cell == null) {
            agent.skip();
            return;
        }

        // this if-statement prevents agents from walking around while being next to the charging station
        if (currentCell.getGradientRepresentation().isPresent() && currentCell.getGradientRepresentation().get().getValue() == 1
            && cell.getGradientRepresentation().isPresent() && cell.getGradientRepresentation().get().getValue() != 0) {
            agent.skip(); // if the agent is on gradient 1 but gradient 0 is currently occupied.
            return;
        }

        // If another agent is waiting with critical battery, don't take his place
        if ( !(agent.hasCriticalBatteryState()) && currentCell.getGradientRepresentation().isPresent() && currentCell.getGradientRepresentation().get().getValue() == 1) {
            ArrayList<Mail> toRemove = new ArrayList<>();
           for (Mail mail : agent.getMessages()) {
               if (mail.getMessage().equals(Agent.CRITICAL_BATTERY_STATE_MESSAGE))
                 toRemove.add(mail);
           }
           if (!toRemove.isEmpty()) {
               agent.removeMessages(toRemove);
               agent.skip();
               return;
           }
        }

        if (!cell.isWalkable())
            agent.skip();
        else {
            // Move towards the cell with the best gradient
            Coordinate newCoord = new Coordinate(cell.getX(), cell.getY());
            if (agent.getLastArea() != null && newCoord.equalsCoordinate(new Coordinate(agent.getLastArea().getX(), agent.getLastArea().getY()))) {
                agent.addMemoryFragment(AgentImp.LOOP_DETECTION_KEY, "true");
            }
            agent.step(cell.getX(), cell.getY());
        }
    }

    /**
     * If the agent has a critical battery state, the agent has not died, and if he is next to the charging station,
     * then the agent broadcasts a critical battery message to the agent that is currently charging. This makes the other
     * agent move out the way so that this agent does not "die"
     */
    @Override
    public void communicate(AgentImp agent) {
        CommunicateDropoff.communicateDropOff(agent);
        if (!agent.hasCriticalBatteryState() || agent.getBatteryState() < Agent.BATTERY_DECAY_STEP) return;
        CellPerception cell = agent.getPerception().getCellPerceptionOnRelPos(0, 0);
        if (cell.getGradientRepresentation().isPresent() && cell.getGradientRepresentation().get().getValue() == 1)
            agent.broadcastMessage(Agent.CRITICAL_BATTERY_STATE_MESSAGE);
    }
}
