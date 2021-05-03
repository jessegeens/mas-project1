package agent.behaviour.colored;

import agent.AgentImp;
import environment.Coordinate;
import environment.Mail;
import environment.world.agent.AgentRep;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CommunicateHelp {

    public static void manageHelp(AgentImp agent) {
        receiveHelpMessage(agent);
        receiveAcknowledge(agent);
        askForHelp(agent);
    }

    public static void receiveHelpMessage(AgentImp agent) {
        ArrayList<Mail> toDelete = new ArrayList<>();
        for (Mail mail: agent.getMessages()) {
            String msg = mail.getMessage();
            if (msg.length() > 4 && msg.substring(0, 4).equals("help")) {
                if (parseColorFromMessage(msg).equals(agent.getAgentColor().toString())) {
                    //print(agent, "receive help");
                    CoordinateQueue.addCoordinate(agent, parseCoordinateFromMessage(msg));
                    List<AgentRep> agents = agent.getPerception().findNearbyAgents();
                    for (AgentRep receiver: agents) {
                        if (String.valueOf(receiver.getID()).equals(mail.getFrom())) {
                            acknowledge(agent, receiver);
                        }
                    }
                }
                toDelete.add(mail);
            }
        }
        agent.removeMessages(toDelete);
    }

    public static void askForHelp(AgentImp agent) {
        if (agent.getMemoryFragment(AgentImp.HELP_MESSAGE_KEY) != null) {
            List<AgentRep> agents = agent.getPerception().findNearbyAgents();
            for (AgentRep receiver: agents) {
                String msg = agent.getMemoryFragment(AgentImp.HELP_MESSAGE_KEY);
                //print(agent, "ask for help: " + msg);
                agent.sendMessage(receiver, msg);
            }
        }
    }

    public static void acknowledge(AgentImp sender, AgentRep receiver) {
       // print(sender, "send ack");
        sender.sendMessage(receiver, "acknowledge");
    }

    public static void receiveAcknowledge(AgentImp agent) {
        ArrayList<Mail> toDelete = new ArrayList<>();
        for (Mail mail: agent.getMessages()) {
            String msg = mail.getMessage();
            if (msg.equals("acknowledge")) {
                print(agent, "receive ack");
                agent.removeMemoryFragment(AgentImp.HELP_MESSAGE_KEY);
                toDelete.add(mail);
            }
        }
        agent.removeMessages(toDelete);
    }

    private static String parseColorFromMessage(String msg) {
        //System.out.println("parse color: "+msg);;
        return msg.split(";")[1];
    }

    private static String parseCoordinateFromMessage(String msg) {
        //System.out.println("parse coordinate: "+msg);;
        return msg.split(";")[2];
    }

    public static String constructHelpMessage(Coordinate coordinate, Color color) {
        return "help;" + color.toString() + ";" + coordinate.toString();
    }

    private static void print(AgentImp agent, String msg) {
        System.out.println("agent " + agent.getName() + ": " + msg);
    }
}
