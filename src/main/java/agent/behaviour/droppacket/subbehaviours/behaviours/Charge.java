package agent.behaviour.droppacket.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import agent.behaviour.droppacket.DropPacket;
import environment.Coordinate;
import environment.Mail;
import environment.world.agent.Agent;
import util.CommunicateDropoff;

import java.util.ArrayList;

public class Charge extends LTDBehaviour {

    boolean hasToMove;

    @Override
    public void act(AgentImp agent) {
        if (!hasToMove) agent.skip();
        else {
            Coordinate coordinate = agent.generateRandomMove();
            if (coordinate != null) {
                agent.step(coordinate.getX(), coordinate.getY());
                agent.addMemoryFragment(DropPacket.MOVED_AWAY_KEY, "true");
            } else agent.skip();
        }
    }

    @Override
    public void communicate(AgentImp agent) {
        ArrayList<Mail> toDelete = new ArrayList<>();
        hasToMove = false;
        CommunicateDropoff.communicateDropOff(agent);
        for (Mail mail: agent.getMessages()){
            if (mail.getMessage().equals(Agent.CRITICAL_BATTERY_STATE_MESSAGE)) {
                if (! agent.hasCriticalBatteryState()) hasToMove = true;
                toDelete.add(mail);
            }
        }
        agent.removeMessages(toDelete);
    }
}
