package util;

import agent.AgentImp;
import environment.Coordinate;
import environment.Mail;
import environment.world.agent.AgentRep;

import java.awt.*;
import java.util.List;

public class CommunicateDropoff {
    public static void communicateDropOff(AgentImp agent, List<Mail> mails){
        for (Mail mail: mails){
            String msg = mail.getMessage();
            //System.out.println(agent.getName() + ": received " + msg);
            if(msg.length() > 4 && msg.substring(0,4).equals("dest")){

                String color = parseColorFromMessage(msg);
                if (agent.getMemoryFragment(color) == null){
                    System.out.println(agent.getName() + ": received a destination from " + mail.getFrom() + " for color " + color);
                    agent.addMemoryFragment(color, parseCoordinateFromMessage(msg));
                }

            }
        }
        List<AgentRep> agents = agent.getPerception().findNearbyAgents();
        if(agent.getMemoryFragment("colors") != null){
            for(AgentRep receiver: agents){
                String[] colors = agent.getMemoryFragment("colors").split(";");
                for(String color : colors) {
                    String coord = agent.getMemoryFragment(color);
                    agent.sendMessage(receiver, "dest;" + color + ";" + coord);
                }
            }
        }
    }

    private static String parseColorFromMessage(String msg){
        //System.out.println("Parsing color from " + msg.split(";")[1]);
        return msg.split(";")[1];
    }

    private static String parseCoordinateFromMessage(String msg){
        return msg.split(";")[2];
    }

}
