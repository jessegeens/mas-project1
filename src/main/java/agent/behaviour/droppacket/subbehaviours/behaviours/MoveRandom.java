package agent.behaviour.droppacket.subbehaviours.behaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import environment.Coordinate;
import environment.Mail;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        // TODO send message to everyone about all your packets
    }

    Color parseColorFromMessage(String msg){
        return Color.getColor(msg.split(";")[1]);
    }

    Coordinate parseCoordinateFromMessage(String msg){
        return Coordinate.fromString(msg.split(";")[2]);
    }


}
