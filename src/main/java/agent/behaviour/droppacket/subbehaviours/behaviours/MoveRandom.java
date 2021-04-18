package agent.behaviour.droppacket.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import environment.Coordinate;
import environment.Mail;
import environment.world.agent.AgentRep;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static util.MemoryParser.parseColorFromMessage;
import static util.MemoryParser.parseCoordinateFromMessage;

public class MoveRandom extends LTDBehaviour {

    @Override
    public void act(AgentImp agent) {
        Coordinate coordinate = agent.generateRandomMove();
        if (coordinate != null)
            agent.step(coordinate.getX(), coordinate.getY());
        else
            agent.skip();
    }

    @Override
    public void communicate(AgentImp agent) {
        for (Mail mail: agent.getMessages()){
            String msg = mail.getMessage();
            if(msg.length() > 4 && msg.substring(0,4) == "dest"){
                System.out.println(agent.getName() + ": received a destination from " + mail.getFrom());
                String color = parseColorFromMessage(msg).toString();
                if (agent.getMemoryFragment(color) == null)
                    agent.addMemoryFragment(color, parseCoordinateFromMessage(msg).toString());
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

        // TODO send message to everyone about all your packets
    }


}
