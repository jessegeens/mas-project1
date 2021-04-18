package agent.behaviour.droppacket.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import agent.behaviour.droppacket.DropPacket;
import environment.Coordinate;
import environment.Mail;
import environment.world.agent.Agent;

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
        hasToMove = false;
        for (Mail mail: agent.getMessages()){
            System.out.println("mail " + mail.getMessage());
            if (mail.getMessage().equals(Agent.CRITICAL_BATTERY_STATE_MESSAGE) && !agent.hasCriticalBatteryState()){
                hasToMove = true;
                System.out.println("received Message from: " + mail.getFrom() + "and will move away :" + mail.getTo());
            }
        }
        //System.out.println("blub" + agent.getID());

    }
}
