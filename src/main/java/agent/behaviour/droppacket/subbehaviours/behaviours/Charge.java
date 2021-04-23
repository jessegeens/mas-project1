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
        if (!hasToMove){
            // Charge
            agent.skip();
        }else{
            Coordinate coordinate = agent.generateRandomMove();
            if (coordinate != null){
                agent.step(coordinate.getX(), coordinate.getY());
                agent.addMemoryFragment(DropPacket.MOVED_AWAY_KEY, "true");

            }else
                agent.skip();
        }
    }

    @Override
    public void communicate(AgentImp agent) {
        ArrayList<Mail> toDelete = new ArrayList<>();
        hasToMove = false;
        System.out.println("mails before = "+agent.getMessages().size());
        CommunicateDropoff.communicateDropOff(agent);
        //System.out.println("mails after = "+mails.size());
        for (Mail mail: agent.getMessages()){
            System.out.println("mail " + mail.getMessage());
            if (mail.getMessage().equals(Agent.CRITICAL_BATTERY_STATE_MESSAGE)) {
                //System.out.println("received comms");
                if (! agent.hasCriticalBatteryState())
                    hasToMove = true;
                //    System.out.println("received Message from: " + mail.getFrom() + "and will move away :" + mail.getTo());
                toDelete.add(mail);
            }
        }
        agent.removeMessages(toDelete);
        //System.out.println("blub" + agent.getID());

    }
}
