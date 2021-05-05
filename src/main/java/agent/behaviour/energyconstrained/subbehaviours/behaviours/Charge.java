package agent.behaviour.energyconstrained.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import environment.Coordinate;
import environment.Mail;
import environment.world.agent.Agent;
import agent.behaviour.energyconstrained.CommunicateDropoff;

import java.util.ArrayList;

public class Charge extends LTDBehaviour {

    boolean hasToMove;

    @Override
    public void act(AgentImp agent) {
        if (!hasToMove) agent.skip();
        else {
            // If the agent has to move away, remember this so the agent can come back
            Coordinate coordinate = agent.generateRandomMove();
            if (coordinate != null) {
                agent.step(coordinate.getX(), coordinate.getY());
                agent.addMemoryFragment(AgentImp.MOVED_AWAY_KEY, "true");
            } else agent.skip();
        }
    }

    // here, we check if the agent needs to move away because an agent with a critical battery state is nearby
    @Override
    public void communicate(AgentImp agent) {
        ArrayList<Mail> toDelete = new ArrayList<>();
        hasToMove = false;
        CommunicateDropoff.communicateDropOff(agent);
        for (Mail mail: agent.getMessages()) {
            if (mail.getMessage().equals(Agent.CRITICAL_BATTERY_STATE_MESSAGE)) {
                if (! agent.hasCriticalBatteryState()) hasToMove = true;
                toDelete.add(mail);
            }
        }
        agent.removeMessages(toDelete);
    }
}
